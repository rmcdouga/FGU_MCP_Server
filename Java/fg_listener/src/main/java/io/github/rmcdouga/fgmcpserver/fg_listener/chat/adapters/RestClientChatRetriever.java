package io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever;

public class RestClientChatRetriever implements ChatRetriever {
	private static final Logger log = LoggerFactory.getLogger(RestClientChatRetriever.class);
	private static final String SERVER_STATUS_PATH = "/api/status";
	private static final String CHAT_EVENTS_PATH = "/api/chat/queries";
	
	private final RestClient restClient;			// RestClient for making HTTP requests to the server.  This is used to retrieve chat messages and check the server status.
	
	public RestClientChatRetriever(RestClient.Builder restClientBuilder, String baseUrl) {
		this(restClientBuilder.baseUrl(baseUrl)
							  .build());
	}

	private RestClientChatRetriever(RestClient restClient) {
		this.restClient = restClient;
		super();
	}
	
	@Override
	public Optional<BridgeStatus> bridgeStatus() {
		try {
			var status = restClient.get()
								   .uri(ub->ub.path(SERVER_STATUS_PATH)
										      .build())
								   .header("Authorization", "Bearer " + "9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11")
								   .retrieve()
								   .body(BridgeStatus.class);
			log.info("Server status: {}", status);
			return Optional.of(status);
			
		} catch (RestClientException e) {
			log.error("Failed to retrieve server status JSON: " + e.getMessage(), e);
		}
		return Optional.empty();
	}

	record ChatEvent(String type, String message, long timestamp, int cursor) {
		// This record represents a chat event retrieved from the server.
		// It contains the type of event (e.g., "message", "join", "leave"), the message content,
		// the timestamp of the event, and a cursor value for pagination.
	}

	@Override
	public Optional<QueryBatch> pollForChatEvents(int cursor, int waitSeconds) {
		// This method is a placeholder for the logic to poll for chat events from the server.
		// The actual implementation would involve making an HTTP GET request to the server's chat events endpoint,
		// passing the appropriate parameters (e.g., cursor, limit, wait time) and handling the response.
		try {
			var events = restClient.get()
					   .uri(ub->ub.path(CHAT_EVENTS_PATH)
					   			  .queryParam("after", cursor)
					   			  .queryParam("limit", 50)
					   			  .queryParam("wait", waitSeconds)
					   			  .build())
					   .header("Authorization", "Bearer " + "9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11")
					   .retrieve()
					   .body(QueryBatch.class);
			
			log.info("Retrieved chat events: \n{}", events);
			return Optional.of(events);
		} catch (RestClientException e) {
			log.error("Failed to retrieve chat events JSON: " + e.getMessage(), e);
		}
		return Optional.empty();
	}
	
	/*
	 * String url = "http://127.0.0.1:18765/api/chat/queries?after=" + cursor + "&limit=50&wait=" + waitSeconds;
HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
connection.setRequestMethod("GET");
connection.setRequestProperty("Authorization", "Bearer " + token);
connection.setRequestProperty("Content-Type", "application/json");
connection.setConnectTimeout(10000); // 10 seconds
connection.setReadTimeout((waitSeconds + 10) * 1000); // wait + 10 seconds timeout

int responseCode = connection.getResponseCode();
// Process the response...

Initial value of cursor is set to status.event_cursor, and then updated to the last event's cursor after processing each batch of events. 
This ensures that we only retrieve new events in subsequent requests.



	 */
}
