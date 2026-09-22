package io.github.rmcdouga.fgumcpserver.text;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.rmcdouga.fgumcpserver.FguMcpServerApplication;
import io.github.rmcdouga.fgumcpserver.text.adapters.in.TextTools;
import io.github.rmcdouga.fgumcpserver.text.domain.FguTextSanitizer;
import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

/**
 * Spring configuration class for text-related beans. 
 * 
 * This class defines the necessary beans for text sanitization and text tools.
 */
@Configuration
public class TextConfiguration {
	private static final String CFG_PREFIX = FguMcpServerApplication.APP_CFG_PREFIX + "text";	// Prefix for text-related configuration properties.

	private static final TextSanitizer TEXT_SANITIZER = FguTextSanitizer.factory();

	/**
	 * Creates a bean for TextTools that uses the FguTextSanitizer.
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnBooleanProperty(name = CFG_PREFIX + ".enabled", havingValue = true, matchIfMissing = true)
	TextTools textTools() {
		return new TextTools(TEXT_SANITIZER);
	}
}
