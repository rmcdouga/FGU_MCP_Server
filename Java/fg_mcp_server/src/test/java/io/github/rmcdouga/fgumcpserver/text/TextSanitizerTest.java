package io.github.rmcdouga.fgumcpserver.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TextSanitizerTest {

	@ParameterizedTest
	@CsvSource({"\uFB00, ff", 
				"\uFB01, fi", 
				"\uFB02, fl", 
				"\uFB03, ffi", 
				"\uFB04, ffl"
				})
	void testSanitize_ligatures(String ligature, String expected) {
		assertEquals(expected, TextSanitizer.sanitize(ligature));
	}

	@ParameterizedTest
	@CsvSource( quoteCharacter = '|', // Change the quote character to avoid escaping the quotes in the test data
			value = {"“, \"", 
					 "”, \"",
					 "‘, '", 
					 "’, '",
					 "\u201C, \"", 
					 "\u201D, \"", 
					 "\u2018, '", 
					 "\u2019, '"
					 })
	void testSanitize_quotes(String unicodeQuote, String expected) {
		assertEquals(expected, TextSanitizer.sanitize(unicodeQuote));
	}

	@ParameterizedTest
	@CsvSource({"\u00A0, ' '", 
				"\u202f, ' '", 
				})
	void testSanitize_spaces(String specialSpace, String expected) {
		assertEquals(expected, TextSanitizer.sanitize(specialSpace));
	}
}
