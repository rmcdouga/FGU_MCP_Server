package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class PeekabooClientCliTest extends AbstractPeekabooClientTest {
	
	public PeekabooClientCliTest() {
		super(new PeekabooClientCli(Path.of("/", "opt", "homebrew", "bin", "peekaboo")));
	}

}
