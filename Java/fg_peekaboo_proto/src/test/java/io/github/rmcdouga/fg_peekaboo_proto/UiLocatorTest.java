package io.github.rmcdouga.fg_peekaboo_proto;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UiLocatorTest {

	@Test
	void testLocateButton(@Autowired UiLocator uiLocator) {
		// This test is a placeholder to ensure that the UiLocator bean is created successfully
		assertNotNull(uiLocator);
		uiLocator.locateButton("MY CAMPAIGNS", Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithUpdate.png"));
	}

	@SpringBootApplication()
	@ComponentScan(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FgPeekabooPrototypeApplication.class))
	static class TestApplication {

		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}


		@Bean
	    ChatClientBuilderCustomizer setTemperature() {
	    	return builder -> builder.defaultOptions(ChatOptions.builder().temperature(0.0)); // Make the model deterministic for testing purposes
	    }
		
		@Bean
		UiLocator uiLocator(ChatClient.Builder chatClientBuilder) {
			return new UiLocator(chatClientBuilder);
		}
	}
}
