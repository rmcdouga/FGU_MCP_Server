package io.github.rmcdouga.fgumcpserver.text;

import java.text.Normalizer;

/**
 * Santizes text by:
 *   1) converting left/right single and double quotes to their basic equivalents
 *   2) converting ligatures to their constituent characters
 *   3) convert special spaces to basic space
 */
public class TextSanitizer {
	
	static String sanitize(String textIn) {
		return Normalizer.normalize(textIn, Normalizer.Form.NFKC)	// ligatures and special spaces
				.replace("\u201C", "\"") 		// left double quote
				.replace("\u201D", "\"") 		// right double quote
				.replace("\u2018", "'") 		// left single quote
				.replace("\u2019", "'") 		// right single quote
				;
	}

}
