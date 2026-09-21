package io.github.rmcdouga.fgumcpserver_integ_tests;

import static org.modeljars.catalog.Qwen3_1_7b_Q8_0.MODEL;
//import static org.modeljars.catalog.Cactus_Compute_Needle2_Cact_Cq2_Mixed.MODEL;
//import static org.modeljars.catalog.Qwen3_8b_Q4_K_M.MODEL;

import org.modeljars.ModelBackend;
import org.modeljars.ModelJars;
import org.modeljars.ModelLoadOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.integrallis.models.api.SamplingOptions;
import com.integrallis.models.spring.ai.ModelsSpringAiChatModel;

@SpringBootApplication
public class FgMcpServerIntegrationTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FgMcpServerIntegrationTestsApplication.class, args);
	}

	@Bean
	ChatClient.Builder chatClientBuilder(
	        ObjectProvider<ChatClientBuilderCustomizer> customizers) {
//		System.out.println("Creating ChatClient.Builder with model: " + MODEL);
	    var defaults = SamplingOptions.builder().build();
	    var loadOptions = ModelLoadOptions.builder()
	        .backend(ModelBackend.JAVA)
	        .build();
	    var runtime = ModelJars.openRuntime(MODEL, loadOptions);

	    var model = new ModelsSpringAiChatModel(
	        runtime.model(),
	        runtime.descriptor().alias(),
	        runtime.chatTemplate(),
	        defaults,
	        runtime.descriptor().capabilities());

	    return applyCustomizers(
	        ChatClient.builder(model),
	        customizers
	    );
	}
	
	private ChatClient.Builder applyCustomizers(
	        ChatClient.Builder builder,
	        ObjectProvider<ChatClientBuilderCustomizer> customizers) {
	  customizers.orderedStream()
	        .forEach(customizer -> addCustomizer(customizer, builder));
	    return builder;
	}

	private void addCustomizer(ChatClientBuilderCustomizer customizer, ChatClient.Builder builder) {
//		System.out.println("Applying customizer: " + customizer.getClass().getName());
		customizer.customize(builder);
	}
	
    @Bean
    ChatClientBuilderCustomizer addMcpTools(ToolCallbackProvider mcpToolCallbacks) {
    	ToolCallback[] toolCallbacks = mcpToolCallbacks.getToolCallbacks();
//    	System.out.println("Adding MCP tools to ChatClient builder (%d tools):".formatted(toolCallbacks.length));
//    	Arrays.stream(toolCallbacks)
//    	 	.forEach(toolCallback -> System.out.println("  - Tool: " + toolCallback.getToolDefinition().name()));
    	return builder -> builder.defaultTools(mcpToolCallbacks);
    }
}
