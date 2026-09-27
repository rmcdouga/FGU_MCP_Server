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
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguImagesStore;

@DisplayName("FguDataLogic")
class FguDataLogicTest {

	private final class MockFguDataStore implements FguDataStore {
		private final FguCampaign[] campaigns;
		private final AtomicInteger campaignCalls = new AtomicInteger(0);

		private MockFguDataStore(FguCampaign[] campaigns) {
			this.campaigns = campaigns;
		}

		@Override
		public Stream<FguCampaign> campaigns() {
			campaignCalls.incrementAndGet();
			return Stream.of(campaigns);
		}

		@Override
		public FguImagesStore imagesStore() {
			throw new UnsupportedOperationException("Not implemented for test");
		}

		public AtomicInteger campaignCalls() {
			return campaignCalls;
		}
	}

	@Test
	@DisplayName("campaigns maps store campaigns into input campaigns preserving names")
	void campaignsMapsNamesFromStoreCampaigns() {
		FguDataStore store = createFguDataStore(createFguCampaign("Keoland"), createFguCampaign("Saltmarsh"));
		FguDataLogic logic = new FguDataLogic(store);

		List<String> names = logic.campaigns()
				.map(Campaign::name)
				.toList();

		assertThat(names).containsExactly("Keoland", "Saltmarsh");
	}

	@Test
	@DisplayName("campaigns returns empty stream when store returns empty stream")
	void campaignsReturnsEmptyStreamWhenStoreIsEmpty() {
		FguDataStore store = createFguDataStore();
		FguDataLogic logic = new FguDataLogic(store);

		List<Campaign> campaigns = logic.campaigns().toList();

		assertThat(campaigns).isEmpty();
	}

	@Test
	@DisplayName("campaigns delegates to data store each call")
	void campaignsDelegatesToDataStoreEachCall() {
		FguDataStore store = createFguDataStore(createFguCampaign("A"));
		FguDataLogic logic = new FguDataLogic(store);

		logic.campaigns().count();
		logic.campaigns().count();

		assertThat(((MockFguDataStore)store).campaignCalls().get()).isEqualTo(2);
	}
	
	private FguCampaign createFguCampaign(String name) {
		return new FguCampaign() {
			@Override
			public String name() {
				return name;
			}

			@Override
			public FguImagesStore imagesStore() {
				throw new UnsupportedOperationException("Not implemented for test");
			}
		};
	}
	
	private FguDataStore createFguDataStore(FguCampaign... campaigns) {
		return new MockFguDataStore(campaigns);
	}
}
