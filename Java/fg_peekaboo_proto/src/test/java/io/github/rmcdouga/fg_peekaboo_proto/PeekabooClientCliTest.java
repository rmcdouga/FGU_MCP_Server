package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;

class PeekabooClientCliTest extends AbstractPeekabooClientTest {
	
	public PeekabooClientCliTest() {
		super(new PeekabooClientCli(Path.of("/", "opt", "homebrew", "bin", "peekaboo"), JacksonJsonData::parseJson));
	}

}
