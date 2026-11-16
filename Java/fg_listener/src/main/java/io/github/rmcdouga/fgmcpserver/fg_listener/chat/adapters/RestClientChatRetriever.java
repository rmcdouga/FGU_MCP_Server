package io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters;

import java.time.Duration;
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
	private static final String CHAT_STATUS_PATH = "/api/chat/status"; // POST http://127.0.0.1:18765/api/chat/status
	private static final String CHAT_PATH = "/api/chat"; // GET http://127.0.0.1:18765/api/chat?after=0&limit=50&private=true
	
	
	private final RestClient restClient;			// RestClient for making HTTP requests to the server.  This is used to retrieve chat messages and check the server status.
	
	public RestClientChatRetriever(RestClient.Builder restClientBuilder, String baseUrl, String token) {
		this(restClientBuilder.baseUrl(baseUrl)
				   			  .defaultHeader("Authorization", "Bearer " + token)
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
	public Optional<QueryBatch> pollForChatEvents(int cursor, Duration waitTime) {
		try {
			var events = restClient.get()
					   .uri(ub->ub.path(CHAT_EVENTS_PATH)
					   			  .queryParam("after", cursor)
					   			  .queryParam("limit", 50)
					   			  .queryParam("wait", waitTime.toSeconds())
					   			  .build())
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
