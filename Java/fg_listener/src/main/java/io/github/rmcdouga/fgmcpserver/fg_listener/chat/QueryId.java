package io.github.rmcdouga.fgmcpserver.fg_listener.chat;

import java.util.Optional;

public record QueryId(String sessionId, QueryType queryType, Integer queryNumber) {
	public enum QueryType {
		Chat,
		Command,
		;
		
		private static final QueryType[] VALUES = values();

		public static Optional<QueryType> fromString(String value) {
			for (var type : VALUES) {
				if (type.name().equalsIgnoreCase(value)) {
					return Optional.of(type);
				}
			}
			return Optional.empty();
		}
		
		public String asString() {
			return name().toLowerCase();
		}
	}

	static QueryId fromString(String queryIdString) {
		var parts = queryIdString.split(":");
		if (parts.length != 3) {
			throw new IllegalArgumentException("Invalid query ID string: " + queryIdString);
		}
		var sessionId = parts[0];
		if (sessionId.isEmpty()) {
			throw new IllegalArgumentException("Session ID cannot be empty in query ID string: " + queryIdString);
		}
		var queryType = QueryType.fromString(parts[1]).orElseThrow(()->new IllegalArgumentException("Invalid query type: " + parts[1]));
		var queryNumber = Integer.parseInt(parts[2]);
		if (queryNumber < 0) {
			throw new IllegalArgumentException("Query number cannot be negative in query ID string: " + queryIdString);
		}
		return new QueryId(sessionId, queryType, queryNumber);
	}

	public String asString() {
		return sessionId + ":" + queryType.asString() + ":" + queryNumber.toString();
	}
}
