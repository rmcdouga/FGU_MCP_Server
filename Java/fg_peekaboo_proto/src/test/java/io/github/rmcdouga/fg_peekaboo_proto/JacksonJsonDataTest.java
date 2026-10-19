package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class JacksonJsonDataTest {
	private static final String VALID_JSON = """
			{
			  "success" : true,
			  "data" : {
			    "bundle_id" : "unity.SmiteWorks.Fantasy Grounds",
			    "pid" : 91716,
			    "action" : "launch",
			    "app_name" : "Fantasy Grounds",
			    "process_start_identity" : 1792415641837126,
			    "is_ready" : true,
			    "window_ready" : false,
			    "window_ids" : [
			
			    ],
			    "window_identity" : "exact",
			    "process_start_identity_decimal" : "1792415641837126",
			    "new_instance" : false,
			    "window_count" : 0
			  },
			  "outcome" : {
			    "requires_fresh_observation" : false,
			    "delivery_mechanism" : "native_framework",
			    "dispatched_unit_count" : 3,
			    "mutation_dispatched" : true,
			    "effect" : "confirmed",
			    "state" : "confirmed_change",
			    "delivery_mode" : "foreground",
			    "evidence" : "verified_change",
			    "escalation" : "none",
			    "retry_safe" : false,
			    "dispatch_state" : "dispatched",
			    "route" : "bridge",
			    "retry_safety" : "not_applicable",
			    "sample_decimal" : 3.5
			  },
			  "effect" : "confirmed",
			  "debug_logs" : [
			    "[2026-10-19T13:14:01.812Z] DEBUG: Runtime host: remote onDemand via /Users/robmcdougall/Library/Application Support/Peekaboo/daemon.sock (build 4.9.5 (4.9.5))",
			    "[2026-10-19T13:14:01.818Z] VERBOSE: Launching application: unity.SmiteWorks.Fantasy Grounds"
			  ]
			}
			""";

	private final JsonData underTest;
	
	public JacksonJsonDataTest() {
		this.underTest = JacksonJsonData.parseJson(VALID_JSON);
	}

	@Test
	void testGetLongAt() {
		Optional<Long> pid = underTest.getLongAt("/data/pid");
		
		assertThat(pid).as("The '/data/pid' value should be present and equal to 91716.")
				   .isPresent()
				   .hasValue(91716L);
		
	}

	@Test
	void testGetStringAt() {
		Optional<String> bundleId = underTest.getStringAt("/data/bundle_id");
		
		assertThat(bundleId).as("The '/data/bundle_id' value should be present and equal to 'unity.SmiteWorks.Fantasy Grounds'.")
						   .isPresent()
						   .hasValue("unity.SmiteWorks.Fantasy Grounds");
	}
	
	@Test
	void testGetBooleanAt() {
		Optional<Boolean> isReady = underTest.getBooleanAt("/data/is_ready");
		
		assertThat(isReady).as("The '/data/is_ready' value should be present and equal to true.")
						  .isPresent()
						  .hasValue(true);
	}
	
	@Test
	void testGetDecimalAt() {
		Optional<BigDecimal> processStartIdentityDecimal = underTest.getDecimalAt("/outcome/sample_decimal");
		
		assertThat(processStartIdentityDecimal).as("The '/outcome/sample_decimal' value should be present and equal to 3.5.")
											   .isPresent()
											   .hasValue(new BigDecimal("3.5"));
	}
	
	@Test
	void testGetIntAt() {
		Optional<Integer> windowCount = underTest.getIntAt("/outcome/dispatched_unit_count");
		
		assertThat(windowCount).as("The '/data/window_count' value should be present and equal to 3.")
							   .isPresent()
							   .hasValue(3);
	}
	
	@Test
	void testGetFloatAt() {
		Optional<Float> dispatchedUnitCount = underTest.getFloatAt("/outcome/dispatched_unit_count");
		
		assertThat(dispatchedUnitCount).as("The '/outcome/dispatched_unit_count' value should be present and equal to 3.0f.")
									   .isPresent()
									   .hasValue(3.0f);
	}
	
	@Test
	void testGetDoubleAt() {
		Optional<Double> dispatchedUnitCount = underTest.getDoubleAt("/outcome/dispatched_unit_count");
		
		assertThat(dispatchedUnitCount).as("The '/outcome/dispatched_unit_count' value should be present and equal to 3.0.")
									   .isPresent()
									   .hasValue(3.0);
		
	}
	
	@Test
	void testGetShortAt() {
		Optional<Short> windowCount = underTest.getShortAt("/outcome/dispatched_unit_count");
		
		assertThat(windowCount).as("The '/data/window_count' value should be present and equal to 3.")
							   .isPresent()
							   .hasValue((short)3);
		
	}
}
