package io.github.rmcdouga.fgmcpserver.fg_listener;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Function;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataException;
import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataFactory;

public abstract class AbstractJsonDataTest {

	private static final String OBJ_SUBTREE = "{ \"subObj\" : \"subObjValue\"}";
	private static final String SAMPLE_JSON_FORMAT_STR = "{ \"foo\" : \"bar\", \"obj\" : %s }";
	private static final String SAMPLE_JSON_DATA = String.format(SAMPLE_JSON_FORMAT_STR, OBJ_SUBTREE);
	
	private static final String SAMPLE_JSON_DATA_PRETTY = """
			{
			  "foo" : "bar",
			  "obj" : {
			    "subObj" : "subObjValue"
			  }
			}""";
	
	private final JsonDataFactory jsonFactory;
	private final JsonData underTest;

	public AbstractJsonDataTest(JsonDataFactory jsonFactory) {
		this.jsonFactory = jsonFactory;
		this.underTest = jsonFactory.fromJsonString(SAMPLE_JSON_DATA);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testAsString() {
		assertEquals(SAMPLE_JSON_DATA_PRETTY, underTest.toString());
	}

	@Test
	void testGetStringAt() {
		assertAll(
				()->assertEquals("bar", underTest.getStringAt("/foo").get(), "Should find foo in JsonData"),
				()->assertEquals("subObjValue", underTest.getStringAt("/obj/subObj").get(), "Should find obj/subObj in JsonData"),
				()->assertTrue(underTest.getStringAt("/bar").isEmpty(), "Should not find bar in JsonData")
				);
	}

	@Test
	void testSubsetAtString() throws Exception {
		JSONAssert.assertEquals(OBJ_SUBTREE , underTest.subsetAt("/obj").get().toString(), false);
		assertTrue(underTest.subsetAt("/foo").isEmpty());
	}

	@Test
	void testFrom_InvalidJson() {
		JsonDataException ex = assertThrows(JsonDataException.class, ()->jsonFactory.fromJsonString("{ foo : bar }"));	// labels not in quotes.
		assertThat(ex).message()
					  .contains("Failed to parse JSON");
	}

	@Test
	void testArrayAtString() throws JSONException {
		String obj1 = "{ \"obj1\" : \"obj1Value\" }";
		String obj2 = "{ \"obj2\" : \"obj2Value\" }";
		String sampleArray = "[ %s, %s ]".formatted(obj1, obj2);
		JsonData underTest = jsonFactory.fromJsonString(String.format(SAMPLE_JSON_FORMAT_STR, sampleArray));
				
		
		List<JsonData> result = underTest.arrayAt("/obj").toList();
		
		assertEquals(2, result.size());
		JSONAssert.assertEquals(obj1 , result.get(0).toString(), false);
		JSONAssert.assertEquals(obj2 , result.get(1).toString(), false);
	}

	@Test
	void testArrayAtString_NotArray() {
		assertEquals(List.of(), underTest.arrayAt("/obj").map(JsonData::toString).toList());
	}


//	@Test
//	void testListAtString() throws Exception {
//		String obj1 = "{ \"obj1\" : \"obj1Value\" }";
//		String obj2 = "{ \"obj2\" : \"obj2Value\" }";
//		String sampleArray = "[ %s, %s ]".formatted(obj1, obj2);
//		JsonData underTest = jsonFactory.apply(String.format(SAMPLE_JSON_FORMAT_STR, sampleArray));
//		List<JsonData> result = underTest.listAt("/obj");
//		
//		assertEquals(2, result.size());
//		JSONAssert.assertEquals(obj1 , result.get(0).asString(), false);
//		JSONAssert.assertEquals(obj2 , result.get(1).asString(), false);
//	}
//
//	@Test
//	void testListAtJsonPointer() throws Exception {
//		String obj1 = "{ \"obj1\" : \"obj1Value\" }";
//		String obj2 = "{ \"obj2\" : \"obj2Value\" }";
//		String sampleArray = "[ %s, %s ]".formatted(obj1, obj2);
//		JsonData underTest = jsonFactory.apply(String.format(SAMPLE_JSON_FORMAT_STR, sampleArray));
//		List<JsonData> result = underTest.listAt(underTest.pointerOf("/obj"));
//		
//		assertEquals(2, result.size());
//		JSONAssert.assertEquals(obj1 , result.get(0).asString(), false);
//		JSONAssert.assertEquals(obj2 , result.get(1).asString(), false);
//	}
//
//	@Test
//	void testDetermineRootName() {
//		JsonData underTest = jsonFactory.apply("{ \"foo\" : %s }".formatted(OBJ_SUBTREE));
//		assertEquals("foo", underTest.determineRootName());
//	}
//
//	@Test
//	void testDetermineRootName_TooManyRoots() {
//		IllegalStateException ex = assertThrows(IllegalStateException.class, ()->underTest.determineRootName());
//		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Expected just one json root but found nodes", "foo", "obj"));
//	}
//
//	@Test
//	void testListChildren() {
//		assertEquals(List.of("foo", "obj"), underTest.listChildren());
//	}
//
//	@Test
//	void testListChildrenAtString() {
//		assertEquals(List.of("subObj"), underTest.listChildrenAt("/obj"));
//	}
//
//	@Test
//	void testListChildrenAtJsonDataPointer() {
//		assertEquals(List.of("subObj"), underTest.listChildrenAt(underTest.pointerOf("/obj")));
//	}
//
//	@Test
//	void testChildren() {
//		assertEquals(List.of("foo", "obj"), underTest.children().toList());
//	}
//
//	@Test
//	void testChildrenAtString() {
//		assertEquals(List.of("subObj"), underTest.childrenAt("/obj").toList());
//	}
//
//	@Test
//	void testChildrenAtJsonDataPointer() {
//		assertEquals(List.of("subObj"), underTest.childrenAt(underTest.pointerOf("/obj")).toList());
//	}
//
//
//	@Test
//	void testPointerToString() {
//		String pointerValue = "/obj/subObj";
//		assertEquals(pointerValue, underTest.pointerOf(pointerValue).toString());
//	}
}