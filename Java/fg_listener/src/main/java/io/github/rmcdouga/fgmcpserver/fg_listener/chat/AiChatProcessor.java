package io.github.rmcdouga.fgmcpserver.fg_listener.chat;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever;
import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever.QueryBatch;

/**
 * Handles the processing of AI chat messages. 
 * This class is responsible for managing the incoming chat messages and delegating them to individual chat message processors.
 * It manages the threading and execution of chat message processing.
 */
public class AiChatProcessor {
	private static final Logger log = LoggerFactory.getLogger(AiChatProcessor.class);
	private final Duration WAIT_TIME = Duration.ofSeconds(10);
	
	private final ChatRetriever chatRetriever;	// Used for retrieving chat messages from the bridge.
	private final ExecutorService executorService;	// Used for executing chat message processing in separate threads.
	
	// State
	private boolean stopProcessing = false;	// Flag to indicate whether the processing should be stopped.
	private int cursor = 0;	// Cursor for tracking the position in the chat message stream.
	
	
	public AiChatProcessor(ChatRetriever chatRetriever, ExecutorService executorService) {
		this.chatRetriever = chatRetriever;
		this.executorService = executorService;
	}
	
	public void startProcessing() {
		// TODO: Initialize ListenerState (not implemented yet)
		var listenerState = new ListenerState();
		while (!stopProcessing) {
			chatRetriever.pollForChatEvents(cursor, WAIT_TIME)
					     .ifPresent(msg->run(msg, listenerState));
		}
	}
	
	public void stopProcessing() throws InterruptedException {
		stopProcessing = true;
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES);
	}
	
	
	private void run(QueryBatch queryBatch, ListenerState listenerState) {
		for (var query : queryBatch.queries()) {
			log.info("Processing chat message: " + query.request().question());
		}
		// TODO: Update the listener state with the new cursor position after processing the batch.
		cursor = queryBatch.cursor();
	}
	
	/**
	 * Processes an individual chat message.
	 * It uses a single thread to process the message.
	 * 
	 */
	public class AiChatMessageProcessor {
		
		public void run() {
//			// Process the chat message here.
//			// This is where you would implement the logic to handle the AI chat message.
//			
//			// Example: You could send a response back using the chatRetriever.
//			String response = "Response to: " + chatMessage;
//			chatRetriever.sendChatResponse(response);
		}
	}
}
