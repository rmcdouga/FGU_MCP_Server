package io.github.rmcdouga.fgumcpserver.fgu_data.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public sealed interface Image {
	enum ImageFormat {
		JPEG, PNG, GIF, BMP, WEBP
	};
	
	public String name();
	public InputStream inputStream() throws IOException;
	public ImageFormat format();
	
	public record PathImage(Path filePath) implements Image {

		@Override
		public InputStream inputStream() throws IOException {
			return Files.newInputStream(filePath);
		}

		@Override
		public String name() {
			return removeExtension(filePath.getFileName().toString());
		}

		@Override
		public ImageFormat format() {
			return determineFormatFromExtension(filePath.getFileName().toString());
		}
		
		public static PathImage create(Path filePath) {
			try {
				if (!Files.exists(filePath)) {
					throw new IllegalArgumentException("File does not exist: " + filePath);
				}
				if (!Files.isRegularFile(filePath)) {
					throw new IllegalArgumentException("Path is not a regular file: " + filePath);
				}
				if (!Files.isReadable(filePath)) {
					throw new IllegalArgumentException("File is not readable: " + filePath);
				}
				if (Files.size(filePath) == 0) {
					throw new IllegalArgumentException("File is empty: " + filePath);
				}
				if (determineFormatFromExtension(filePath.getFileName().toString()) == null) {
					throw new IllegalArgumentException("File has an unknown format: " + filePath);
				}
				return new PathImage(filePath);
			} catch (IOException e) {
				throw new IllegalArgumentException("Error accessing file: " + filePath, e);
			}
		}
	}
	
//	public record Base64Image(String base64Data, String name, ImageFormat format) implements Image {
//
//		@Override
//		public InputStream inputStream() throws IOException {
//			byte[] decoded = Base64.getDecoder().decode(base64Data);
//			return new ByteArrayInputStream(decoded);
//		}
//
//		@Override
//		public String name() {
//			return removeExtension(name);
//		}
//
//		@Override
//		public ImageFormat format() {
//		}
//		
//		/**
//		 * Creates a Base64Image with the specified base64Data and name, using the extension of the name to determine the image format. If the extension is not recognized, the format will be set to null.
//		 * 
//		 * @param base64Data
//		 * @param name
//		 * @return
//		 */
//		public static Base64Image create(String base64Data, String name) {
//			return new Base64Image(base64Data, name, format);
//		}
//		
//		/**
//		 * Creates a Base64Image with the specified base64Data, name, and mimeType. The mimeType is used to determine the image format. If the mimeType is not recognized, the format will be set to null.
//		 * 
//		 * @param base64Data
//		 * @param name
//		 * @param mimeType
//		 * @return
//		 */
//		public static Base64Image create(String base64Data, String name, String mimeType) {
//			return new Base64Image(base64Data, name, format);
//		}
//	}

	private static String removeExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex > 0)  ? fileName.substring(0, dotIndex) : fileName;
	}
	
	private static ImageFormat determineFormatFromExtension(String fileName) {
		String extension = getFileExtension(fileName).toLowerCase().trim();
		return switch (extension) {
			case "jpg", "jpeg" -> ImageFormat.JPEG;
			case "png" -> ImageFormat.PNG;
			case "gif" -> ImageFormat.GIF;
			case "bmp" -> ImageFormat.BMP;
			case "webp" -> ImageFormat.WEBP;
			case "" -> throw new IllegalArgumentException("No extension found in file name: " + fileName); // No extension
			default -> throw new IllegalArgumentException("Unknown format (" + extension + ")"); // Unknown format
		};
	}
	
	private static String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex > 0) ? fileName.substring(dotIndex + 1) : "";
	}
	
}
