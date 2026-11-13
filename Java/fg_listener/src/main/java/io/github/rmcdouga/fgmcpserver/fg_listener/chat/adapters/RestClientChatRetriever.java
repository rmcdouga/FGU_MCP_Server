package io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever;

public class RestClientChatRetriever implements ChatRetriever {
	private static final Logger log = LoggerFactory.getLogger(RestClientChatRetriever.class);
	private static final String SERVER_STATUS_PATH = "/api/status";
	
	private final RestClient restClient;			// RestClient for making HTTP requests to the server.  This is used to retrieve chat messages and check the server status.
	private final URI statusUri;					// URI for the server status endpoint.  This is used to check if the server is up and running.
	
	public RestClientChatRetriever(RestClient.Builder restClientBuilder, UriBuilder uriBuilder) {
		this(restClientBuilder.build(), uriBuilder);
	}

	private RestClientChatRetriever(RestClient restClient, UriBuilder uriBuilder) {
		this.restClient = restClient;
		this.statusUri = uriBuilder.path(SERVER_STATUS_PATH)
								   .build();
		super();
	}
	
	@Override
	public Optional<BridgeStatus> bridgeStatus() {
		try {
			var status = restClient.get()
								   .uri(statusUri)
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
