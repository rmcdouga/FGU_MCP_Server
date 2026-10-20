package io.github.rmcdouga.fg_peekaboo_proto;

import java.util.List;
import java.util.OptionalLong;

import io.github.rmcdouga.fg_peekaboo_proto.FantasyGroundsApp.FantasyGroundsProcess.InitialWindow.FantasyGroundsTab;

public class FantasyGroundsApp {
	private static final String FG_PROCESS_NAME = "SmiteWorks"; // The name of the Fantasy Grounds process should contain this
	private static final String FG_BUNDLE_ID = "unity.SmiteWorks.Fantasy Grounds"; // The bundle identifier for Fantasy Grounds (macOS)

	private final PeekabooClient peekabooMcpClient;
	private final UiLocator uiLocator;
	
	public FantasyGroundsApp(PeekabooClient peekabooMcpClient, UiLocator uiLocator) {
		this.peekabooMcpClient = peekabooMcpClient;
		this.uiLocator = uiLocator;
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
			
			public static final class FantasyGroundsTab extends InitialWindowImpl implements InitialWindow {
				Boolean updateAvailable() {
					throw new UnsupportedOperationException("Not yet implemented");
				}
				
				FantasyGroundsTab performUpdate() {
					throw new UnsupportedOperationException("Not yet implemented");
				}
			}

			public static final class MyCampaignsTab extends InitialWindowImpl implements InitialWindow {
				
			}

			public static final class JoinAGameTab extends InitialWindowImpl implements InitialWindow {
				
			}

		}

		private static sealed abstract class InitialWindowImpl implements InitialWindow {
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
	
	public static FantasyGroundsTab startProcess() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
