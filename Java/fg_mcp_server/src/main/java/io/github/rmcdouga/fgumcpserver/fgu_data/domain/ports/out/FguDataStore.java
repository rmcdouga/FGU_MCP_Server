package io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out;

import java.util.stream.Stream;

public interface FguDataStore {
	/**
	 * Return a stream of all the Fantasy Grounds campaigns available in this data store.
	 * 
	 * @return
	 */
	Stream<FguCampaign> campaigns();
	
	/**
	 * Returns the global FgußImagesStore for this data store.
	 * 
	 * @return
	 */
	FguImagesStore imagesStore();
	
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
