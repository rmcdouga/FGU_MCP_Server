package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import java.nio.file.Path;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguImagesStore;

public class FileSystemFguImagesStore implements FguImagesStore {
	static final String IMAGES_DIR_NAME = "images";
	
	private final Path imagesPath;

	FileSystemFguImagesStore(Path imagesPath) {
		this.imagesPath = imagesPath;
	}
	
	
}
