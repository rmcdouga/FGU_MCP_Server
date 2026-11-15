package io.github.rmcdouga.fgmcpserver.fg_listener;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.databind.json.JsonMapper;

class ConfigJsonTest {

	private static final String SAMPLE_JSON = """
			{
			"token": "1234567890",
			"port": 18775,
			"source_roots": {
			  "CoreRPG": "/SmiteWorks/Fantasy Grounds/CoreRPG",
			  "5E": "/SmiteWorks/Fantasy Grounds/5E"
			},
			"fg_data": "/SmiteWorks/Fantasy Grounds"
			}
			""";

	private final ConfigJson.ConfigJsonFactory factory = new ConfigJson.ConfigJsonFactory(new JacksonJsonData.JacksonJsonDataFactory(new JsonMapper())::parseJson);
	
	@Test
	void testFromString() {
		// Act
		var config = factory.fromString(SAMPLE_JSON);
		
		// Assert
		assertEquals("1234567890", config.token());
		assertEquals(18775, config.port());
		assertEquals(2, config.sourceRoots().size());
		assertEquals("/SmiteWorks/Fantasy Grounds/CoreRPG", config.sourceRoots().get("CoreRPG").toString());
		assertEquals("/SmiteWorks/Fantasy Grounds/5E", config.sourceRoots().get("5E").toString());
		assertEquals("/SmiteWorks/Fantasy Grounds", config.fg_data().toString());
	}

	@Test
	void testFromPath(@TempDir Path tempDir) throws Exception {
		// Arrange
		var configJsonPath = tempDir.resolve("config.json");
		Files.writeString(configJsonPath, SAMPLE_JSON);
		
		// Act
		var config = factory.fromPath(configJsonPath);
		
		// Assert
		assertEquals("1234567890", config.token());
		assertEquals(18775, config.port());
		assertEquals(2, config.sourceRoots().size());
		assertEquals("/SmiteWorks/Fantasy Grounds/CoreRPG", config.sourceRoots().get("CoreRPG").toString());
		assertEquals("/SmiteWorks/Fantasy Grounds/5E", config.sourceRoots().get("5E").toString());
		assertEquals("/SmiteWorks/Fantasy Grounds", config.fg_data().toString());
	}
}
