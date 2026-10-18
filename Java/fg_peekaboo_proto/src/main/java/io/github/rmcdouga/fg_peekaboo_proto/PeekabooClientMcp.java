package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.ImageContent;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

/**
 * This class manages the Peekaboo MCP server and provides methods to interact with it, 
 * such as capturing images of application windows.
 * 
 */
public class PeekabooClientMcp implements PeekabooClient {
	private static final Pattern PID_PATTERN = Pattern.compile("PID:\\s*(\\d+)");

	private static final Logger log = LoggerFactory.getLogger(PeekabooClientMcp.class);
	
	private final McpSyncClient client;
	
//	public record Process(long id) {
//		public static Process of(long id) {
//			return new Process(id);
//		}
//	}
	
	// Private constructor to enforce the use of the static find() method for instantiation.
	private PeekabooClientMcp(McpSyncClient client) {
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
	public static PeekabooClient find(List<McpSyncClient> clients) {
		List<McpSyncClient> servers = clients.stream().filter(c ->c.getServerInfo().name().toLowerCase().equals("peekaboo-mcp")).toList();
		if (servers.size() != 1) {
			throw new IllegalStateException("Expected exactly one PeekabooMcpClient, found " + servers.size());
		}
		return new PeekabooClientMcp(servers.getFirst());
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
	@Override
	public void image(AppTarget target, ImageFormat format, ImageScale scale, Path outputPath) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("image")
									   							   .arguments(Map.of(
									   									   "app_target", target.value(),
									   									   "format", format.toString().toLowerCase(),
									   									   "scale", scale.toString().toLowerCase(),
									   									   "path", outputPath.toString()
								   											))
									   							   .build());
		
		displayToolResult(toolResult, "image");
		
		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'image' tool: " + toolResult.toString());
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
	@Override
	public void image(AppTarget target, ImageScale scale) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("image")
									   							   .arguments(Map.of(
									   									   "app_target", target.value(),
									   									   "format", "data",
									   									   "scale", scale.toString().toLowerCase()
								   											))
									   							   .build());
		
		displayToolResult(toolResult, "image");
		
		// We don't currently do anything with the image data returned, 
		// but we could extend this method to return the byte array if needed.

		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'image' tool: " + toolResult.toString());
		}
		
	}
	
	@Override
	public void see(AppTarget target, Path outputPath) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("see")
									   							   .arguments(Map.of(
									   									   "app_target", target.value(),
									   									   "path", outputPath.toString(),
									   									   "fresh", true
								   											))
									   							   .build());
		
		displayToolResult(toolResult, "see");

		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'see' tool: " + toolResult.toString());
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
	@Override
	public void click(long pid, int x, int y) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("click")
									   							   .arguments(Map.of(
									   									   "pid", Long.toString(pid),
									   									   "coords", "%d,%d".formatted(x, y),
									   									   "foreground", true
								   											))
									   							   .build());
		
		displayToolResult(toolResult, "click");

		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'click' tool: " + toolResult.toString());
		}
	}

	/**
	 * Simulates a mouse move at the specified (x, y) coordinates within the given application target.
	 * 
	 * @param target the application target where the click should occur (by name, process ID, or frontmost)
	 * @param x the x-coordinate of the click position
	 * @param y the y-coordinate of the click position
	 * 
	 * @throws IllegalStateException if there is an error calling the 'click' tool on the Peekaboo MCP server.
	 */
	@Override
	public void move(AppTarget target, int x, int y) {
		
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("move")
									   							   .arguments(Map.of(
//									   									   "pid", Long.toString(pid),
									   									   "to", "%d,%d".formatted(x, y),
									   									   "foreground", true
//									   									   "profile", "human"
								   											))
									   							   .build());
		
		displayToolResult(toolResult, "move");

		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'move' tool: " + toolResult.toString());
		}
	}

	private void displayToolResult(CallToolResult toolResult, String toolname) {
		List<Content> contentList = toolResult.content();
		log.atInfo().log(()->"Peekaboo MCP '" + toolname + "' tool returned " + contentList.size() + " content items.");
		var content = contentList.getFirst();
		log.atInfo().log("Content type: " + content.type());
		log.atInfo().log("Content metadata: " + Objects.requireNonNullElse(content.meta(), "").toString() + ".");
		switch (content.type()) {
			case "text" -> log.atInfo().log("Content text: " + ((TextContent)content).text());
			case "image" -> {
				ImageContent imageContent = (ImageContent)content;
				log.atInfo().log("Content image mime-type: " + imageContent.mimeType());
				log.atInfo().log("Content image data length: " + imageContent.data().length() + " bytes.");
				}
			default -> log.atInfo().log("Unimplemented content type: '" + content.type() + "'.");
		}
	}
	
	@Override
	public long appLaunch(String bundleId) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("app")
				   .arguments(Map.of(
						   "action", "launch",
						   "bundleId", bundleId,
						   "foreground", "true"
							))
				   .build());

		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'click' tool: " + toolResult.toString());
		}
		
		return toolResult.content().stream()
				  .filter(c -> c.type().equals("text"))
				  .findFirst()
				  .map(c -> ((TextContent)c).text())
				  .flatMap(PeekabooClientMcp::extractPid)
				  .orElseThrow(() -> new PeekabooMcpClientException("No process ID returned from 'app_launch' tool."));
		
	}
	
	@Override
	public long appFocus(long pid) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("app")
				   .arguments(Map.of(
						   "action", "focus",
						   "name", "PID:" + Long.toString(pid),
						   "foreground", "true"
							))
				   .build());

		if (toolResult.isError()) {
			throw new PeekabooMcpClientException("Error calling 'click' tool: " + toolResult.toString());
		}
		
		return toolResult.content().stream()
				  .filter(c -> c.type().equals("text"))
				  .findFirst()
				  .map(c -> ((TextContent)c).text())
				  .flatMap(PeekabooClientMcp::extractPid)
				  .orElseThrow(() -> new PeekabooMcpClientException("No process ID returned from 'app_launch' tool."));
		
	}
	
	private static Optional<Long> extractPid(String content) {
		  // Define pattern to match "PID: " followed by one or more digits
        Matcher matcher = PID_PATTERN.matcher(content);
        return matcher.find() ? Optional.of(Long.parseLong(matcher.group(1))) : Optional.empty();
        
	}
	
	public static class PeekabooMcpClientException extends RuntimeException {
		public PeekabooMcpClientException(String message) {
			super(message);
		}
		
		public PeekabooMcpClientException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
