package io.github.rmcdouga.fgumcpserver_integ_tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ListCampaignsTests {

	@Test
	void testListCampaigns(@Autowired ChatClient.Builder chatClientBuilder) {
	   	var result = chatClientBuilder.build()
   				.prompt()
   				.user("What Fantasy Grounds Campaigns are available?")
   				.call()
   				.content();
	   	
	   	System.out.println("Available campaigns: " + result);
   	   	assertThat(result).contains("Tutorial 5E Campaign", "SimpleTestCampaign");		
	}
}
