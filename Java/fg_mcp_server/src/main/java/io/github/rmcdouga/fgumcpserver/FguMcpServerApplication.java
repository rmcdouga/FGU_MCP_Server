package io.github.rmcdouga.fgumcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the FGU MCP Server. This class is responsible for bootstrapping the Spring Boot application 
 * and starting the embedded web server.
 */
@SpringBootApplication
public class FguMcpServerApplication {
	public static final String APP_CFG_PREFIX = "fgumcpserver.";	// Prefix for application configuration properties.

	private FguMcpServerApplication() {	// Private constructor to prevent instantiation.
	}

	/**
	 * The entry point of the FGU MCP Server application. This method is invoked when the application is started.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(FguMcpServerApplication.class, args);
	}

}
