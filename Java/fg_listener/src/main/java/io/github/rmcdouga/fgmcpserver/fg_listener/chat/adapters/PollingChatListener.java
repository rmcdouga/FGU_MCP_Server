package io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters;

import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever;

public class PollingChatListener {
	
	private final ChatRetriever chatRetriever;

	public PollingChatListener(ChatRetriever chatRetriever) {
		this.chatRetriever = chatRetriever;
		super();
	}

}
