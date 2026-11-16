package io.github.rmcdouga.fgmcpserver.fg_listener.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.rmcdouga.fgmcpserver.fg_listener.chat.QueryId.QueryType;

class QueryIdTest {

	@ParameterizedTest
	@CsvSource({
		"session1:chat:1,session1,Chat,1",
		"session2:command:42,session2,Command,42"
	})
	void testFromString(String queryIdString, String expectedSessionId, QueryType expectedQueryType, Integer expectedQueryNumber) {
		// Act
		var queryId = QueryId.fromString(queryIdString);
		
		// Assert
		assertThat(queryId.sessionId()).isEqualTo(expectedSessionId);
		assertThat(queryId.queryType()).isEqualTo(expectedQueryType);
		assertThat(queryId.queryNumber()).isEqualTo(expectedQueryNumber);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"invalid:format",
		"session1:invalid:1",
		"session1:chat:notanumber",
		":chat:1",
		"session1:chat:-1",
	})
	void testFromStringInvalidFormat(String invalidQueryIdString) {
		// Act & Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> QueryId.fromString(invalidQueryIdString));
		assertThat(ex.getMessage()).containsAnyOf(
			"Invalid query ID string",
			"Invalid query type",
			"For input string: \"notanumber\"",
			"Session ID cannot be empty",
			"Query number cannot be negative"
		);
	}
	
	@ParameterizedTest
	@CsvSource({
		"session1,Chat,1,session1:chat:1",
		"session2,Command,42,session2:command:42"
	})
	void testAsString(String sessionId, QueryType queryType, Integer queryNumber, String expectedQueryIdString) {
		// Arrange
		var queryId = new QueryId(sessionId, queryType, queryNumber);
		
		// Act
		var result = queryId.asString();
		
		// Assert
		assertThat(result).isEqualTo(expectedQueryIdString);
	}

	static class QueryTypeTest {

		@ParameterizedTest
		@CsvSource({
			"Chat,Chat",
			"chat,Chat",
			"Command,Command",
			"command,Command",
			"Invalid,"
		})
		void testFromString(String testString, QueryType expected) {
			// Act
			var result = QueryType.fromString(testString);
			
			// Assert
			if (expected == null) {
				assertTrue(result.isEmpty());
			} else {
				assertTrue(result.isPresent());
				assertEquals(expected, result.get());
			}
		}

		@ParameterizedTest
		@CsvSource({
			"Chat,chat",
			"Command,command"
		})
		void testAsString(QueryType queryType, String expected) {
			// Act
			var result = queryType.asString();
			
			// Assert
			assertEquals(expected, result);
		}

	}

}
