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
	private static final String BRIDGE_COMMAND_PATH = "/api/command";
	
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

	@Override
	public Optional<QueryBatch> pollForChatEvents(int cursor, int waitSeconds) {
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

	@Override
	public Optional<CommandResponse> bridgeCommand(BridgeCommand bridgeCommand) {
		try {
			var result = restClient.post()
					   			   .uri(ub->ub.path(BRIDGE_COMMAND_PATH)
					   					   	  .build())
					   			   .header("Authorization", "Bearer " + "9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11")
					   			   .body(bridgeCommand)
					   			   .retrieve()
					   			   .body(CommandResponse.class);
			
			log.info("Sent command, result: \n{}", result);
			return Optional.of(result);
		} catch (RestClientException e) {
			log.error("Failed to send command: " + e.getMessage(), e);
		}
		return Optional.empty();
	}
}
