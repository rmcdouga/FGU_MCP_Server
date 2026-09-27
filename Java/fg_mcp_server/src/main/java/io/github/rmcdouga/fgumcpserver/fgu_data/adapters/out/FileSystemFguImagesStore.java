package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import java.nio.file.Path;

import org.jspecify.annotations.Nullable;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.Image;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguImagesStore;

public class FileSystemFguImagesStore implements FguImagesStore {
	static final String IMAGES_DIR_NAME = "images";
	
	private final Path imagesPath;

	FileSystemFguImagesStore(Path imagesPath) {
		this.imagesPath = imagesPath;
	}

	// TODO: Implement conversion to recommended image format: 
	//       https://www.fantasygrounds.com/forums/showthread.php?80115-Image-and-Map-sizing-suggestions
	
	/**
	 *	Saves the given image to the Fantasy Grounds campaign's image store with the specified name.
	 *
	 *	@param imageName the name to save the image as (with no extension), can be null or empty to use the image's original name
	 *	@param image 	 the image to save
	 */
	@Override
	public void importImage(@Nullable String imageName, Image image) {
		String nameToUse = (imageName == null || imageName.isBlank()) ? image.name() : imageName;
		Path targetFilePath = imagesPath.resolve(nameToUse + "." + image.format().name().toLowerCase());
		
		try (var inputStream = image.inputStream()) {
			java.nio.file.Files.copy(inputStream, targetFilePath);
		} catch (java.io.IOException e) {
			throw new RuntimeException("Failed to import image: " + nameToUse, e);
		}		
	}
}
