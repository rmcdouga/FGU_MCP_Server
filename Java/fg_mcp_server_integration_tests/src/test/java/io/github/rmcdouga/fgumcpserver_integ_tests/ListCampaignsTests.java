package io.github.rmcdouga.fgumcpserver_integ_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ListCampaignsTests {
	private static Path tempDir;
	
	ListCampaignsTests(@TempDir Path tempDir) {
		tempDir = tempDir.toAbsolutePath();
	}

	@DynamicPropertySource
	static void configureDynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.ai.mcp.client.stdio.connections.fg_mcp_server_integration_tests.args", ()->constructArgs());
	}

	static String constructArgs() {
		return "run,--java=25,-Dfgumcpserver.data.fguBaseDir=" + tempDir + ",../fg_mcp_server/target/fg_mcp_server-0.2.0-SNAPSHOT.jar";
	}
	@Test
	void testListCampaigns(@Autowired ChatClient.Builder chatClientBuilder) throws IOException {
		Files.createDirectories(tempDir.resolve("campaigns").resolve("campaign1"));
		Files.createDirectories(tempDir.resolve("campaigns").resolve("campaign2"));
		
	   	var result = chatClientBuilder.build()
   				.prompt()
   				.user("List the Fantasy Grounds Campaigns that are available?")
   				.call()
   				.content();
	   	
	   	System.out.println("Available campaigns: " + result);
   	   	assertThat(result).contains("campaign1", "campaign2");		
	}
}
