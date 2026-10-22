package io.github.rmcdouga.fg_peekaboo_proto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;

public class UiLocator {
	private static final Logger log = LoggerFactory.getLogger(UiLocator.class);
	
	/**
	 * @param buttonName
	 * @param xmin Minimum X coordinate of the button bounding box in 0-999 range (model output)
	 * @param ymin Minimum Y coordinate of the button bounding box in 0-999 range (model output)
	 * @param xmax Maximum X coordinate of the button bounding box in 0-999 range (model output)
	 * @param ymax Maximum Y coordinate of the button bounding box in 0-999 range (model output)
	 */
	public record ButtonBoundingBox(String buttonName, int xmin, int ymin, int xmax, int ymax) {	
		public String toString() {
			return buttonName + ": " +  xmin + "," + ymin + "," + xmax + "," + ymax;
		}
	};

	/**
	 * @param x Button click location X coordinate in pixels
	 * @param y Button click location Y coordinate in pixels
	 */
	public record ButtonClickLocation(int x, int y) {
		/**
		 * Creates a ButtonClickLocation from the center of a bounding box defined by its minimum and maximum coordinates.
		 * 
		 * @param xmin Bounding box minimum X coordinate in pixels
		 * @param ymin Bounding box minimum Y coordinate in pixels
		 * @param xmax Bounding box maximum X coordinate in pixels
		 * @param ymax Bounding box maximum Y coordinate in pixels
		 * @return A ButtonClickLocation representing the center of the bounding box
		 */
		private static ButtonClickLocation fromBoundingBox(int xmin, int ymin, int xmax, int ymax) {
			log.atDebug().log("Button Location: {}, {}, xmax: {}, ymax {}", xmin, ymin, xmax, ymax);
			int centerX = (xmin + xmax) / 2;
			int centerY = (ymin + ymax) / 2;
			return new ButtonClickLocation(centerX, centerY);
		}
		
		public String toString() {
			return x + "," + y;
		}
		// ButtonClickLocation.fromBoundingBox(from1000(result.xmin,3176), from1000(result.ymin,1924), from1000(result.xmax,3176), from1000(result.ymax,1924)
		// 		log.atDebug().log(()->"Model response (pixels): " + from1000(result.xmin,IMAGE_SIZE_X) + "," + from1000(result.ymin,IMAGE_SIZE_Y) + "," + from1000(result.xmax,3176) + "," + from1000(result.ymax,1924));

		public static ButtonClickLocation fromButtonBoundingBox(ButtonBoundingBox box, int width, int height) {
			return ButtonClickLocation.fromBoundingBox(from1000(box.xmin,width), from1000(box.ymin,height), from1000(box.xmax,width), from1000(box.ymax,height));
		}

		// Model returns coordinates in 0-1000 range, but the actual screenshot is in pixels. This method converts the model's coordinates to actual pixel coordinates.
		private static int from1000(int value, int maxValue) {
			return value * maxValue / 1000;
		}
	}
	
	private final ChatClient chatClient;

	public UiLocator(ChatClient.Builder chatClientBuilder) {
		this(chatClientBuilder.build());
	}

	private UiLocator(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	/**
	 * Locates the bounding box of a button with the specified text in the given UI screenshot.
	 * 
	 * @param buttonText 		target text of the button to locate
	 * @param uiScreenshotPath	path to the UI screenshot image file
	 * @return					Optional containing the ButtonBoundingBox if found, or an empty Optional if not found	
	 */
	public Optional<ButtonBoundingBox> locateButton(Path uiScreenshotPath, String buttonText) {
		
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
		return result.xmin == 0 && result.ymin == 0 && result.xmax == 0 && result.ymax == 0 
				? Optional.empty()
				: Optional.of(result);
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
	
	public boolean doesScreenshotContainTexts(Path uiScreenshotPath, List<String> text) {
		String textList = text.stream().collect(Collectors.joining("\", \"", "[\"", "\"]"));
		@Nullable 
		Boolean result = chatClient.prompt()
					  					  	 .user(p->p.media(toMedia(uiScreenshotPath))
					  					  			   .text("Does this screenshot contain all the following texts: " + textList + "? Return true or false.")
					  					  		  )
					  					  	 .call()
					  					  	 .entity(Boolean.class, spec -> spec.useProviderStructuredOutput()
					  					  			 									  .validateSchema()
					  					  			);
			
		log.atDebug().log(()->"Model response: {}" + (result == null ? "null" : result.toString()));
		return result != null && result;
	}

}
