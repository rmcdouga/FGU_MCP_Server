package io.github.rmcdouga.fgumcpserver.fgu_data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;
import org.springframework.test.context.TestPropertySource;

import io.github.rmcdouga.fgumcpserver.fgu_data.adapters.in.FguDataTools;
import io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out.FileSystemFguDataStore;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguDataStore;

class FguDataConfigurationTest {

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE) 
	class TestWithNoProperties {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			assertNotNull(context.getBean(FguDataTools.class));
		}
	}

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE)
	@TestPropertySource(properties = "fgumcpserver.data.enabled=false")
	class TestWithDataDisabledProperty {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			assertThrows(NoSuchBeanDefinitionException.class, ()->context.getBean(FguDataTools.class));
		}
	}

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE)
	@TestPropertySource(properties = "fgumcpserver.data.enabled=true")
	class TestWithDataEnabledProperty {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			assertNotNull(context.getBean(FguDataTools.class));
		}
	}

	@Nested
	@ApplicationModuleTest(mode = BootstrapMode.STANDALONE)
	@TestPropertySource(properties = "fgumcpserver.data.fguBaseDir=/foo/bar")
	class TestWithCustomFguBaseDirProperty {
		
		@Autowired ApplicationContext context;
		
		@Test
		void testContextLoads() {
			assertNotNull(context);
			var fguDataStore = context.getBean(FguDataStore.class);
			if (fguDataStore instanceof FileSystemFguDataStore fsStore) {
				assertEquals("/foo/bar", fsStore.rootPath().toString());
			} else {
				fail("Expected FguDataStore to be an instance of FileSystemFguDataStore");
			}
		}
	}
}
