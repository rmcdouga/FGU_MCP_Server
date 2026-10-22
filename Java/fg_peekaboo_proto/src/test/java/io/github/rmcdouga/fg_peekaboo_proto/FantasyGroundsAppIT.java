package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rmcdouga.fg_peekaboo_proto.FantasyGroundsApp.FantasyGroundsProcess.FantasyGroundsTab;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance.Lifecycle;

//@Disabled("This test is only for manual integration testing on a machine with Fantasy Grounds, Ollama and Peekaboo installed.")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({ "disable-main-app-runner"})
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class FantasyGroundsAppIT {

	protected final FantasyGroundsApp underTest;
	
	public FantasyGroundsAppIT(@Autowired PeekabooClient peekabooMcpClient, @Autowired UiLocator uiLocator, @TempDir Path tempDir) {
		 this.underTest = new FantasyGroundsApp(peekabooMcpClient, uiLocator, ()->tempDir);
	}

	@Test
	@Order(1)
	void testLaunch() {
		FantasyGroundsTab fgTab = underTest.startProcess();
		assertThat(fgTab).isNotNull();
		
		assertThat(FantasyGroundsApp.processId())
			.as("Should have found the Fantasy Grounds process ID")
			.isPresent()
			.isNotEqualTo(0L);
	}

	@Nested
	class FantasyGroundsTabTests {
		private FantasyGroundsTab underTest = FantasyGroundsAppIT.this.underTest.startProcess();
		
		@Test
		@Order(2)
		void testUpdateAvailable() throws InterruptedException {
			if (underTest.updateAvailable()) {
				// TODO: Eventually, there should be an elicitation about whether to perform the update or not.  
				//       For now, we will just click "OK" to confirm the update.
				underTest = underTest.performUpdate();
			}
			assertThat(underTest.updateAvailable())
				.as("After performing an update, the Fantasy Grounds tab should not indicate that an update is available.")
				.isFalse();
			
		}
	}
}
