package io.github.rmcdouga.fgumcpserver.text;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.rmcdouga.fgumcpserver.text.adapters.in.TextTools;
import io.github.rmcdouga.fgumcpserver.text.domain.FguTextSanitizer;
import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

@Configuration
public class TextConfiguration {

	private static final TextSanitizer TEXT_SANITIZER = FguTextSanitizer.factory();

	@Bean
	TextTools textTools() {
		return new TextTools(TEXT_SANITIZER);
	}
}
