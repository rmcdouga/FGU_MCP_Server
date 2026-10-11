package io.github.rmcdouga.fg_peekaboo_proto;

import java.util.List;

public class FantasyGroundsApp {
	private static final String FG_PROCESS_NAME = "SmiteWorks"; // The name of the Fantasy Grounds process should contain this

	static long processId() {

        List<ProcessHandle> smiteworksProcesses = ProcessHandle.allProcesses()
        													   .filter(ph -> ph.info().command().isPresent())
        													   .filter(ph -> ph.info().command().get().contains(FG_PROCESS_NAME))
        													   .toList();
        return switch(smiteworksProcesses.size()) {
			case 0 -> throw new IllegalStateException("No Fantasy Grounds process found.");
			case 1 -> smiteworksProcesses.getFirst().pid();
			default -> throw new IllegalStateException("Multiple (%d) Fantasy Grounds processes found:".formatted(smiteworksProcesses.size()));
        };
	}
}
