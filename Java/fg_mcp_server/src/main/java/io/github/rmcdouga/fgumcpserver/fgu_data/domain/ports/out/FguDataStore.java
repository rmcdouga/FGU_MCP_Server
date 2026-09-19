package io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out;

import java.util.stream.Stream;

public interface FguDataStore {
	Stream<FguCampaign> campaigns();
	
	@SuppressWarnings("serial")
	public static class FguDataStoreException extends RuntimeException {

		public FguDataStoreException(String message) {
			super(message);
		}

		public FguDataStoreException(String message, Throwable cause) {
			super(message, cause);
		}

		public FguDataStoreException(Throwable cause) {
			super(cause);
		}
	}
}
