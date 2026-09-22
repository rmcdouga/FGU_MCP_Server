package io.github.rmcdouga.fgumcpserver.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;
import org.springframework.test.context.TestPropertySource;

import io.github.rmcdouga.fgumcpserver.text.adapters.in.TextTools;

class TextConfigurationTest {

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE) 
	class TestWithNoProperties {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			assertNotNull(context.getBean(TextTools.class));
		}
	}

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE)
	@TestPropertySource(properties = "fgumcpserver.text.enabled=false")
	class TestWithDataDisabledProperty {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			assertThrows(NoSuchBeanDefinitionException.class, ()->context.getBean(TextTools.class));
		}
	}

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE)
	@TestPropertySource(properties = "fgumcpserver.text.enabled=true")
	class TestWithDataEnabledProperty {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			assertNotNull(context.getBean(TextTools.class));
		}
	}
}
