package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.rmcdouga.fg_peekaboo_proto.FantasyGroundsApp.FantasyGroundsProcess.FantasyGroundsTab;
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

			Boolean updateAvailable() {
				Optional<ButtonClickLocation> buttonLoc = locateUpdateAvailableButton();
				return buttonLoc.isPresent();
			}

			FantasyGroundsTab performUpdate() {
				ButtonClickLocation buttonLoc = locateUpdateAvailableButton().orElseThrow(()->new FantasyGroundsAppException("No 'UPDATE AVAILABLE' button found in Fantasy Grounds tab."));
				// TODO: Convert the button location to screen coordinates and click it using the Peekaboo client
				throw new UnsupportedOperationException("Not yet implemented");
			}			

			private Optional<ButtonClickLocation> locateUpdateAvailableButton() {
				var seeInfo = peekabooClient.see(PeekabooClient.AppTarget.ProcessTarget.of(processId), screenshotDirPathFactory.get());
				Optional<ButtonBoundingBox> buttonLoc = uiLocator.locateButton("UPDATE AVAILABLE", seeInfo.screenshotImage());
				log.atInfo().log(()->"FantasyGroundsTab.locateUpdateAvailableButton() - buttonLoc: " + buttonLoc);
				
				Optional<ButtonClickLocation> buttonClickLoc = buttonLoc.map(box -> ButtonClickLocation.fromButtonBoundingBox(box, seeInfo.imageWidth(), seeInfo.imageHeight()) );
				log.atInfo().log(()->"FantasyGroundsTab.locateUpdateAvailableButton() - buttonClickLoc: " + buttonClickLoc);
				
				// Need to add target bounds to coordinates to get the actual screen coordinates for the click. 
				// This is not yet implemented.
				
				return buttonClickLoc;
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
