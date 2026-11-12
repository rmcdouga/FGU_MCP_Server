package io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ChatRetrieverTest {

	@Test
	void testIsBridgeUp_Up() {
		ChatRetriever.BridgeStatus underTest = new ChatRetriever.BridgeStatus(
				"0.5.2", 2, "edd009ed1bd08b53cffc72f1", true, "session-id", 123.45,
				new ChatRetriever.BridgeStatus.Context("ruleset", true, "version", "campaign"), 5, 10);
		
		assertTrue(underTest.isBridgeUp());
	}

	@Test
	void testIsBridgeUp_NotConnected() {
		ChatRetriever.BridgeStatus underTest = new ChatRetriever.BridgeStatus(
				"0.5.2", 2, "edd009ed1bd08b53cffc72f1", false, "session-id", 123.45,
				new ChatRetriever.BridgeStatus.Context(null, null, null, null), 5, 10);
		
		assertFalse(underTest.isBridgeUp());
	}

	@Test
	void testIsBridgeUp_NoHost() {
		ChatRetriever.BridgeStatus underTest = new ChatRetriever.BridgeStatus(
				"0.5.2", 2, "edd009ed1bd08b53cffc72f1", true, "session-id", 123.45,
				new ChatRetriever.BridgeStatus.Context(null, null, null, null), 5, 10);
		
		assertFalse(underTest.isBridgeUp());
	}

	@ParameterizedTest
	@CsvSource({
		"campaign,true",
		"other,false"
	})
	void testIsBridgeUp_Campaign(String campaign, boolean expectedResult) {
		ChatRetriever.BridgeStatus underTest = new ChatRetriever.BridgeStatus(
				"0.5.2", 2, "edd009ed1bd08b53cffc72f1", true, "session-id", 123.45,
				new ChatRetriever.BridgeStatus.Context("ruleset", true, "version", "campaign"), 5, 10);
		
		assertEquals(expectedResult, underTest.isBridgeUp(campaign));
	}
}
