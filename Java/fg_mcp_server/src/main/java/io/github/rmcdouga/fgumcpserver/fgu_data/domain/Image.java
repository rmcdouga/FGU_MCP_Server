package io.github.rmcdouga.fgumcpserver.fgu_data.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public sealed interface Image {
	public InputStream inputStream() throws IOException;
	
	public record PathImage(Path filePath) implements Image {

		@Override
		public InputStream inputStream() throws IOException {
			return Files.newInputStream(filePath);
		}}
	
	public record Base64Image(String base64Data) implements Image {

		@Override
		public InputStream inputStream() throws IOException {
			byte[] decoded = Base64.getDecoder().decode(base64Data);
			return new ByteArrayInputStream(decoded);
		}}

}
