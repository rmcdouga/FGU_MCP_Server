package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonBoundingBox;
import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonClickLocation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({ "disable-main-app-runner"})
class UiLocatorTest {
	private static final Logger log = LoggerFactory.getLogger(UiLocatorTest.class);
	private static final Path UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithUpdate.png");
	private static final Path NO_UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithoutUpdate.png");

	// TODO: IMAGE SIZE is currently hardcoded, but it should be dynamically determined from the screenshot image.
	//       This is a temporary solution until we can get the actual image size from the screenshot.
	private static final int IMAGE_SIZE_X = 3176; // 1588 vs 1598 (latter is real)
	private static final int IMAGE_SIZE_Y = 1924; // 962 vs 994
	
	@ParameterizedTest
	@CsvSource({
		"MY CAMPAIGNS, 820, 80, 1580, 220",
		"UPDATE AVAILABLE, 482, 1326, 2694, 1384"
	})
	void testLocateButton_UpdateAvailable(String buttonName, int expectedXmin, int expectedYmin, int expectedXmax, int expectedYmax, @Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act
		ButtonClickLocation buttonLocation = uiLocator.locateButton(UPDATE_AVAILABLE_SCREENSHOT_PATH, buttonName)
													  .map(box -> ButtonClickLocation.fromButtonBoundingBox(box, IMAGE_SIZE_X, IMAGE_SIZE_Y))
													  .orElseThrow();
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
		Optional<ButtonBoundingBox> buttonBoundingBox = uiLocator.locateButton(NO_UPDATE_AVAILABLE_SCREENSHOT_PATH, buttonName);
		
		// Assert
		if (expectedXmin == 0 && expectedYmin == 0 && expectedXmax == 0 && expectedYmax == 0) {
			assertThat(buttonBoundingBox)
				.as("Button coordinate must be empty when the button is not found")
				.isEmpty();
			return;
		} else {
			var buttonLocation = buttonBoundingBox
				.map(box -> ButtonClickLocation.fromButtonBoundingBox(box, IMAGE_SIZE_X, IMAGE_SIZE_Y));
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
		Optional<ButtonBoundingBox> buttonLocation = uiLocator.locateButton(UPDATE_AVAILABLE_SCREENSHOT_PATH, "INVALID BUTTON NAME");
		
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
			.isThrownBy(()->uiLocator.locateButton(Path.of(nonExistentFilename), "MY CAMPAIGNS"))
			.withMessageContaining("NonExistentScreenshot.png");
	}
	
	@Test
	void testDoesScreenshotContainTexts_containsText(@Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act & Assert
		assertThat(uiLocator.doesScreenshotContainTexts(UPDATE_AVAILABLE_SCREENSHOT_PATH, List.of("MY CAMPAIGNS", "UPDATE AVAILABLE")))
			.as("Screenshot should contain both 'MY CAMPAIGNS' and 'UPDATE AVAILABLE' texts.")
			.isTrue();
	}
	
	@Test
	void testDoesScreenshotContainTexts_doesNotContainText(@Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act & Assert
		assertThat(uiLocator.doesScreenshotContainTexts(NO_UPDATE_AVAILABLE_SCREENSHOT_PATH, List.of("MY CAMPAIGNS", "UPDATE AVAILABLE")))
			.as("Screenshot should not contain 'UPDATE AVAILABLE' text.")
			.isFalse();
	}
	
}
