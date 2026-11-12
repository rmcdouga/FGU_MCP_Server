package io.github.rmcdouga.fgmcpserver.fg_listener;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

@Disabled("This test is for manual testing of the bridge, not for automated testing.")
@SpringBootTest(webEnvironment = WebEnvironment.NONE, 
				classes = FgListenerApplication.class
				)
class BridgeTest {

	private final RestClient restClient;			// RestClient for making HTTP requests to the server.  This is used to retrieve chat messages and check the server status.
	private final UriBuilder uriBuilder;			// URI for the server bridge endpoint.  This is used to check if the server is up and running.
	
	public BridgeTest(@Autowired RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
		this.uriBuilder = new DefaultUriBuilderFactory().builder();
		super();
	}

//**Summary of the parts:**
//
//
//- **Host/port:** `127.0.0.1:18765` (a local loopback bridge)
//- **Path:** `/bridge/hello`
//- **Query params:**
//  - `token` = `9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11`
//  - `session` = `1794436466-188221`
//  - `info` = `{"ruleset":"E","host":true,"version":"0.4.4","campaign":"SimpleTestCampaign"}`
//
//	"f368c1cdc1fb7c4e9d04397552e626e0a9afd51757f26116cdd48941eef56247"
//
//This looks like a local MCP bridge handshake (ruleset "E" = D&D 5e, campaign "SimpleTestCampaign", bridge version 0.4.4). Want me to verify it against the `fg_mcp_server` bridge in your workspace?
//	
	@Test
	void test() {
		var uri = uriBuilder.scheme("http")
							.host("127.0.0.1")
							.port(18765)
							.path("/bridge/hello")
							.queryParam("token", "9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11")
							.queryParam("session", "1794436466-188221")
							.queryParam("info", "{\"ruleset\":\"E\",\"host\":true,\"version\":\"0.4.4\",\"campaign\":\"SimpleTestCampaign\"}")
							.build(false);
		
		var response = restClient.get()
								 .uri(uri)
								 .retrieve()
								 .body(String.class);
		
		System.out.println("Response: " + response);
	}

}
