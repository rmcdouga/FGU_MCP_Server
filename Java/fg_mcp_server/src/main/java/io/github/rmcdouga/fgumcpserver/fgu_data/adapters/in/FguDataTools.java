package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.in;

import java.util.stream.Collectors;

import org.springframework.ai.mcp.annotation.McpTool;

import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in.FguData;

public class FguDataTools {
	private final FguData fguData;
	
	public FguDataTools(FguData fguData) {
		this.fguData = fguData;
	}

	@McpTool(description = "List available Fantasy Grounds Campaigns.")
	String listFguCampaigns() {
		return "Available Fantasy Grounds Campaigns:\n%s".formatted(
				fguData.campaigns()
					   .map(c -> c.name())
					   .collect(Collectors.joining("\n"))
				);
	}

}
