package io.github.rmcdouga.fgumcpserver.text.domain.ports.in;

/**
 * Interface for sanitizing text for Fantasy Grounds.
 * 
 */
public interface TextSanitizer {
	/**
	 * Sanitizes text as required by Fantasy Grounds.
	 * 
	 * @param textIn
	 * @return
	 */
	String sanitize(String textIn);
}
