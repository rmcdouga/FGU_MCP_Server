package io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData;
import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataException;
import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataFactory;
import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever;

public class RestClientChatRetriever implements ChatRetriever {
	private static final Logger log = LoggerFactory.getLogger(RestClientChatRetriever.class);
	private static final String SERVER_STATUS_PATH = "/api/status";
	
	private final RestClient restClient;			// RestClient for making HTTP requests to the server.  This is used to retrieve chat messages and check the server status.
	private final URI statusUri;					// URI for the server status endpoint.  This is used to check if the server is up and running.
	private final JsonDataFactory jsonDataFactory;	// Factory for creating JsonData objects from JSON strings.  This is used to parse the JSON responses from the server.
	
	public RestClientChatRetriever(RestClient.Builder restClientBuilder, UriBuilder uriBuilder, JsonDataFactory jsonDataFactory) {
		this(restClientBuilder.build(), uriBuilder, jsonDataFactory);
	}

	private RestClientChatRetriever(RestClient restClient, UriBuilder uriBuilder, JsonDataFactory jsonDataFactory) {
		this.restClient = restClient;
		this.statusUri = uriBuilder.path(SERVER_STATUS_PATH)
								   .build();
		this.jsonDataFactory = jsonDataFactory;
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
//			JsonData responseJson = jsonDataFactory.fromJsonString(status);	// This will throw an exception if the JSON is invalid, which indicates that the server is not up.
//			System.out.println("Server status: " + responseJson.toString());
//			return responseJson.getBooleanAt("/connected")
//							   .orElse(false);
			log.info("Server status: {}", status);
			return Optional.of(status);
			
		} catch (JsonDataException e) {
			log.error("Failed to parse server status JSON: " + e.getMessage(), e);
		} catch (ResourceAccessException e) {
			log.error("Failed to access server status endpoint: " + e.getMessage(), e);
		}
		return Optional.empty();
	}

}
