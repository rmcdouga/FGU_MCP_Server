package io.github.rmcdouga.fg_peekaboo_proto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;

public class UiLocator {
	
	public record ButtonLocation(int x, int y) {
		public static ButtonLocation fromBoundingBox(int x, int y, int width, int height) {
			int centerX = x + width / 2;
			int centerY = y + height / 2;
			return new ButtonLocation(centerX, centerY);
		}
		
		public String toString() {
			return x + "," + y;
		}
	}
	
	private final ChatClient chatClient;

	public UiLocator(ChatClient.Builder chatClientBuilder) {
		this(chatClientBuilder.build());
	}

	public UiLocator(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	public ButtonLocation locateButton(String buttonText, Path uiScreenshotPath) {
			String result = chatClient.prompt()
					  .user(p->p.media(toMedia(uiScreenshotPath))
							  	.text("Please describe the contents of the attached screenshot.")
							)
					  .call()
					  .content();
			
			System.out.println("Model response:\n" + result);
			return null;	// TBD: Implement actual button location logic based on model response
	}

	private Media toMedia(Path uiScreenshotPath) {
		try {
			return Media.builder().mimeType(MimeType.valueOf("image/png"))
								  .data(Files.readAllBytes(uiScreenshotPath))
								  .build();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read screenshot file: " + uiScreenshotPath, e);
		}
	}
	
	private URL toUrl(Path uiScreenshotPath) {
		try {
			return uiScreenshotPath.toAbsolutePath().toUri().toURL();
		} catch (MalformedURLException e) {
			// Should never happen, but if it does, wrap it in a runtime exception
			throw new IllegalStateException("Failed to convert screenshot path to URL: " + uiScreenshotPath, e);
		}
	}
}
