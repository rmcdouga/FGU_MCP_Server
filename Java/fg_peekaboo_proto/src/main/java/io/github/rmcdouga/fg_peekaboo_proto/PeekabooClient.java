package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;

public interface PeekabooClient {
	/**
	 * Manages the "app_target" argument for the "image" tool, which can be specified as 
	 * an application name, process ID, or frontmost application.
	 */
	public static sealed interface AppTarget {
		
		String value();
		String cliValue();
		
		public static final record AppNameTarget(String name) implements AppTarget {
			public static AppNameTarget of(String name) {
				return new AppNameTarget(name);
			}

			@Override
			public String value() {
				return name;
			}

			@Override
			public String cliValue() {
				return value();
			}
		}
		
		public static final record ProcessTarget(long processId) implements AppTarget {
			public static ProcessTarget of(long processId) {
				return new ProcessTarget(processId);
			}

			@Override
			public String value() {
				return "PID:" + String.valueOf(processId);
			}

			@Override
			public String cliValue() {
				return String.valueOf(processId);
			}
		}
		
		public static final record FrontMostTarget() implements AppTarget {
			public static FrontMostTarget of() {
				return new FrontMostTarget();
			}

			@Override
			public String value() {
				return "frontmost";
			}

			@Override
			public String cliValue() {
				return value();
			}
		}
	}
	
	/**
	 * Image format options for the "image" tool
	 */
	public enum ImageFormat {
		PNG, JPEG;
	}

	/**
	 * Image scale options for the "image" tool. LOGICAL scale captures the image at the logical resolution, 
	 * while NATIVE scale captures it at the native resolution of the display.
	 */
	public enum ImageScale {
		LOGICAL, NATIVE;
	}
	


	/**
	 * Captures an image of the specified application target and saves it to the given output path.
	 * 
	 * @param target the application target to capture (by name, process ID, or frontmost)
	 * @param format the image format to save (PNG or JPEG)
	 * @param scale the scale of the image (logical or native)
	 * @param outputPath the path where the captured image will be saved
	 * 
	 * @throws IllegalStateException if there is an error calling the 'image' tool on the Peekaboo MCP server.
	 */
	void image(AppTarget target, ImageFormat format, ImageScale scale, Path outputPath);

	/**
	 * Captures an image of the specified application target and returns it as a byte array.
	 * 
	 * @param target the application target to capture (by name, process ID, or frontmost)
	 * @param scale the scale of the image (logical or native)
	 * 
	 * @throws IllegalStateException if there is an error calling the 'image' tool on the Peekaboo MCP server.
	 */
	void image(AppTarget target, ImageScale scale);

	record SeeInfo(Path screenshotImage, int imageWidth, int imageHeight) {
		public static SeeInfo of(Path screenshotImage, int imageWidth, int imageHeight) {
			return new SeeInfo(screenshotImage, imageWidth, imageHeight);
		}
	}
	/**
	 * Captures an image of the specified application target and saves it to the given output path.
	 * 
	 * @param target the application target to capture (by name, process ID, or frontmost)
	 * @param outputPath the path where the captured image will be saved
	 * @return ImageInfo containing information (e.g. the width and height) about the captured image
	 * 
	 * @throws IllegalStateException if there is an error calling the 'see' tool on the Peekaboo MCP server.
	 */
	SeeInfo see(AppTarget target, Path outputPath);

	/**
	 * Simulates a mouse click at the specified (x, y) coordinates within the given application target.
	 * 
	 * @param target the application target where the click should occur (by name, process ID, or frontmost)
	 * @param x the x-coordinate of the click position
	 * @param y the y-coordinate of the click position
	 * 
	 * @throws IllegalStateException if there is an error calling the 'click' tool on the Peekaboo MCP server.
	 */
	void click(long pid, int x, int y);

	/**
	 * Simulates a mouse move at the specified (x, y) coordinates within the given application target.
	 * 
	 * @param target the application target where the click should occur (by name, process ID, or frontmost)
	 * @param x the x-coordinate of the click position
	 * @param y the y-coordinate of the click position
	 * 
	 * @throws IllegalStateException if there is an error calling the 'click' tool on the Peekaboo MCP server.
	 */
	void move(AppTarget target, int x, int y);

	long appLaunch(String bundleId);

	long appFocus(long pid);
	
	@SuppressWarnings("serial")
	public static class PeekabooClientException extends RuntimeException {
		public PeekabooClientException(String message) {
			super(message);
		}
		
		public PeekabooClientException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}