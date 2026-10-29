package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MimeType;

import io.github.rmcdouga.fg_peekaboo_proto.FantasyGroundsApp.FantasyGroundsProcess.InitialWindow.Tab;
import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonBoundingBox;
import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonClickLocation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({ "disable-main-app-runner"})
@ExtendWith(SoftAssertionsExtension.class)
class UiLocatorTest {
	private static final Logger log = LoggerFactory.getLogger(UiLocatorTest.class);
	private static final Path UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithUpdate.png");
	private static final Path NO_UPDATE_AVAILABLE_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithoutUpdate.png");
	private static final Path UPDATE_AVAILABLE_SCREENSHOT_RUN_UPDATER_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_WithUpdate_RunUpdater.png");
	private static final Path MY_CAMPAIGNS_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UiLocatorTest_InitialScreen_MyCampaigns_Selected.png");
	private static final Path JOIN_A_GAME_SCREENSHOT_PATH = Path.of("src/test/resources/screenshots/UILocatorTest_InitialScreen_JoinAGame_Selected.png");

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
	void testDoesScreenshotContainTexts_containsText_RunUpdater(@Autowired UiLocator uiLocator) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act & Assert
		assertThat(uiLocator.doesScreenshotContainTexts(UPDATE_AVAILABLE_SCREENSHOT_RUN_UPDATER_PATH, List.of("OK")))
			.as("Screenshot should contain both 'OK' text.")
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
	
	enum IsTabSelectedScenario {
		FANTASY_GROUNDS_SELECTED(NO_UPDATE_AVAILABLE_SCREENSHOT_PATH, true, false, false),
		MY_CAMPAIGNS_SELECTED(MY_CAMPAIGNS_SCREENSHOT_PATH, false, true, false),
		JOIN_A_GAME_SELECTED(JOIN_A_GAME_SCREENSHOT_PATH, false, false, true);
		
		private final Path screenshotPath;
		private final boolean isFantasyGroundsSelected;
		private final boolean isMyCampaignsSelected;
		private final boolean isJoinAGameSelected;
		
		private IsTabSelectedScenario(Path screenshotPath, boolean isFantasyGroundsSelected,
				boolean isMyCampaignsSelected, boolean isJoinAGameSelected) {
			this.screenshotPath = screenshotPath;
			this.isFantasyGroundsSelected = isFantasyGroundsSelected;
			this.isMyCampaignsSelected = isMyCampaignsSelected;
			this.isJoinAGameSelected = isJoinAGameSelected;
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testIsTabSelected(IsTabSelectedScenario scenario, @Autowired UiLocator uiLocator, SoftAssertions softly) {
		// Arrange
		assertNotNull(uiLocator);
		
		// Act & Assert
		softly.assertThat(uiLocator.isTabSelected(scenario.screenshotPath, Tab.FANTASY_GROUNDS))
			.as("'FANTASY GROUNDS' tab should be selected.")
			.isEqualTo(scenario.isFantasyGroundsSelected);
		
		softly.assertThat(uiLocator.isTabSelected(scenario.screenshotPath, Tab.MY_CAMPAIGNS))
			.as("'MY CAMPAIGNS' tab should not be selected.")
			.isEqualTo(scenario.isMyCampaignsSelected);
	
		softly.assertThat(uiLocator.isTabSelected(scenario.screenshotPath, Tab.JOIN_A_GAME))
			.as("'JOIN A GAME' tab should not be selected.")
			.isEqualTo(scenario.isJoinAGameSelected);
	
	}
	
	@Disabled("This test is only for manual testing to determine what the model thinks it sees in a screenshot.")
	@ParameterizedTest
	@EnumSource
	void describeScreenshot(IsTabSelectedScenario scenario, @Autowired ChatClient.Builder chatClientBuilder) {
		var chatClient = chatClientBuilder.defaultOptions(ChatOptions.builder().temperature(0.0)).build();
		String result = chatClient.prompt()
		  	 .user(p->p.media(toMedia(scenario.screenshotPath))
		  			   .text("Describe the contents of this screenshot. Highlight if anything is selected or underlined.")
		  		  )
		  	 .call()
		  	 .content();
		
		log.info("for scenario: " + scenario.name() + ", screenshot: " + scenario.screenshotPath);
		log.info(result);
		
	}
	
	private static Media toMedia(Path uiScreenshotPath) {
		try {
			return Media.builder().mimeType(MimeType.valueOf("image/png"))
								  .data(Files.readAllBytes(uiScreenshotPath))
								  .build();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read screenshot file: " + uiScreenshotPath, e);
		}
	}
	

}
