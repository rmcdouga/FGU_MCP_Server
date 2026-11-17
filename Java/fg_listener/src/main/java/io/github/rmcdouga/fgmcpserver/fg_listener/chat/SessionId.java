package io.github.rmcdouga.fgmcpserver.fg_listener.chat;

public record SessionId(String sessionId) {
	public static SessionId fromString(String sessionId) {
		return new SessionId(sessionId);
	}
	public String asString() {
		return sessionId;
	}
}
