package io.github.rmcdouga.fgumcpserver.text.domain;

import java.text.Normalizer;

import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

/**
 * Santizes text by:
 *   1) converting left/right single and double quotes to their basic equivalents
 *   2) converting ligatures to their constituent characters
 *   3) convert special spaces to basic space
 */
public class FguTextSanitizer {
	
	private FguTextSanitizer() {	// Private constructor to prevent instantiation of this utility class.
	}

	/**
	 * Sanitizes text as required by Fantasy Grounds.
	 * 
	 * Sanitizes text by:
	 *  1) converting left/right single and double quotes to their basic equivalents
	 *  2) converting ligatures to their constituent characters
	 *  3) convert special spaces to basic space
	 *  
	 * @param textIn
	 * @return
	 */
	static String staticSanitize(String textIn) {
		return Normalizer.normalize(textIn, Normalizer.Form.NFKC)	// ligatures and special spaces
				.replace("\u201C", "\"") 		// left double quote
				.replace("\u201D", "\"") 		// right double quote
				.replace("\u2018", "'") 		// left single quote
				.replace("\u2019", "'") 		// right single quote
				;
	}

	/**
	 * Creates a factory for a TextSanitizer that uses the staticSanitize method.
	 * 
	 * @return Factory for generating a TextSanitizer that uses the staticSanitize method.
	 */
	public static TextSanitizer factory() {
		return t->staticSanitize(t);
	};
}
