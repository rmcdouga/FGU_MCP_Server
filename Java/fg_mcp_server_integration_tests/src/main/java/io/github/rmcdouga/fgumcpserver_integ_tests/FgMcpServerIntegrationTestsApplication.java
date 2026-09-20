package io.github.rmcdouga.fgumcpserver_integ_tests;

import static org.modeljars.catalog.Qwen3_1_7b_Q8_0.MODEL;

import org.modeljars.ModelBackend;
import org.modeljars.ModelJars;
import org.modeljars.ModelLoadOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
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
	        .forEach(customizer -> customizer.customize(builder));
	    return builder;
	}
}
