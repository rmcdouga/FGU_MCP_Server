package io.github.rmcdouga.fgumcpserver.text.adapters.in;

import org.springframework.ai.mcp.annotation.McpTool;

import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

/**
 * MCP tool for sanitizing text for Fantasy Grounds. 
 * 
 * This class provides a method to sanitize text using the provided TextSanitizer implementation.
 * 
 */
public class TextTools {

	private final TextSanitizer textSanitizer;
	
	/**
	 * Constructs a TextTools instance with the specified TextSanitizer.
	 * 
	 * @param textSanitizer
	 */
	public TextTools(TextSanitizer textSanitizer) {
		this.textSanitizer = textSanitizer;
	}

	/**
	 * Sanitizes text for Fantasy Grounds using the provided TextSanitizer implementation.
	 * 
	 * @param textIn
	 * @return
	 */
	@McpTool(description = "Sanitize text for Fantasy Grounds.")
	String sanitizeTextForFgu(String textIn) {
		return "Sanitized version of the text is:\n%s".formatted(textSanitizer.sanitize(textIn));
	}
}
