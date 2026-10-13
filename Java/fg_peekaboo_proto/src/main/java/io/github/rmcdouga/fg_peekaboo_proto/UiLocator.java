package io.github.rmcdouga.fg_peekaboo_proto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;

public class UiLocator {
	// TODO: IMAGE SIZE is currently hardcoded, but it should be dynamically determined from the screenshot image.
	//       This is a temporary solution until we can get the actual image size from the screenshot.
	private static final int IMAGE_SIZE_X = 3176;
	private static final int IMAGE_SIZE_Y = 1924;
	private static final Logger log = LoggerFactory.getLogger(UiLocator.class);
	
	public record ButtonClickLocation(int x, int y) {
		public static ButtonClickLocation fromBoundingBox(int xmin, int ymin, int xmax, int ymax) {
			log.atDebug().log("Button Location: {}, {}, xmax: {}, ymax {}", xmin, ymin, xmax, ymax);
			int centerX = (xmin + xmax) / 2;
			int centerY = (ymin + ymax) / 2;
			return new ButtonClickLocation(centerX, centerY);
		}
		
		public String toString() {
			return x + "," + y;
		}
	}
	
	private final ChatClient chatClient;

	public UiLocator(ChatClient.Builder chatClientBuilder) {
		this(chatClientBuilder.build());
	}

	private UiLocator(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	public Optional<ButtonClickLocation> locateButton(String buttonText, Path uiScreenshotPath) {
		final record ButtonBoundingBox(String buttonName, int xmin, int ymin, int xmax, int ymax) {	
			public String toString() {
				return buttonName + ": " +  xmin + "," + ymin + "," + xmax + "," + ymax;
			}
		};
		
		@Nullable 
		ButtonBoundingBox result = chatClient.prompt()
					  					  	 .user(p->p.media(toMedia(uiScreenshotPath))
					  					  			   .text("Locate the " + buttonText + " button and return its bounding box coordinates [xmin, ymin, xmax, ymax]. **Mandatory** Don't guess, return 0 if unsure.")
					  					  		  )
					  					  	 .call()
					  					  	 .entity(ButtonBoundingBox.class, spec -> spec.useProviderStructuredOutput()
					  					  			 									  .validateSchema()
					  					  			);
			
		log.atDebug().log(()->"Model response: {}" + (result == null ? "null" : result.toString()));
		log.atDebug().log(()->"Model response (pixels): " + from1000(result.xmin,IMAGE_SIZE_X) + "," + from1000(result.ymin,IMAGE_SIZE_Y) + "," + from1000(result.xmax,3176) + "," + from1000(result.ymax,1924));
		return result.xmin == 0 && result.ymin == 0 && result.xmax == 0 && result.ymax == 0 
				? Optional.empty()
				: Optional.of(ButtonClickLocation.fromBoundingBox(from1000(result.xmin,3176), from1000(result.ymin,1924), from1000(result.xmax,3176), from1000(result.ymax,1924)));
	}

	private Media toMedia(Path uiScreenshotPath) {
		try {
			return Media.builder().mimeType(MimeType.valueOf("image/png"))
								  .data(Files.readAllBytes(uiScreenshotPath))
								  .build();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read screenshot file: " + uiScreenshotPath, e);
		}
	}

	// Model returns coordinates in 0-1000 range, but the actual screenshot is in pixels. This method converts the model's coordinates to actual pixel coordinates.
	private int from1000(int value, int maxValue) {
		return value * maxValue / 1000;
	}
}
