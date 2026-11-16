package io.github.rmcdouga.fgmcpserver.fg_listener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataException;
import io.github.rmcdouga.fgmcpserver.fg_listener.chat.AiChatProcessor;
import io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters.RestClientChatRetriever;
import io.github.rmcdouga.fgmcpserver.fg_listener.chat.ports.ChatRetriever;
import tools.jackson.databind.json.JsonMapper;

@SpringBootApplication
public class FgListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FgListenerApplication.class, args);
	}

	@Bean
	JsonData.JsonDataFactory jsonData(JsonMapper jsonMapper) {
		var jsonFactory = new JacksonJsonData.JacksonJsonDataFactory(jsonMapper);
		return jsonFactory::parseJson;
	}
	
	@Bean
	ConfigJson configJson(JsonData.JsonDataFactory jsonData, @Value("${fglistener.config.path:/Users/robmcdougall/SmiteWorks/FG-MCP-Server}")Path configJsonPath) throws JsonDataException, IOException {
		ConfigJson.ConfigJsonFactory factory = new ConfigJson.ConfigJsonFactory(jsonData);
		return factory.fromPath(configJsonPath);
	}
	
	@Bean
	ChatRetriever chatRetriever(RestClient.Builder restClientBuilder, ConfigJson configJson) {
		return new RestClientChatRetriever(restClientBuilder, "http://127.0.0.1:" + configJson.port(), configJson.token());
	}
	
	@Bean
	AiChatProcessor aiChatProcessor(ChatRetriever chatRetriever) {
		return new AiChatProcessor(chatRetriever, Executors.newVirtualThreadPerTaskExecutor());
	}
	
	@Profile("!disable-main-app-runner")
	@Bean
    ApplicationRunner callAiChatProcessor(AiChatProcessor aiChatProcessor) {
        return _ -> {
        	// Start processing chat messages in a separate thread.
			aiChatProcessor.startProcessing();
			// Wait for Q in the console to stop processing.
			
			
        };
    }

}
