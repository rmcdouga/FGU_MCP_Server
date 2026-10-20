package io.github.rmcdouga.fg_peekaboo_proto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

public interface JsonData {
	public JsonDataPointer pointer(String pointerString);
	
	/**
	 * Returns the value of the JsonNode pointed at using a JsonPointer string.
	 * 
	 * @param jsonDataPtr
	 * @return
	 */
	public Optional<Boolean> getBooleanAt(String jsonDataPtr);
	public Optional<BigDecimal> getDecimalAt(String jsonDataPtr);
	public Optional<Double> getDoubleAt(String jsonDataPtr);
	public Optional<Float> getFloatAt(String jsonDataPtr);
	public Optional<Integer> getIntAt(String jsonDataPtr);
	public Optional<Long> getLongAt(String jsonDataPtr);
	public Optional<Short> getShortAt(String jsonDataPtr);
	public Optional<String> getStringAt(String jsonDataPtr);

	public static interface JsonDataPointer {}

	@FunctionalInterface
	public static interface JsonDataFactory {
		public JsonData fromJsonString(String jsonString) throws JsonDataException;
	}
	
	@SuppressWarnings("serial")
	public static class JsonDataException extends RuntimeException {
		public JsonDataException(String message) {
			super(message);
		}
		public JsonDataException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Returns the JsonData of the JsonNode pointed at using a JsonPointer string.
	 * 
	 * @param jsonPtr
	 * @return
	 */
	Optional<JsonData> subsetAt(String jsonPtr);

	/**
	 * Returns a Stream of JsonData of the Array pointed at using a JsonPointer String
	 * 
	 * @param jsonPtr - String containing JsonPointer
	 * @return
	 */
	Stream<JsonData> arrayAt(String jsonPtr);

}
