package io.github.rmcdouga.fgumcpserver.fgu_data.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in.Campaign;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguCampaign;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguDataStore;

@DisplayName("FguDataLogic")
class FguDataLogicTest {

	@Test
	@DisplayName("campaigns maps store campaigns into input campaigns preserving names")
	void campaignsMapsNamesFromStoreCampaigns() {
		FguDataStore store = () -> Stream.of(
				(FguCampaign) () -> "Keoland",
				(FguCampaign) () -> "Saltmarsh");
		FguDataLogic logic = new FguDataLogic(store);

		List<String> names = logic.campaigns()
				.map(Campaign::name)
				.toList();

		assertThat(names).containsExactly("Keoland", "Saltmarsh");
	}

	@Test
	@DisplayName("campaigns returns empty stream when store returns empty stream")
	void campaignsReturnsEmptyStreamWhenStoreIsEmpty() {
		FguDataStore store = Stream::empty;
		FguDataLogic logic = new FguDataLogic(store);

		List<Campaign> campaigns = logic.campaigns().toList();

		assertThat(campaigns).isEmpty();
	}

	@Test
	@DisplayName("campaigns delegates to data store each call")
	void campaignsDelegatesToDataStoreEachCall() {
		AtomicInteger calls = new AtomicInteger(0);
		FguDataStore store = () -> {
			calls.incrementAndGet();
			return Stream.of((FguCampaign) () -> "A");
		};
		FguDataLogic logic = new FguDataLogic(store);

		logic.campaigns().count();
		logic.campaigns().count();

		assertThat(calls.get()).isEqualTo(2);
	}
}
