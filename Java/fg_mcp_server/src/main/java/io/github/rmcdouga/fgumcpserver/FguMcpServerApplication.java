package io.github.rmcdouga.fgumcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.github.rmcdouga.fgumcpserver.text.domain.FguTextSanitizer;
import io.github.rmcdouga.fgumcpserver.text.domain.ports.in.TextSanitizer;

@SpringBootApplication
public class FguMcpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FguMcpServerApplication.class, args);
	}

}
