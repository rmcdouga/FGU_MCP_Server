package io.github.rmcdouga.fgumcpserver.text.adapters.in;

import org.springframework.ai.mcp.annotation.McpTool;

import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

public class TextTools {

	private final TextSanitizer textSanitizer;
	
	public TextTools(TextSanitizer textSanitizer) {
		this.textSanitizer = textSanitizer;
	}

	@McpTool(description = "Sanitize text for Fantasy Grounds.")
	String sanitizeTextForFgu(String textIn) {
		return "Sanitized version of the text is:\n%s".formatted(textSanitizer.sanitize(textIn));
	}
}
