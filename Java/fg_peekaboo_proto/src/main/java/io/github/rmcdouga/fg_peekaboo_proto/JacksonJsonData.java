package io.github.rmcdouga.fg_peekaboo_proto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class JacksonJsonData implements JsonData {
	private static final JsonMapper jsonMapper = new JsonMapper();
	private final JsonNode rootNode;
	
	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return jsonMapper.readValue(json, clazz);
		} catch (JacksonException e) {
			throw new JsonDataException("Failed to parse JSON: (" + e.getMessage() + ")\n" + json, e);
		}
	}

	public static JsonData parseJson(String json) {
		try {
			return new JacksonJsonData(jsonMapper.readTree(json));
		} catch (JacksonException e) {
			throw new JsonDataException("Failed to parse JSON: (" + e.getMessage() + ")\n" + json, e);
		}
	}
	
	private JacksonJsonData(JsonNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public JsonDataPointer pointer(String pointerString) {
		return JacksonJsonDataPointer.of(pointerString);
	}

	@Override
	public Optional<Boolean> getBooleanAt(String jsonDataPtr) {
		return at(jsonDataPtr, JsonNode::asBooleanOpt);
	}

	@Override
	public Optional<BigDecimal> getDecimalAt(String jsonDataPtr) {
		return at(jsonDataPtr, JsonNode::decimalValueOpt);
	}
	
	@Override
	public Optional<Double> getDoubleAt(String jsonDataPtr) {
		return at(jsonDataPtr, jsonNode->jsonNode.asDoubleOpt()
									 			 .stream()
									 			 .mapToObj(Double::valueOf)
									 			 .findFirst());
	}

	@Override
	public Optional<Float> getFloatAt(String jsonDataPtr) {
		return at(jsonDataPtr, JsonNode::asFloatOpt);
	}
	
	@Override
	public Optional<Integer> getIntAt(String jsonDataPtr) {
		return at(jsonDataPtr, jsonNode->jsonNode.asIntOpt()
	 			 								 .stream()
	 			 								 .mapToObj(Integer::valueOf)
	 			 								 .findFirst());
	}

	@Override
	public Optional<Long> getLongAt(String jsonDataPtr) {
		return at(jsonDataPtr, jsonNode->jsonNode.asLongOpt()
	 			 								 .stream()
	 			 								 .mapToObj(Long::valueOf)
	 			 								 .findFirst());
	}

	@Override
	public Optional<Short> getShortAt(String jsonDataPtr) {
		return at(jsonDataPtr, JsonNode::asShortOpt);
	}
	
	@Override
	public Optional<String> getStringAt(String jsonDataPtr) {
		return at(jsonDataPtr, JsonNode::asStringOpt);
	}

	/**
	 * Returns the JsonData of the JsonNode pointed at using a JsonPointer string.
	 * 
	 * @param jsonPtr
	 * @return
	 */
	@Override
	public Optional<JsonData> subsetAt(String jsonPtr) {
		return subsetAt(pointer(jsonPtr));
	}
	
	/**
	 * Returns the JsonData of the JsonNode pointed at using a JsonPointer.
	 * 
	 * @param jsonPtr
	 * @return
	 */
	private Optional<JsonData> subsetAt(JsonDataPointer jsonDataPtr) {
		if (!(jsonDataPtr instanceof JacksonJsonDataPointer jacksonPtr)) {
			throw new IllegalArgumentException("Expected JacksonJsonDataPointer, got: " + jsonDataPtr.getClass().getName());
		}
		JsonNode node = rootNode.at(jacksonPtr.jsonPointer);
		return node.isContainer() ? Optional.of(JacksonJsonData.parseJson(node.toPrettyString())) : Optional.empty();
	}
	
	/**
	 * Returns a Stream of JsonData of the Array pointed at using a JsonPointer String
	 * 
	 * @param jsonPtr - String containing JsonPointer
	 * @return
	 */
	@Override
	public Stream<JsonData> arrayAt(String jsonPtr) {
		return arrayAt(pointer(jsonPtr));
	}

	/**
	 * Returns a Stream of JsonData of the Array pointed at using a JsonPointer String
	 * 
	 * @param jsonDataPtr - JsonPointer
	 * @return
	 */
	private Stream<JsonData> arrayAt(JsonDataPointer jsonDataPtr) {
		if (!(jsonDataPtr instanceof JacksonJsonDataPointer jacksonPtr)) {
			throw new IllegalArgumentException("Expected JacksonJsonDataPointer, got: " + jsonDataPtr.getClass().getName());
		}
		JsonNode node = rootNode.at(jacksonPtr.jsonPointer);
		if (!node.isArray()) { return Stream.empty(); }
		// We have an array, convert it to a list of JsonData
		Iterable<JsonNode> iterable = () -> node.iterator();
		return StreamSupport.stream(iterable.spliterator(), false)	// get stream of JsonNodes
							.map(JsonNode::toPrettyString)			// convert to Strings
							.map(JacksonJsonData::parseJson);					// convert back to JsonData
	}
	
	
	private <T> Optional<T> at(JsonDataPointer jsonDataPtr, Function<JsonNode, Optional<T>> extractor) {
		if (!(jsonDataPtr instanceof JacksonJsonDataPointer jacksonPtr)) {
			throw new IllegalArgumentException("Expected JacksonJsonDataPointer, got: " + jsonDataPtr.getClass().getName());
		}
		JsonNode node = rootNode.at(jacksonPtr.jsonPointer);
		return node.isValueNode() ? extractor.apply(node) : Optional.empty();
	}
	
	private <T> Optional<T> at(String jsonDataPtr, Function<JsonNode, Optional<T>> extractor) {
		return at(pointer(jsonDataPtr), extractor);
	} 

	private static class JacksonJsonDataPointer implements JsonDataPointer {
		private final JsonPointer jsonPointer;

		private JacksonJsonDataPointer(JsonPointer jsonPointer) {
			this.jsonPointer = jsonPointer;
		}

		private static JsonDataPointer of(String pointerString) {
			return new JacksonJsonDataPointer(JsonPointer.valueOf(pointerString));
		}

		@Override
		public String toString() {
			return jsonPointer.toString();
		}
	}

	@Override
	public String toString() {
		return rootNode.toPrettyString();
	}
	
	
}
