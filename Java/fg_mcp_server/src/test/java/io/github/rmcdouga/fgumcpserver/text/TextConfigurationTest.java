package io.github.rmcdouga.fgumcpserver.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;

import io.github.rmcdouga.fgumcpserver.text.adapters.in.TextTools;

@ApplicationModuleTest(mode = BootstrapMode.STANDALONE) 
class TextConfigurationTest {

	@Autowired ApplicationContext context;
	
	@Test
	void configurationLoads() {
		assertNotNull(context);
		assertNotNull(context.getBean(TextTools.class));
	}

}
