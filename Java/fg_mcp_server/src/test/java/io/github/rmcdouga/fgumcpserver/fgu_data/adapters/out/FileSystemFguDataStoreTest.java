package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out.FileSystemFguDataStore;

import org.junit.jupiter.api.DisplayName;

@DisplayName("FileSystemFguDataStore Tests")
class FileSystemFguDataStoreTest {

	@Test
	@DisplayName("should construct with custom root path")
	void testConstructorWithCustomPath() {
		// Arrange
		Path customPath = Path.of("/custom/fantasy/grounds");

		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore(customPath);

		// Assert
		assertEquals(customPath, store.rootPath());
	}

	@Test
	@DisplayName("should construct with default root path")
	void testConstructorWithDefaultPath() {
		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore();

		// Assert
		assertNotNull(store.rootPath());
		assertTrue(store.rootPath().isAbsolute(), "Default root path should be absolute");
	}

	@Test
	@DisplayName("default path should contain OS-specific directory")
	void testDefaultPathContainsOsSpecificDirectory() {
		// Arrange
		String osName = System.getProperty("os.name").toLowerCase();

		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore();
		String pathString = store.rootPath().toString();

		// Assert
		if (osName.contains("mac")) {
			assertTrue(pathString.contains("SmiteWorks") && pathString.contains("Fantasy Grounds"),
					"macOS path should contain 'SmiteWorks' and 'Fantasy Grounds'");
		} else if (osName.contains("windows")) {
			assertTrue(pathString.contains("Fantasy Grounds"),
					"Windows path should contain 'Fantasy Grounds'");
		} else {
			// Linux and other systems
			assertTrue(pathString.contains(".smiteworks"),
					"Linux path should contain '.smiteworks'");
		}
	}

	@Test
	@DisplayName("default path should be in user home directory")
	void testDefaultPathInUserHome() {
		// Arrange
		String userHome = System.getProperty("user.home");

		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore();
		String pathString = store.rootPath().toString();

		// Assert
		assertTrue(pathString.startsWith(userHome),
				"Default root path should start with user home directory");
	}

	@Test
	@DisplayName("should not return null for default path")
	void testDefaultPathNotNull() {
		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore();

		// Assert
		assertNotNull(store.rootPath(), "Default root path should not be null");
	}

	@Test
	@DisplayName("should accept null custom path")
	void testConstructorWithNullPath() {
		// Act & Assert - verify it throws an exception
		assertThrows(NullPointerException.class, () -> new FileSystemFguDataStore(null));
	}
}
