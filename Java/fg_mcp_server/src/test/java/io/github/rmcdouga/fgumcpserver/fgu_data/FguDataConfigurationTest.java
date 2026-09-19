package io.github.rmcdouga.fgumcpserver.fgu_data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;

import io.github.rmcdouga.fgumcpserver.fgu_data.adapters.in.FguDataTools;

@ApplicationModuleTest(mode = BootstrapMode.STANDALONE) 
class FguDataConfigurationTest {

	@Autowired ApplicationContext context;
	
	@Test
	void testContextLoads() {
		assertNotNull(context);
		assertNotNull(context.getBean(FguDataTools.class));
	}

}
