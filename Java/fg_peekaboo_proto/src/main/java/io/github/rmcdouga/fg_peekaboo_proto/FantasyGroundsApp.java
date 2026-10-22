package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.rmcdouga.fg_peekaboo_proto.FantasyGroundsApp.FantasyGroundsProcess.FantasyGroundsTab;
import io.github.rmcdouga.fg_peekaboo_proto.PeekabooClient.SeeInfo;
import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonBoundingBox;
import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonClickLocation;


public class FantasyGroundsApp {
	private static final Logger log = LoggerFactory.getLogger(FantasyGroundsApp.class);

	private static final String FG_PROCESS_NAME = "SmiteWorks"; // The name of the Fantasy Grounds process should contain this
	private static final String FG_BUNDLE_ID = "unity.SmiteWorks.Fantasy Grounds"; // The bundle identifier for Fantasy Grounds (macOS)

	private final PeekabooClient peekabooClient;
	private final UiLocator uiLocator;
	private final Supplier<Path> screenshotDirPathFactory;
	
	public FantasyGroundsApp(PeekabooClient peekabooClient, UiLocator uiLocator, Supplier<Path> screenshotPathFactory) {
		this.peekabooClient = peekabooClient;
		this.uiLocator = uiLocator;
		this.screenshotDirPathFactory = screenshotPathFactory;
	}


	/**
	 * Finds the process ID of an existing Fantasy Grounds application.
	 * 
	 * @return
	 */
	static OptionalLong processId() {

        List<ProcessHandle> smiteworksProcesses = ProcessHandle.allProcesses()
        													   .filter(ph -> ph.info().command().isPresent())
        													   .filter(ph -> ph.info().command().get().contains(FG_PROCESS_NAME))
        													   .toList();
        return switch(smiteworksProcesses.size()) {
			case 0 -> OptionalLong.empty();
			case 1 -> OptionalLong.of(smiteworksProcesses.getFirst().pid());
			default -> throw new IllegalStateException("Multiple (%d) Fantasy Grounds processes found:".formatted(smiteworksProcesses.size()));
        };
	}
	
	public class FantasyGroundsProcess {
		private final long processId;

		private FantasyGroundsProcess(long processId) {
			this.processId = processId;
		}
		
		// Utility routines used by all the screens to navigate the Fantasy Grounds UI.  
		// These routines will use the Peekaboo client to locate buttons and click on them.
		
		private SeeInfo captureScreenshot() {
			return peekabooClient.see(PeekabooClient.AppTarget.ProcessTarget.of(processId), screenshotDirPathFactory.get());
		}

		private void clickOnButton(String buttonText) {
			ButtonClickLocation updateButtonLoc = locateButton(buttonText).orElseThrow(()->new FantasyGroundsAppException("No 'UPDATE AVAILABLE' button found in Fantasy Grounds tab."));
			peekabooClient.move(PeekabooClient.AppTarget.ProcessTarget.of(processId), updateButtonLoc.x(), updateButtonLoc.y());
			peekabooClient.click(processId, updateButtonLoc.x(), updateButtonLoc.y());
		}			

		private Optional<ButtonClickLocation> locateButton(String buttonText) {
			var seeInfo = captureScreenshot();
			Optional<ButtonBoundingBox> buttonLoc = uiLocator.locateButton(seeInfo.screenshotImage(), buttonText);
			log.atInfo()
			   .addArgument(buttonText)
			   .addArgument(buttonLoc.toString())
			   .log("FantasyGroundsTab.locateButton({}) - buttonLoc: {}");
			
			Optional<ButtonClickLocation> buttonClickLoc = buttonLoc
					.map(box -> ButtonClickLocation.fromButtonBoundingBox(box, seeInfo.imageWidth(), seeInfo.imageHeight()))	// Calculate location from bounding box
					;
			log.atInfo()
			   .addArgument(buttonText)
			   .addArgument(buttonClickLoc.toString())
			   .log("FantasyGroundsTab.locateButton({}) - buttonClickLoc: {}");
			
			return buttonClickLoc;
		}

		private Optional<SeeInfo> waitForScreenWithText(String requiredTexts, Duration waitBetweenRetries, int maxRetries) throws InterruptedException {
			return waitForScreenWithTexts(List.of(requiredTexts), waitBetweenRetries, maxRetries);
		}
		
		private Optional<SeeInfo> waitForScreenWithTexts(Collection<String> requiredTexts, Duration waitBetweenRetries, int maxRetries) throws InterruptedException {
			return waitForCondition(new ConditionChecker<>() {
																SeeInfo seeInfo;
																@Override
																public boolean checkCondition() {
																	seeInfo = captureScreenshot();
																	return uiLocator.doesScreenshotContainTexts(seeInfo.screenshotImage(), requiredTexts);
																}
																@Override
																public SeeInfo returnValue() {
																	log.atInfo()
																	   .addArgument(requiredTexts.toString())
																	   .log("FantasyGroundsProcess.waitForScreenWithTexts() - Found required texts: {}");
																	return seeInfo;
																}
															}, 
									 waitBetweenRetries, 
									 maxRetries);
			}
		
