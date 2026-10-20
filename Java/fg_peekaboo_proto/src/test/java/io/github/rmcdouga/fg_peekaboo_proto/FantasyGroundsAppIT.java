package io.github.rmcdouga.fg_peekaboo_proto;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rmcdouga.fg_peekaboo_proto.JsonData.JsonDataFactory;
import tools.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@Disabled("This test is only for manual integration testing on a machine with both Ollama and Peekaboo installed.")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({ "disable-main-app-runner"})
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class FantasyGroundsAppIT {

	private final FantasyGroundsApp underTest;
	
	public FantasyGroundsAppIT(@Autowired PeekabooClient peekabooMcpClient, @Autowired UiLocator uiLocator) {
		 this.underTest = new FantasyGroundsApp(peekabooMcpClient, uiLocator);
	}

	@Test
	void testProcessId() {
		System.out.println("Found FG process: %d".formatted(FantasyGroundsApp.processId()));
	}

}
