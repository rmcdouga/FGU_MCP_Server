package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguCampaign;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguDataStore;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguImagesStore;

public class FileSystemFguDataStore implements FguDataStore {
	private static final String MACOS_APP_DIR = "SmiteWorks";
	private static final String LINUX_APP_DIR = ".smiteworks";
	private static final String FANTASY_GROUNDS = "Fantasy Grounds";
	private static final String CAMPAIGNS_DIR = "campaigns";
	
	private final Path rootPath;

	/**
	 * Constructs a FileSystemFguDataStore with the default location where Fantasy Grounds
	 * stores files (i.e. the "data" directory).
	 */
	public FileSystemFguDataStore() {
		this(getDefaultRootPath());
	}
	
	public FileSystemFguDataStore(Path rootPath) {
		this.rootPath = Objects.requireNonNull(rootPath);
	}

	public Stream<FguCampaign> campaigns() {
		Path campaignsPath = rootPath.resolve(CAMPAIGNS_DIR);
		if (!Files.exists(campaignsPath) || !Files.isDirectory(campaignsPath)) {
			return Stream.empty(); // Return an empty list if the campaigns directory does not exist
		}
		
		try (Stream<Path> campaignDirs = Files.list(campaignsPath)) {
			return campaignDirs
					.filter(Files::isDirectory)
					.map(path -> new FileSystemFguCampaign(path))
					.map(FguCampaign.class::cast)
					.toList()						// Collect to a list so that we can close the stream and handle any IO Exceptions before returning.
					.stream();
		} catch (IOException e) {
			throw new FguDataStore.FguDataStoreException("Failed to list campaigns in " + campaignsPath, e);
		}
	}
	
	/**
	 * Gets the root path where Fantasy Grounds data is stored.
	 * 
	 * Visible for testing purposes only.
	 * 
	 * @return the root path
	 */
	public Path rootPath() {
		return rootPath;
	}

	private static Path getDefaultRootPath() {
		// If MacOS, the default location is ~/SmiteWorks/Fantasy Grounds
		// If Windows, the default location is %AppData%\Fantasy Grounds\
		// If Linux, the default location is ~/.smiteworks/
		String osName = System.getProperty("os.name").toLowerCase();
		String userHome = System.getProperty("user.home");
		
		if (osName.contains("mac")) {
			return Path.of(userHome, MACOS_APP_DIR, FANTASY_GROUNDS);
		} else if (osName.contains("windows")) {
			String appData = System.getenv("APPDATA");
			if (appData != null) {
				return Path.of(appData, FANTASY_GROUNDS);
			} else {
				// Fallback if APPDATA is not set
				return Path.of(userHome, "AppData", "Roaming", FANTASY_GROUNDS);
			}
		} else {
			// Linux and other systems
			return Path.of(userHome, LINUX_APP_DIR);
		}
	}

	@Override
	public FguImagesStore imagesStore() {
		return new FileSystemFguImagesStore(rootPath.resolve(FileSystemFguImagesStore.IMAGES_DIR_NAME));
	}
}
