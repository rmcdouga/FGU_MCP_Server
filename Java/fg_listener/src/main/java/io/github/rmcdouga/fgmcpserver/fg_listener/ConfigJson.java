package io.github.rmcdouga.fgmcpserver.fg_listener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataException;
import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataFactory;

/*
 * 
 */
public record ConfigJson(
		String token,
		int port,
		Map<String, Path> sourceRoots,
		Path fg_data
		) {
	
	
	public static class ConfigJsonFactory {
		private final JsonData.JsonDataFactory jsonFactory;

		public ConfigJsonFactory(JsonDataFactory jsonFactory) {
			this.jsonFactory = jsonFactory;
			super();
		}

		public ConfigJson fromPath(Path configJsonPath) throws JsonDataException, IOException {
			if (Files.isDirectory(configJsonPath)) {
				configJsonPath = configJsonPath.resolve("config.json");
			}
			return fromString(Files.readString(configJsonPath));
		}

		public ConfigJson fromString(String jsonString) throws JsonDataException {
			var jsonData = jsonFactory.fromJsonString(jsonString);
			var token = jsonData.getStringAt("/token").orElseThrow();
			var port = jsonData.getIntAt("/port").orElseThrow();
			var sourceRoots = jsonData.toMapOfString("/source_roots")
									  .entrySet().stream()
									  .collect(Collectors.toMap(
											  e->e.getKey(),
											  e->Path.of(e.getValue())
											  ));
			var fg_data = Path.of(jsonData.getStringAt("/fg_data").orElseThrow());
			return new ConfigJson(token, port, sourceRoots, fg_data);
		}
	}
}


