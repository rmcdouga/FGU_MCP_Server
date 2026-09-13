package io.github.rmcdouga.fgumcpserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.Tool;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TextToolsMcpIntegrationTest {

	@LocalServerPort
    private int port;

    private McpSyncClient client;
    
    @BeforeEach
	void setUp() throws Exception {
    	client = McpClient.sync(HttpClientStreamableHttpTransport.builder("http://localhost:" + port)
    															 .endpoint("/mcp")
    															 .build())
    					  .build();
    	client.initialize();
	}

	@AfterEach
	void tearDown() throws Exception {
		client.closeGracefully();
	}

	@Test
	void whenMcpClientListTools_thenTheToolIsRegistered() {
	    List<String> toolNames = client.listTools().tools().stream().map(Tool::name).toList();
	    assertThat(toolNames).contains("sanitizeTextForFgu");
	}
}
