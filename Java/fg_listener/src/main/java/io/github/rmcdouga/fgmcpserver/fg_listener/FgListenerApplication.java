package io.github.rmcdouga.fgmcpserver.fg_listener;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataException;
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
}
