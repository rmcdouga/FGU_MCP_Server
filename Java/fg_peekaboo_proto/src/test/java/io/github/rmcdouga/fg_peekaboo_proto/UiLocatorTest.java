package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonClickLocation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UiLocatorTest {
	private static final Logger log = LoggerFactory.getLogger(UiLocatorTest.class);
	private static final Path UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithUpdate.png");

	static final List<Arguments> BUTTONS = List.of(
			Arguments.of("MY CAMPAIGNS", 820, 80, 1580, 220),
			Arguments.of("UPDATE AVAILABLE", 482, 1326, 2694, 1384)
	);
	
	@ParameterizedTest
	@FieldSource("BUTTONS")
	void testLocateButton(String buttonName, int expectedXmin, int expectedYmin, int expectedXmax, int expectedYmax, @Autowired UiLocator uiLocator) {
		// This test is a placeholder to ensure that the UiLocator bean is created successfully
		assertNotNull(uiLocator);
		ButtonClickLocation buttonLocation = uiLocator.locateButton(buttonName, UPDATE_AVAILABLE_SCREENSHOT_PATH);
		log.atDebug().log(()->buttonName +" button Location: " + buttonLocation);
		assertNotNull(buttonLocation);
		assertThat(buttonLocation.x())
			.as("Button click X coordinate must be inside the button min/max")
			.isBetween(expectedXmin, expectedXmax);
		assertThat(buttonLocation.y())
			.as("Button click Y coordinate must be inside the button min/max")
			.isBetween(expectedYmin, expectedYmax);
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
