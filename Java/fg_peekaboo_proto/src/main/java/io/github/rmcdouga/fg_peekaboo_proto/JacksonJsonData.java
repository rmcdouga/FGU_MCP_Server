package io.github.rmcdouga.fg_peekaboo_proto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

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
	

}
