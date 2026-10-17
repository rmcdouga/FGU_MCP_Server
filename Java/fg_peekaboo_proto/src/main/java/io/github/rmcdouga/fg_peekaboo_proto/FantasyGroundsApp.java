package io.github.rmcdouga.fg_peekaboo_proto;

import java.util.List;
import java.util.OptionalLong;

public class FantasyGroundsApp {
	private static final String FG_PROCESS_NAME = "SmiteWorks"; // The name of the Fantasy Grounds process should contain this
	private final PeekabooMcpClient peekabooMcpClient;
	private final UiLocator uiLocator;
	
	public FantasyGroundsApp(PeekabooMcpClient peekabooMcpClient, UiLocator uiLocator) {
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
		
		public class IntialWindow {
			
		}
	}
	
//	public static FantasyGroundsProcess.IntialWindow startProcess() {
//		
//	}
}
