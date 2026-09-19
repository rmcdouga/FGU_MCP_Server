package io.github.rmcdouga.fgumcpserver.fgu_data.domain;

import java.util.stream.Stream;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in.Campaign;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in.FguData;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguDataStore;

/**
 * Acts as a glue/translation layer for the FGU data domain.
 * 
 * It is responsible for orchestrating the retrieval of data from the FguDataStore adapter and 
 * transforming it into a form suitable for use by FguData client class.
 * 
 */
public class FguDataLogic implements FguData {
	private final FguDataStore dataStore;

	public FguDataLogic(FguDataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	public Stream<Campaign> campaigns() {
		return dataStore.campaigns().map(c -> new Campaign() {
			@Override
			public String name() {
				return c.name();
			}
		});
	}


}
