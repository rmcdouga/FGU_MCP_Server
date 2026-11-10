package io.github.rmcdouga.mcp_info;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.Tool;

@SpringBootApplication
public class FgMcpClientApplication {
	private static final Logger log = LoggerFactory.getLogger(FgMcpClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FgMcpClientApplication.class, args);
	}
	
    @Bean
    ApplicationRunner printMcpClients(List<McpSyncClient> clients) {
		return _ -> {
			log.info("MCP Clients:");
			for (McpSyncClient client : clients) {
				log.info("  - " + client.getServerInfo().name() + " (" + client.getClass().getName() + ")");
		        System.out.println("\n=== Direct MCP call (no model) ===");

		        // 1. List all tools exposed by this tool
		        String serverName = client.getServerInfo().name();
		        List<Tool> tools = client.listTools().tools();
				System.out.println("Available %s tools (%d):".formatted(serverName, tools.size()));
		        tools.forEach(tool -> {
		            System.out.println("  - " + tool.name() + ": " + tool.description());
		            System.out.println("      - inputSchema: " + Objects.toString(tool.inputSchema()));
		            System.out.println("      - outputSchema: " + Objects.toString(tool.outputSchema()));
		        	});
		        List<Prompt> prompts = client.listPrompts().prompts();
				System.out.println("Available %s prompts (%d):".formatted(serverName, prompts.size()));
				prompts.forEach(prompt ->
		            System.out.println("  - " + prompt.name() ));// + ": " + tool.description()));
		        List<Resource> resources = client.listResources().resources();
				System.out.println("Available %s resources (%d):".formatted(serverName, resources.size()));
				resources.forEach(resource ->
		            System.out.println("  - " + resource.name() ));// + ": " + tool.description()));
			}
		};
	}
	    

}