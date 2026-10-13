package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;

/**
 * This class manages the Peekaboo MCP server and provides methods to interact with it, 
 * such as capturing images of application windows.
 * 
 */
public class PeekabooMcpClient {
	private static final Logger log = LoggerFactory.getLogger(PeekabooMcpClient.class);
	
	private final McpSyncClient client;
	
//	public record Process(long id) {
//		public static Process of(long id) {
//			return new Process(id);
//		}
//	}
	
	/**
	 * Manages the "app_target" argument for the "image" tool, which can be specified as 
	 * an application name, process ID, or frontmost application.
	 */
	public static sealed interface AppTarget {
		
		String value();
		
		public static final record AppNameTarget(String name) implements AppTarget {
			public static AppNameTarget of(String name) {
				return new AppNameTarget(name);
			}

			@Override
			public String value() {
				return name;
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
		}
		
		public static final record FrontMostTarget() implements AppTarget {
			public static FrontMostTarget of() {
				return new FrontMostTarget();
			}

			@Override
			public String value() {
				return "frontmost";
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
	
	// Private constructor to enforce the use of the static find() method for instantiation.
	private PeekabooMcpClient(McpSyncClient client) {
		this.client = client;
	}
	
	/**
	 * Finds the PeekabooMcpClient from a list of McpSyncClients. 
	 * It expects exactly one client with the server name "peekaboo-mcp". 
	 * 
	 * @param clients the list of McpSyncClients to search through
	 * @return the PeekabooMcpClient instance
	 * 
	 * @throws IllegalStateException if there is not exactly one PeekabooMcpClient found.
	 */
	public static PeekabooMcpClient find(List<McpSyncClient> clients) {
		List<McpSyncClient> servers = clients.stream().filter(c ->c.getServerInfo().name().toLowerCase().equals("peekaboo-mcp")).toList();
		if (servers.size() != 1) {
			throw new IllegalStateException("Expected exactly one PeekabooMcpClient, found " + servers.size());
		}
		return new PeekabooMcpClient(servers.getFirst());
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
	public void image(AppTarget target, ImageFormat format, ImageScale scale, Path outputPath) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("image")
									   							   .arguments(Map.of(
									   									   "app_target", target.value(),
									   									   "format", format.toString().toLowerCase(),
									   									   "scale", scale.toString().toLowerCase(),
									   									   "path", outputPath.toString()
								   											))
									   							   .build());
		
		List<Content> contentList = toolResult.content();
		
		if (toolResult.isError()) {
			throw new IllegalStateException("Error calling 'image' tool: " + toolResult.toString());
		}
	}

	/**
	 * Captures an image of the specified application target and returns it as a byte array.
	 * 
	 * @param target the application target to capture (by name, process ID, or frontmost)
	 * @param scale the scale of the image (logical or native)
	 * 
	 * @throws IllegalStateException if there is an error calling the 'image' tool on the Peekaboo MCP server.
	 */
	public void image(AppTarget target, ImageScale scale) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("image")
									   							   .arguments(Map.of(
									   									   "app_target", target.value(),
									   									   "format", "data",
									   									   "scale", scale.toString().toLowerCase()
								   											))
									   							   .build());
		
		if (toolResult.isError()) {
			throw new IllegalStateException("Error calling 'image' tool: " + toolResult.toString());
		}
		
	}
	
	/**
	 * Simulates a mouse click at the specified (x, y) coordinates within the given application target.
	 * 
	 * @param target the application target where the click should occur (by name, process ID, or frontmost)
	 * @param x the x-coordinate of the click position
	 * @param y the y-coordinate of the click position
	 * 
	 * @throws IllegalStateException if there is an error calling the 'click' tool on the Peekaboo MCP server.
	 */
	public void click(AppTarget target, int x, int y) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("click")
									   							   .arguments(Map.of(
									   									   // TODO:  These parameters are all wrong.
									   									   "app_target", target.value(),
									   									   "x", String.valueOf(x),
									   									   "y", String.valueOf(y)
								   											))
									   							   .build());
		
		if (toolResult.isError()) {
			throw new IllegalStateException("Error calling 'click' tool: " + toolResult.toString());
		}
	}
}
