package io.github.rmcdouga.fgumcpserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class TextToolsStdioMcpIntegrationTest {
	
	private static final String SAMPLE_TEXT = "This is some text that contains “left and right quotes” and some since ‘left and right single-quotes’.";
	private static final String EXPECTED_RESULT = "This is some text that contains \"left and right quotes\" and some since \'left and right single-quotes\'.";

	@AutoClose
    private McpSyncClient mcpClient = launchMcp();

    @BeforeEach
    void setUp() {
        // Handshake initialization with the server
        this.mcpClient.initialize();
    }

    @Test
    void testToolIsAvailable() {
        // 1. Assert that the server exposes your @McpTool configurations
        McpSchema.ListToolsResult toolsResult = mcpClient.listTools();
        List<String> toolNames = toolsResult.tools().stream().map(Tool::name).toList();
		assertThat(toolNames).isNotEmpty();
	    assertThat(toolNames).contains("sanitizeTextForFgu");
    }
    
    @Test
    void testToolWorks() {
    	CallToolRequest callToolRequest = CallToolRequest.builder("sanitizeTextForFgu")
				.arguments(Map.of("textIn", SAMPLE_TEXT))
				.build();
		CallToolResult result = mcpClient.callTool(callToolRequest);
		Content content = result.content().getFirst();
		if (!(content instanceof TextContent textContent)) {
			Assertions.fail("Expected a text content result, but got: " + content);
			throw new IllegalStateException("Expected a text content result, but got: " + content);
		}
		assertThat(textContent.text()).contains(EXPECTED_RESULT);
    	
    }
    
	private McpSyncClient launchMcp() {
		// Define how the Client spins up the server via Java process
        ServerParameters params = ServerParameters.builder("/opt/homebrew/bin/jbang")
                .args(
                    "run",         								// Run the jar
                    "--java=25",   								// using java 25
                    "target/fg_mcp_server-0.0.1-SNAPSHOT.jar"   // jar must have been built first via `mvn package`
                )
                .build();

//        ServerParameters params = ServerParameters.builder("java")
//                .args(
//                    "-Dspring.ai.mcp.server.stdio=true",         // Force STDIO mode
//                    "-Dspring.main.web-application-type=none",   // Turn off HTTP server
//                    "-Dlogging.pattern.console=",                // Mute console pattern logs
//                    "-Dlogging.level.root=OFF",                  // Avoid messing up STDOUT protocol
//                    "-cp", System.getProperty("java.class.path"),// Pass current classpath
//                    "com.example.mcp.server.McpServerApplication"// Target main application class
//                )
//                .build();

        // Bind the client to the subprocess STDIO streams
        return McpClient.sync(new StdioClientTransport(params, McpJsonDefaults.getMapper())).build();
	}


}