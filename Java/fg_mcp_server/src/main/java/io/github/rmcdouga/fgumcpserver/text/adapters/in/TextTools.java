package io.github.rmcdouga.fgumcpserver.text.adapters.in;

import org.springframework.ai.tool.annotation.Tool;

import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

public class TextTools {

	private final TextSanitizer textSanitizer;
	
	public TextTools(TextSanitizer textSanitizer) {
		this.textSanitizer = textSanitizer;
	}

	@Tool(description = "Sanitize text for Fantasy Grounds.")
	String sanitizeTextForFgu(String textIn) {
		return textSanitizer.sanitize(textIn);
	}
}
