package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.test.context.ActiveProfiles;

import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonClickLocation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
// @ActiveProfiles({ "integration-test", "exclude-mcp" })
class UiLocatorTest {
	private static final Logger log = LoggerFactory.getLogger(UiLocatorTest.class);
	private static final Path UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithUpdate.png");
	private static final Path NO_UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithoutUpdate.png");

	@ParameterizedTest
	@CsvSource({
		"MY CAMPAIGNS, 820, 80, 1580, 220",
		"UPDATE AVAILABLE, 482, 1326, 2694, 1384"
	})
	void testLocateButton_UpdateAvailable(String buttonName, int expectedXmin, int expectedYmin, int expectedXmax, int expectedYmax, @Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act
		ButtonClickLocation buttonLocation = uiLocator.locateButton(buttonName, UPDATE_AVAILABLE_SCREENSHOT_PATH).orElseThrow();
		log.atDebug().log(()->buttonName +" button Location: " + buttonLocation);
		
		// Assert
		assertNotNull(buttonLocation);
		assertThat(buttonLocation.x())
			.as("Button click X coordinate must be inside the button min/max")
			.isBetween(expectedXmin, expectedXmax);
		assertThat(buttonLocation.y())
			.as("Button click Y coordinate must be inside the button min/max")
			.isBetween(expectedYmin, expectedYmax);
	}

	@ParameterizedTest
	@CsvSource({
		"MY CAMPAIGNS, 820, 80, 1580, 220",
		"UPDATE AVAILABLE, 0, 0, 0, 0"
	})
	void testLocateButton_NoUpdateAvailable(String buttonName, int expectedXmin, int expectedYmin, int expectedXmax, int expectedYmax, @Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act
		Optional<ButtonClickLocation> buttonLocation = uiLocator.locateButton(buttonName, NO_UPDATE_AVAILABLE_SCREENSHOT_PATH);
		
		// Assert
		if (expectedXmin == 0 && expectedYmin == 0 && expectedXmax == 0 && expectedYmax == 0) {
			assertThat(buttonLocation)
				.as("Button coordinate must be empty when the button is not found")
				.isEmpty();
			return;
		} else {
			assertThat(buttonLocation)
				.as("Button coordinate must be present when the button is found")
				.isPresent();
				assertThat(buttonLocation.get().x())
					.as("Button click X coordinate must be inside the button min/max")
					.isBetween(expectedXmin, expectedXmax);
				assertThat(buttonLocation.get().y())
					.as("Button click Y coordinate must be inside the button min/max")
					.isBetween(expectedYmin, expectedYmax);
		}
	}

	@Test 
	void testLocateButtonWithInvalidButtonName(@Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act
		Optional<ButtonClickLocation> buttonLocation = uiLocator.locateButton("INVALID BUTTON NAME", UPDATE_AVAILABLE_SCREENSHOT_PATH);
		
		// Assert
		assertThat(buttonLocation)
			.as("Button coordinate must be empty when the button is not found")
			.isEmpty();
	}
	
	@Test
	void testIsButtonPresentInvalidScreenshot(@Autowired UiLocator uiLocator) {
		// Arrange
		String nonExistentFilename = "NonExistentScreenshot.png";
		assertNotNull(uiLocator);
		
		// Act & Assert
		assertThatExceptionOfType(UncheckedIOException.class)
			.isThrownBy(()->uiLocator.locateButton("MY CAMPAIGNS", Path.of(nonExistentFilename)))
			.withMessageContaining("NonExistentScreenshot.png");
	}
}
