package io.github.rmcdouga.fgmcpserver.fg_listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
}
