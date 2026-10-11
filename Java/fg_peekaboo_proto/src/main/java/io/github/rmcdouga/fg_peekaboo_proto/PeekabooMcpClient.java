package io.github.rmcdouga.fg_peekaboo_proto;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

public class PeekabooMcpClient {
	private final McpSyncClient client;
	
//	public record Process(long id) {
//		public static Process of(long id) {
//			return new Process(id);
//		}
//	}
	
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
	
	public enum ImageFormat {
		PNG, JPEG;
	}

	public enum ImageScale {
		LOGICAL, NATIVE;
	}
	
	private PeekabooMcpClient(McpSyncClient client) {
		this.client = client;
	}
	
	static PeekabooMcpClient find(List<McpSyncClient> clients) {
		List<McpSyncClient> servers = clients.stream().filter(c ->c.getServerInfo().name().toLowerCase().equals("peekaboo-mcp")).toList();
		if (servers.size() != 1) {
			throw new IllegalStateException("Expected exactly one PeekabooMcpClient, found " + servers.size());
		}
		return new PeekabooMcpClient(servers.getFirst());
	}
	
	public void image(AppTarget target, ImageFormat format, ImageScale scale, Path outputPath) {
		CallToolResult toolResult = client.callTool(CallToolRequest.builder("image")
									   							   .arguments(Map.of(
									   									   "app_target", target.value(),
									   									   "format", format.toString().toLowerCase(),
									   									   "scale", scale.toString().toLowerCase(),
									   									   "path", outputPath.toString()
								   											))
									   							   .build());
		
		if (toolResult.isError()) {
			throw new IllegalStateException("Error calling 'image' tool: " + toolResult.toString());
		}
	}
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
}
