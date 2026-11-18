package io.github.rmcdouga.fgmcpserver.fg_listener.chat.domain;

public record SessionId(String sessionId) {
	public static SessionId fromString(String sessionId) {
		return new SessionId(sessionId);
	}
	public String asString() {
		return sessionId;
	}
}
