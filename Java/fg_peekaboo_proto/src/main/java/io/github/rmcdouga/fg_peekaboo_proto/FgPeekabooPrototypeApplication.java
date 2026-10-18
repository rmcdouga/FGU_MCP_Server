package io.github.rmcdouga.fg_peekaboo_proto;

//import static org.modeljars.catalog.Qwen3_1_7b_Q8_0.MODEL;
//import static org.modeljars.catalog.Cactus_Compute_Needle2_Cact_Cq2_Mixed.MODEL;
//import static org.modeljars.catalog.Qwen3_8b_Q4_K_M.MODEL;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.modeljars.ModelBackend;
//import org.modeljars.ModelJars;
//import org.modeljars.ModelLoadOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.util.MimeType;

import io.github.rmcdouga.fg_peekaboo_proto.UiLocator.ButtonClickLocation;
//import com.integrallis.models.api.SamplingOptions;
//import com.integrallis.models.spring.ai.ModelsSpringAiChatModel;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.Tool;

@SpringBootApplication
public class FgPeekabooPrototypeApplication {
	private static final Logger log = LoggerFactory.getLogger(FgPeekabooPrototypeApplication.class);
	private static final String CFG_PREFIX = "fg_peekaboo_proto.";
	public static final String CFG_SHOW_TOOL_INFO = CFG_PREFIX + "show_tool_info";

	public static void main(String[] args) {
		SpringApplication.run(FgPeekabooPrototypeApplication.class, args);
	}

	// Only required for models that run in the JVM
//	@Bean
//	ChatClient.Builder chatClientBuilder(
//	        ObjectProvider<ChatClientBuilderCustomizer> customizers) {
////		System.out.println("Creating ChatClient.Builder with model: " + MODEL);
//	    var defaults = SamplingOptions.builder().build();
//	    var loadOptions = ModelLoadOptions.builder()
//	        .backend(ModelBackend.JAVA)
//	        .build();
//	    var runtime = ModelJars.openRuntime(MODEL, loadOptions);
//
//	    var model = new ModelsSpringAiChatModel(
//	        runtime.model(),
//	        runtime.descriptor().alias(),
//	        runtime.chatTemplate(),
//	        defaults,
//	        runtime.descriptor().capabilities());
//
//	    return applyCustomizers(
//	        ChatClient.builder(model),
//	        customizers
//	    );
//	}
//	
//	private ChatClient.Builder applyCustomizers(
//	        ChatClient.Builder builder,
//	        ObjectProvider<ChatClientBuilderCustomizer> customizers) {
//	  customizers.orderedStream()
//	        .forEach(customizer -> addCustomizer(customizer, builder));
//	    return builder;
//	}
//
//	private void addCustomizer(ChatClientBuilderCustomizer customizer, ChatClient.Builder builder) {
////		System.out.println("Applying customizer: " + customizer.getClass().getName());
//		customizer.customize(builder);
//	}
	
//    @Bean
//    ApplicationRunner printMcpBeans(ApplicationContext ctx) {
//        return _ -> {
//            System.out.println("Beans with 'mcp' in their class name:");
//            for (String name : ctx.getBeanDefinitionNames()) {
//                Object bean = ctx.getBean(name);
//                String className = bean.getClass().getName();
//                if (className.toLowerCase().contains("mcp")) {
//                    System.out.println("  - " + name + " (" + className + ")");
//                }
//            }
//        };
//    }

    @Profile("!exclude-ai")
	@Bean
	UiLocator uiLocator(ChatClient.Builder chatClientBuilder) {
		return new UiLocator(chatClientBuilder);
	}

    @Profile("!exclude-ai")
    @Bean
    ChatClientBuilderCustomizer setTemperature() {
    	return builder -> builder.defaultOptions(ChatOptions.builder().temperature(0.0)); // Make the model deterministic for testing purposes
    }
	
	@Profile("!integration-test")
	@Bean
    ApplicationRunner callPeekabooDirectly(PeekabooClient peekabooClient, ChatClient.Builder chatClientBuilder, UiLocator uiLocator) {
        return _ -> {
        	long fgProcessId = FantasyGroundsApp.processId().orElseThrow();
			System.out.println("Calling 'image' tool on PeekabooMcpClient for process %d...".formatted(fgProcessId));
			Path screenshotPath = Files.createTempFile("fg_peekaboo_proto_screenshot", ".png");
			peekabooClient.image(PeekabooClient.AppTarget.ProcessTarget.of(fgProcessId), PeekabooClient.ImageFormat.PNG, PeekabooClient.ImageScale.NATIVE, screenshotPath);
//			peekabooClient.see(PeekabooMcpClient.AppTarget.ProcessTarget.of(fgProcessId), screenshotPath);
			System.out.println("Screenshot saved to '%s'".formatted(screenshotPath.toAbsolutePath().toString()));
			uiLocator.locateButton("MY CAMPAIGNS", screenshotPath).ifPresentOrElse(
					_-> System.out.println("Found 'MY CAMPAIGNS' button in the screenshot."),
					() -> System.out.println("'MY CAMPAIGNS' button not found in the screenshot.")
					);
			
        };
    }

    @Profile("!exclude-mcp")
    @Bean
    PeekabooClient peekabooMcpClient(List<McpSyncClient> mcpClients) {
    	return PeekabooClientMcp.find(mcpClients);
    }
    
    @Profile("!exclude-mcp")
    @Bean
    ChatClientBuilderCustomizer addMcpTools(ToolCallbackProvider mcpToolCallbacks, @Value("${" + CFG_SHOW_TOOL_INFO + "}") String showToolInfo) {
    	ToolCallback[] toolCallbacks = mcpToolCallbacks.getToolCallbacks();
    	System.out.println("Adding MCP tools to ChatClient builder (%d tools):".formatted(toolCallbacks.length));
    	
    	List<ToolCallback> tools = List.of(toolCallbacks);
		tools.stream()
		 	.forEach(toolCallback -> System.out.println("  - Tool: " + toolCallback.getToolDefinition().name()));
		
		// Find the details of a specific tool by name
		if (showToolInfo != null && !showToolInfo.isBlank()) {
			showSpecificToolInfo(tools, showToolInfo);
		}		
		
    	return builder -> builder.defaultTools(mcpToolCallbacks);
    }

	private void showSpecificToolInfo(List<ToolCallback> tools, String toolName) {
		Optional<ToolDefinition> imageTool = tools.stream()
				.map(ToolCallback::getToolDefinition)
				.filter(toolDef -> toolDef.name().equals(toolName))
				.findFirst()
				.map(toolDef -> {
					System.out.println("Details of '" + toolName + "' tool:");
					System.out.println("  - Name: " + toolDef.name());
					System.out.println("  - Description: " + toolDef.description());
					System.out.println("  - Input Schema: " + toolDef.inputSchema());
					return toolDef;
				})
				;
	}
}
