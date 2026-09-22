package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguCampaign;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguDataStore;

@DisplayName("FileSystemFguDataStore Tests")
class FileSystemFguDataStoreTest {

	@Test
	@DisplayName("should construct with custom root path")
	void testConstructorWithCustomPath(@TempDir Path tempDir) throws IOException {
		// Arrange
		Path campaignsPath = tempDir.resolve("campaigns");
		Path campaign1 = campaignsPath.resolve("campaign1");
		Path campaign2 = campaignsPath.resolve("campaign2");
		Files.createDirectories(campaign1);
		Files.createDirectories(campaign2);

		// Act
		FguDataStore datastore = new FileSystemFguDataStore(tempDir);
		List<FguCampaign> campaigns = datastore.campaigns().toList();

		// Assert
		assertThat(campaigns).hasSize(2);
		assertThat(campaigns).extracting(FguCampaign::name)
				.containsExactlyInAnyOrder("campaign1", "campaign2");
	}

	@Test
	@DisplayName("should construct with default root path")
	void testConstructorWithDefaultPath() {
		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore();

		// Assert
		assertThat(store).isNotNull();
		assertThat(store.rootPath()).isNotNull();
		assertThat(store.rootPath()).isAbsolute();
	}

	@Test
	@DisplayName("default path should contain OS-specific directory")
	void testDefaultPathContainsOsSpecificDirectory() {
		// Arrange
		String osName = System.getProperty("os.name").toLowerCase();

		// Act
		FileSystemFguDataStore store = new FileSystemFguDataStore();

		// Assert
		assertThat(store).isNotNull();
		String pathString = store.rootPath().toString();
		if (osName.contains("mac")) {
			assertThat(pathString)
				.as("macOS path should contain 'SmiteWorks' and 'Fantasy Grounds'")
				.contains("SmiteWorks").contains("Fantasy Grounds");
		} else if (osName.contains("windows")) {
			assertThat(pathString)
				.as("Windows path should contain 'Fantasy Grounds'")
				.contains("Fantasy Grounds");
		} else {
			// Linux and other systems
			assertThat(pathString)
				.as("Linux path should contain '.smiteworks'")
				.contains(".smiteworks");
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
		assertThat(pathString)
			.as("Default path should be within the user's home directory")
			.startsWith(userHome);
	}

	@Test
	@DisplayName("should accept null custom path")
	void testConstructorWithNullPath() {
		// Act & Assert - verify it throws an exception
		assertThrows(NullPointerException.class, () -> new FileSystemFguDataStore(null));
	}
}
