package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;

import tools.jackson.databind.json.JsonMapper;

@Disabled("This test is only for manual integration testing on a machine with both Fantasy Grounds and Peekaboo installed.")
class PeekabooClientCliIT extends AbstractPeekabooClientIT {
	private static final JsonMapper jsonMapper = new JsonMapper();
	private static final JacksonJsonData.JacksonJsonDataFactory jsonFactory = new JacksonJsonData.JacksonJsonDataFactory(jsonMapper);
	
	public PeekabooClientCliIT() {
		super(new PeekabooClientCli(Path.of("/", "opt", "homebrew", "bin", "peekaboo"), jsonFactory::parseJson));
	}

}
