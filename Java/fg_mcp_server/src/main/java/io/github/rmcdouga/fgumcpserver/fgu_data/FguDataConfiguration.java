package io.github.rmcdouga.fgumcpserver.fgu_data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.rmcdouga.fgumcpserver.fgu_data.adapters.in.FguDataTools;
import io.github.rmcdouga.fgumcpserver.fgu_data.adapters.out.FileSystemFguDataStore;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.FguDataLogic;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.in.FguData;
import io.github.rmcdouga.fgumcpserver.fgu_data.domain.ports.out.FguDataStore;

/**
 * Spring configuration class for FGU data-related beans. 
 * 
 * This class defines the necessary beans for data tools.
 */
@Configuration
public class FguDataConfiguration {

	/**
	 * Creates a bean for Fgu Data Tools.
	 * 
	 * This is the entry point for the FGU data tools, which provides access to the underlying FGU data.
	 * It is an input adapter that uses the FguData interface to access the data.
	 * 
	 * @param fguData
	 * @return
	 */
	@Bean
	FguDataTools fguDataTools(FguData fguData) {
		return new FguDataTools(fguData);
	}

	/**
	 * Creates a FguData bean.
	 * 
	 * This is the entry point for the FGU data domain, which provides access to the underlying FGU data.
	 * 
	 * @param fguDataStore
	 * @return
	 */
	@Bean
	FguData fguData(FguDataStore fguDataStore) {
		return new FguDataLogic(fguDataStore);
	}
	
	/**
	 * This is the output adapter for the FGU data domain which provide access to the FGU data store. 
	 * 
	 * In this case, it is a file system-based implementation.
	 * 
	 * @return
	 */
	@Bean
	FguDataStore fguDataStore() {
		return new FileSystemFguDataStore();
	}
}
