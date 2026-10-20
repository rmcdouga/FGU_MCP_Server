package io.github.rmcdouga.fg_peekaboo_proto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.rmcdouga.fg_peekaboo_proto.JacksonJsonData.JacksonJsonDataFactory;
import tools.jackson.databind.json.JsonMapper;

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
	
	private static final String SEE_JSON = """
			{
			  "data" : {
			    "files" : [
			      {
			        "item_label" : "Fantasy Grounds",
			        "mime_type" : "image/png",
			        "window_index" : 0,
			        "window_id" : 13556,
			        "path" : "/var/folders/ql/8mk4yjnj56g2rwvf2_75yvbh0000gn/T/junit-5309488686238177626/PeekabooMcpClientTest_testSee_screenshot.png",
			        "window_title" : "Fantasy Grounds"
			      }
			    ],
			    "observations" : [
			      {
			        "warnings" : [
			
			        ],
			        "target" : {
			          "source" : "window-id",
			          "window_id" : 13556,
			          "open_if_needed" : false,
			          "hints" : [
			
			          ],
			          "bounds" : [
			            [
			              0,
			              33
			            ],
			            [
			              1588,
			              962
			            ]
			          ],
			          "resolved_kind" : "window-id",
			          "requested_kind" : "pid"
			        },
			        "coordinates" : {
			          "screen_index" : 0,
			          "coordinate_space" : "global_display_points",
			          "logical_bounds" : [
			            [
			              0,
			              33
			            ],
			            [
			              1588,
			              962
			            ]
			          ],
			          "image_size_pixels" : {
			            "width" : 1588,
			            "height" : 962
			          },
			          "scale_factor" : 1
			        },
			        "spans" : [
			          {
			            "metadata" : {
			
			            },
			            "name" : "state.snapshot",
			            "duration_ms" : 35.703791
			          },
			          {
			            "metadata" : {
			
			            },
			            "name" : "target.resolve",
			            "duration_ms" : 4.661875
			          },
			          {
			            "metadata" : {
			              "engine" : "CGWindowList"
			            },
			            "name" : "capture.window",
			            "duration_ms" : 84.731208
			          },
			          {
			            "metadata" : {
			
			            },
			            "name" : "output.write",
			            "duration_ms" : 0.823667
			          },
			          {
			            "metadata" : {
			
			            },
			            "name" : "output.raw.write",
			            "duration_ms" : 0.820584
			          },
			          {
			            "metadata" : {
			
			            },
			            "name" : "desktop.observe",
			            "duration_ms" : 227.293458
			          }
			        ],
			        "state_snapshot" : {
			          "window_count" : 0,
			          "running_application_count" : 213,
			          "display_count" : 0
			        }
			      }
			    ]
			  },
			  "success" : true,
			  "target_receipt" : {
			    "process_start_identity_decimal" : "1792439662433537",
			    "pid" : 27467,
			    "process_start_identity" : 1792439662433537,
			    "window_id" : 13556
			  },
			  "debug_logs" : [
			    "[2026-10-19T20:33:17.140Z] DEBUG: Runtime host: remote onDemand via /Users/robmcdougall/Library/Application Support/Peekaboo/daemon-db70873581b26730.sock (build 4.9.5 (4.9.5))",
			    "[2026-10-19T20:33:17.140Z] VERBOSE [Operation]: Starting operation {mode=auto, annotate=false, menubar=false, app=none, hasAnalyzePrompt=false, operation=see_command}",
			    "[2026-10-19T20:33:17.140Z] VERBOSE [Performance]: Starting timer 'see_command'"
			  ]
			}
			""";

	private static final JsonMapper jsonMapper = new JsonMapper();
	private final JacksonJsonDataFactory jsonFactory = new JacksonJsonDataFactory(jsonMapper);
	private final JsonData underTest;
	
	public JacksonJsonDataTest() {
		this.underTest = jsonFactory.parseJson(VALID_JSON);
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

	@Test
	void testArrayAt_FromSeeJson() {
		//		Optional<JsonData> windowCount = underTest.subsetAt("/data/observations/coordinates/image_size_pixels/width");
		Optional<Integer> windowCount = jsonFactory.parseJson(SEE_JSON)
														.arrayAt("/data/observations")
														.findFirst()
														.get()
														.getIntAt("/coordinates/image_size_pixels/width")
														;
		
		assertThat(windowCount).as("The '/data/observations[0]/coordinates/image_size_pixels/width' value should be present and equal to 1588.")
							   .isPresent()
							   .hasValue(1588);
	}
	
	@Test
	void testSubsetAt_FromSeeJson() {
		//		Optional<JsonData> windowCount = underTest.subsetAt("/data/observations/coordinates/image_size_pixels/width");
		Optional<JsonData> windowCount = jsonFactory.parseJson(SEE_JSON)
														.subsetAt("/target_receipt")
														;
		
		assertThat(windowCount).as("The '/target_receipt' value should be present and equal to json string.")
							   .isPresent()
							   .hasValueSatisfying(v->v.toString().equals("""
									{
									    "process_start_identity_decimal" : "1792439662433537",
									    "pid" : 27467,
									    "process_start_identity" : 1792439662433537,
									    "window_id" : 13556
									}
							   		"""));
	}

}
