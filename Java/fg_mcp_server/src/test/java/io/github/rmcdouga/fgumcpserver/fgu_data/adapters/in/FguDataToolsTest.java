package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.in;

import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in.FguData;

@ExtendWith(MockitoExtension.class)
class FguDataToolsTest {
	
	private final FguData mockFguData;
	private final FguDataTools underTest;
	
	FguDataToolsTest(@Mock FguData mockFguData) {
		this.mockFguData = mockFguData;
		this.underTest = new FguDataTools(mockFguData);
	}

	@Test
	void testListFguCampaigns() {
		when(mockFguData.campaigns()).thenReturn(
				java.util.stream.Stream.of(
						() -> "Campaign 1",
						() -> "Campaign 2",
						() -> "Campaign 3"
				)
		);
		
		String result = underTest.listFguCampaigns();
		assertThat(result).isEqualTo("Available Fantasy Grounds Campaigns:\nCampaign 1\nCampaign 2\nCampaign 3");
	}

	@Test
	void testListFguCampaigns_NoCampaigns() {
		when(mockFguData.campaigns()).thenReturn(Stream.empty());
		
		String result = underTest.listFguCampaigns();
		assertThat(result).isEqualTo("No Fantasy Grounds Campaigns available");
	}

}
