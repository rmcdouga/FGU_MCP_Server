package io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out;

import org.jspecify.annotations.Nullable;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.Image;

public interface FguImagesStore {
	
	/**
	 *	Saves the given image to this Fantasy Grounds image store with the specified name.
	 *
	 *	@param imageName the name to save the image as (with no extension), can be null or empty to use the image's original name
	 *	@param image 	 the image to save
	 */
	void importImage(@Nullable String imageName, Image image);
}
