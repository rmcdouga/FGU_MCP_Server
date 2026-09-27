package io.github.rmcdouga.fgumcpserver.fgu_data.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ImageTest {
	
	@ParameterizedTest
	@ValueSource(strings = {"image.jpg", "image.png", "image.gif", "image.bmp", "image.webp"})
	void testName_withExtension(String filename, @TempDir Path tempDir) {
		var underTest = createPathImage(tempDir, filename);
		assertThat(underTest.name()).isEqualTo("image");
	}

	@Test
	void testCreate_noExtension(@TempDir Path tempDir) {
		var filename = "image";
		assertThatThrownBy(() -> createPathImage(tempDir, filename))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("No extension found in file name")
			.hasMessageContaining("image");
	}

	@ParameterizedTest
	@CsvSource({
		"image.jpg,JPEG", 
		"image.jpeg,JPEG", 
		"image.png,PNG", 
		"image.gif,GIF", 
		"image.bmp,BMP", 
		"image.webp,WEBP"
		})
	void testFormat_withExtension(String filename, Image.ImageFormat expectedFormat, @TempDir Path tempDir) {
		var underTest = createPathImage(tempDir, filename);
		assertThat(underTest.format()).isEqualTo(expectedFormat);
	}

	@Disabled("Not yet implemented")
	@Test
	void testInputStream() {
		fail("Not yet implemented");
	}

	@Disabled("Not yet implemented")
	@Test
	void testFormat() {
		fail("Not yet implemented");
	}

	private static Image createPathImage(Path dir, String filename) {
		try {
			Path filePath = dir.resolve(filename);
			Files.writeString(filePath, "Some test content");
			return Image.PathImage.create(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Error creating test file: " + filename, e);
		}
	}
}