		private interface ConditionChecker<T>  {
			boolean checkCondition();
			T returnValue();
		}

		private Optional<FantasyGroundsProcess> waitForNewFgProcess(Duration waitBetweenRetries, int maxRetries) throws InterruptedException {
			return waitForCondition(new ConditionChecker<>() {
																long newProcessId;
																@Override
																public boolean checkCondition() {
																	OptionalLong newProcessIdOpt = processId();
																	// If there is no new process or the process ID is the same as the current one, return false to indicate that the condition is not met.
																	if (newProcessIdOpt.isEmpty() || newProcessIdOpt.getAsLong() == processId) {
																		return false;
																	}
																	newProcessId = newProcessIdOpt.getAsLong();
																	return true;
																}
																@Override
																public FantasyGroundsProcess returnValue() {
																	log.atInfo()
																	   .addArgument(newProcessId)
																	   .log("FantasyGroundsProcess.waitForNewProcess() - Found new process ID: {}");
																	return new FantasyGroundsProcess(newProcessId);
																}
															}, 
									waitBetweenRetries, 
									maxRetries);
		}
		
		private <T> Optional<T> waitForCondition(ConditionChecker<T> checker, Duration waitBetweenRetries, int maxRetries) throws InterruptedException {
			for (int i = 0; i < maxRetries; i++) {
				if (checker.checkCondition()) {
					return Optional.of(checker.returnValue());
				}
				Thread.sleep(waitBetweenRetries);
			}
			return Optional.empty();
		}

		/**
		 * This is the initial window that is created when the Fantasy Grounds process is launched. 
		 * It contains the FantasyGrounds tab, the My Campaigns tab and the Join a Game tab.
		 * 
		 */
		public static sealed interface InitialWindow {
			public FantasyGroundsTab fantasyGroundsTab();
			public MyCampaignsTab myCampaignsTab();
			public JoinAGameTab joinAGameTab();
			
		}

		public final class FantasyGroundsTab extends InitialWindowImpl implements InitialWindow {

			private static final String OK_BUTTON_TEXT = "OK";
			private static final String UPDATE_BUTTON_TEXT = "UPDATE AVAILABLE";

			Boolean updateAvailable() {
				Optional<ButtonClickLocation> buttonLoc = locateButton(UPDATE_BUTTON_TEXT);
				return buttonLoc.isPresent();
			}

			FantasyGroundsTab performUpdate() throws FantasyGroundsAppException, InterruptedException {
				log.atInfo().log("Proceeding with update.");
				clickOnButton(UPDATE_BUTTON_TEXT);
				
				// After clicking the "UPDATE AVAILABLE" button, we will need to confirm.
				waitForScreenWithText(OK_BUTTON_TEXT, Duration.ofSeconds(1), 10)
					.orElseThrow(()->new FantasyGroundsAppException("No 'OK' button found after clicking 'UPDATE AVAILABLE' button in Fantasy Grounds tab."));
				
				// Implement a wait a set of text strings to appear on screen.  If it's not there, then wait some time and try again.
				// Implement a timeout of a number of seconds or a number of retries.
				// call should be wait(Collection<String> requiredTexts, Duration waitBetweenRetries, int maxRetries);
		
//				try {
//					Thread.sleep(Duration.ofSeconds(1));	// Should think of better way to wait for dialog to appear.
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//					throw new FantasyGroundsAppException("Interrupted while waiting for update to complete.", e);
//				}
				
				
				clickOnButton(OK_BUTTON_TEXT);
				
				FantasyGroundsProcess updatedFgProcess = waitForNewFgProcess(Duration.ofSeconds(20), 10)
																.orElseThrow(()->new FantasyGroundsAppException("No new Fantasy Grounds process found after clicking 'OK' button in Fantasy Grounds tab."));
				
				// This may involve waiting for a new window to appear or for the application to restart. 
				// For now, we will just return null.
				log.atInfo().log("Update is complete.");

				return updatedFgProcess.new FantasyGroundsTab();
			}

		}

		public final class MyCampaignsTab extends InitialWindowImpl implements InitialWindow {
			
		}

		public final class JoinAGameTab extends InitialWindowImpl implements InitialWindow {
			
		}


		private sealed abstract class InitialWindowImpl implements InitialWindow {
			public FantasyGroundsTab fantasyGroundsTab() {
				return new FantasyGroundsTab();
			}
			public MyCampaignsTab myCampaignsTab() {
				return new MyCampaignsTab();
				
			}
			public JoinAGameTab joinAGameTab() {
				return new JoinAGameTab();
			}
			
		}				
	}
	
	public FantasyGroundsTab startProcess() {
		// Launch the Fantasy Grounds process using the Peekaboo client
		long result = peekabooClient.appLaunch(FG_BUNDLE_ID);
		var fgProcess = new FantasyGroundsProcess(result);

		return fgProcess.new FantasyGroundsTab();
	}
	
	@SuppressWarnings("serial")
	public static class FantasyGroundsAppException extends RuntimeException {
		public FantasyGroundsAppException(String message) {
			super(message);
		}
		
		public FantasyGroundsAppException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
