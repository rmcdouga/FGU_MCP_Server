package io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports;

import java.util.stream.Stream;

public interface ChatListener {
	
	public record ChatMessage(String sender, String message) {}
	
	Stream<ChatMessage> chatMessages();

}
