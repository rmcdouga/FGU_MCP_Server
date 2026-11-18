package io.github.rmcdouga.fgmcpserver.fg_listener.chat.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CursorLocationTest {

	@Test
	void testCursorLocation_NegativeLocation() {
		assertThatIllegalArgumentException().isThrownBy(() -> CursorLocation.fromInteger(-1))
											.withMessage("Cursor location cannot be negative: -1");
	}

	@Test
	void testFromString() {
		CursorLocation underTest = CursorLocation.fromString("42");
		assertEquals(42, underTest.asInteger());
		assertEquals("42", underTest.asString());
	}

	@Test
	void testfromInteger() {
		CursorLocation underTest = CursorLocation.fromInteger(42);
		assertEquals(42, underTest.asInteger());
		assertEquals("42", underTest.asString());
	}

}
