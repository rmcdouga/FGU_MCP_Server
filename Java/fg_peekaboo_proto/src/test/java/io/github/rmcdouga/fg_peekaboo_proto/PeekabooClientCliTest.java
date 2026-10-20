package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;

import tools.jackson.databind.json.JsonMapper;

class PeekabooClientCliTest extends AbstractPeekabooClientTest {
	private static final JsonMapper jsonMapper = new JsonMapper();
	private static final JacksonJsonData.JacksonJsonDataFactory jsonFactory = new JacksonJsonData.JacksonJsonDataFactory(jsonMapper);
	
	public PeekabooClientCliTest() {
		super(new PeekabooClientCli(Path.of("/", "opt", "homebrew", "bin", "peekaboo"), jsonFactory::parseJson));
	}

}
