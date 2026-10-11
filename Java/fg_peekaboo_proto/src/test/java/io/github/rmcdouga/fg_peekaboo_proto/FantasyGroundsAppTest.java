package io.github.rmcdouga.fg_peekaboo_proto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("This test is only for manual testing of the Fantasy Grounds process detection.")
class FantasyGroundsAppTest {

	@Test
	void testProcessId() {
		System.out.println("Found FG process: %d".formatted(FantasyGroundsApp.processId()));
	}

}
