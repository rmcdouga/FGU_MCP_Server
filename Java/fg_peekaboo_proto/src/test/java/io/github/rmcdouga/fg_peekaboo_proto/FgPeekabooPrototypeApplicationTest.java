package io.github.rmcdouga.fg_peekaboo_proto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({ "disable-main-app-runner"})
class FgPeekabooPrototypeApplicationTest {

	@Test
	void contextLoads() {
	}

}
