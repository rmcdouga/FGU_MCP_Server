package io.github.rmcdouga.fgumcpserver.fgu_data.adapters.in;

import java.util.List;
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
		try {
			List<String> availableCampaigns = fguData.campaigns()
													  .map(c -> c.name())
													  .toList();
			return availableCampaigns.isEmpty() 
					? "No Fantasy Grounds Campaigns available"
					: "Available Fantasy Grounds Campaigns:\n%s".formatted(
							availableCampaigns.stream().collect(Collectors.joining("\n"))
							);
		} catch (Exception e) {
			e.printStackTrace();
			return "Error retrieving campaigns: %s".formatted(e.getMessage());
		}
	}

}
