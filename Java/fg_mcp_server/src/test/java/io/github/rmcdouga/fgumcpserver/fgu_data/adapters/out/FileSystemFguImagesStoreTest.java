package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.Image;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.Image.PathImage;

@ExtendWith(MockitoExtension.class)
class FileSystemFguImagesStoreTest {
	private final static byte[] TEST_IMAGE_DATA = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };

	private final Path imagesDir;
	private final FileSystemFguImagesStore underTest;
	
	@Mock private PathImage mockImage;
	
	
	public FileSystemFguImagesStoreTest(@TempDir Path imagesDir) {
		this.imagesDir = imagesDir;
		this.underTest = new FileSystemFguImagesStore(imagesDir);
	}

	@Test
	void testImportImage_NoName() throws IOException {
		// Arrange
		String expectedName = "testImage";
		when(mockImage.name()).thenReturn(expectedName);
		when(mockImage.inputStream()).thenReturn(new java.io.ByteArrayInputStream(TEST_IMAGE_DATA));
		when(mockImage.format()).thenReturn(Image.ImageFormat.PNG);
		
		// Act
		underTest.importImage(null, mockImage);
		
		// Assert
		Path expectedFilePath = imagesDir.resolve(expectedName + ".png");
		assertThat(expectedFilePath)
			.as("Expected image file to be created at: " + expectedFilePath)
			.exists();
		assertThat(expectedFilePath)
			.binaryContent()
				.as("Expected image data to match test data")
				.isEqualTo(TEST_IMAGE_DATA);
	}

	@Test
	void testImportImage_NewName() throws IOException {
		// Arrange
		String expectedName = "testImage";
		when(mockImage.inputStream()).thenReturn(new java.io.ByteArrayInputStream(TEST_IMAGE_DATA));
		when(mockImage.format()).thenReturn(Image.ImageFormat.PNG);
		
		// Act
		underTest.importImage("testImage", mockImage);

		// Assert
		Path expectedFilePath = imagesDir.resolve(expectedName + ".png");
		assertThat(expectedFilePath)
			.as("Expected image file to be created at: " + expectedFilePath)
			.exists();
		assertThat(expectedFilePath)
			.binaryContent()
				.as("Expected image data to match test data")
				.isEqualTo(TEST_IMAGE_DATA);
	}
}
