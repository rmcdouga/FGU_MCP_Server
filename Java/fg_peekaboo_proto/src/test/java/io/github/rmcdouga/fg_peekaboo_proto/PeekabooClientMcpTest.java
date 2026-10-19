package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.modelcontextprotocol.client.McpSyncClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, 
				properties = {
					"fg_peekaboo_proto.show_tool_info=window"	// Use this to show tool information in the logs for debugging purposes. Comment out to disable.
					}
				)		
@ActiveProfiles({ "integration-test", "exclude-ai" })
class PeekabooClientMcpTest extends AbstractPeekabooClientTest {
	
	public PeekabooClientMcpTest(@Autowired List<McpSyncClient> mcpClients) {
		super(PeekabooClientMcp.find(mcpClients));
	}

}
