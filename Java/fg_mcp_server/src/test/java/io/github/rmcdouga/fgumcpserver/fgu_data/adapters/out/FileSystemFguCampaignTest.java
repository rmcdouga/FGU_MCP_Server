package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguCampaign;

@DisplayName("FileSystemFguCampaign Tests")
class FileSystemFguCampaignTest {
	
	Function<Path, FguCampaign> campaignFactory = FileSystemFguCampaign::new;

	@Test
	@DisplayName("should construct with valid path")
	void shouldConstructWithValidPath() {
		// Arrange
		Path campaignPath = Path.of("/fantasy/grounds/campaigns/MyAdventure");

		// Act
		FguCampaign campaign = campaignFactory.apply(campaignPath);

		// Assert
		assertThat(campaign).isNotNull();
	}

	@Test
	@DisplayName("should throw NullPointerException when path is null")
	void shouldThrowNullPointerExceptionWhenPathIsNull() {
		// Act & Assert
		assertThatThrownBy(() -> campaignFactory.apply(null))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("campaignRootPath cannot be null");
	}

	@ParameterizedTest
	@ValueSource(strings = { "Campaign1", "My Awesome Adventure", "DnD-Campaign", "campaign_2024", "CMP_01" })
	@DisplayName("should return correct name for various campaign directory names")
	void shouldReturnCorrectNameForVariousCampaigns(String campaignName) {
		// Arrange
		Path campaignPath = Path.of("/campaigns", campaignName);
		FguCampaign campaign = campaignFactory.apply(campaignPath);

		// Act
		String name = campaign.name();

		// Assert
		assertThat(name)
				.as("Campaign name should match directory name: %s", campaignName)
				.isEqualTo(campaignName);
	}
}
