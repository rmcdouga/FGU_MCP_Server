package io.github.rmcdouga.fg_peekaboo_proto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;

public class UiLocator {
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

	public UiLocator(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	public ButtonClickLocation locateButton(String buttonText, Path uiScreenshotPath) {
		final record ButtonBoundingBox(String buttonName, int xmin, int ymin, int xmax, int ymax) {	
			public String toString() {
				return buttonName + ": " +  xmin + "," + ymin + "," + xmax + "," + ymax;
			}
		};
		
		@Nullable 
		ButtonBoundingBox result = chatClient.prompt()
					  					  	 .user(p->p.media(toMedia(uiScreenshotPath))
//					  					  			   .text("Locate the MY CAMPAIGNS button and print the button's X and Y coordinates in pixels as well as the button's width and height in pixels.")
					  					  			   .text("Locate the " + buttonText + " button and return its bounding box coordinates [xmin, ymin, xmax, ymax]. **Mandatory** Don't guess, return 0 if unsure.")
//					  					  			   .text("Determine the size of the image in pixels [ymin, xmin, ymax, xmax]. **Mandatory** Don't guess, return 0 if unsure.")
					  					  		  )
					  					  	 .call()
					  					  	 .entity(ButtonBoundingBox.class, spec -> spec.useProviderStructuredOutput()
					  					  			 									  .validateSchema()
					  					  			);
			
		log.atDebug().log(()->"Model response: {}" + (result == null ? "null" : result.toString()));
		log.atDebug().log(()->"Model response (pixels): " + from1000(result.xmin,3176) + "," + from1000(result.ymin,1924) + "," + from1000(result.xmax,3176) + "," + from1000(result.ymax,1924));
		return ButtonClickLocation.fromBoundingBox(from1000(result.xmin,3176), from1000(result.ymin,1924), from1000(result.xmax,3176), from1000(result.ymax,1924));
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
	
	private int from1000(int value, int maxValue) {
		return value * maxValue / 1000;
	}

	
}
