package io.github.rmcdouga.fgumcpserver_integ_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SanitizeTextTests {

	private static final String SANITIZE_TEXT_PROMPT = "Sanitize the following text for Fantasy Grounds without any explanations and print the result: %s";
	
	@SuppressWarnings("unused")
	private static final List<Arguments> sanitizeTextTestCases =	List.of(
			Arguments.of("This is some text that contains “left and right quotes” and some ‘left and right single-quotes’.",
						 "This is some text that contains \"left and right quotes\" and some 'left and right single-quotes'.")
			);		

	@ParameterizedTest
	@FieldSource("sanitizeTextTestCases")
	void test(String unsanitizedText, String expectedAnswer, @Autowired ChatClient.Builder chatClientBuilder) {
	   	var result = chatClientBuilder.build()
   				.prompt()
   				.user(SANITIZE_TEXT_PROMPT.formatted(unsanitizedText))
   				.call()
   				.content();
	   	
   	   	assertThat(result).isEqualTo(expectedAnswer);
	}

}
