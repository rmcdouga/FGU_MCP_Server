package io.github.rmcdouga.fgmcpserver.fg_listener.chat.adapters;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import io.github.rmcdouga.fgmcpserver.fg_listener.FgListenerApplication;
import io.github.rmcdouga.fgmcpserver.fg_listener.JsonData.JsonDataFactory;

@WireMockTest()
@SpringBootTest(webEnvironment = WebEnvironment.NONE, 
				classes = FgListenerApplication.class,
				// Wiremock produces a lot of output, the following entries reduce that output.  They can be removed for debugging.
				properties = {"logging.level.org.wiremock.spring=WARN", "logging.level.WireMock.wiremock=WARN"}

				)
@EnableWireMock(@ConfigureWireMock(
		portProperties = "fglistener.bridge.port"
		)
	)
class RestClientChatRetrieverTest {
	private static final String ACTUAL_SERVER_URL = "http://127.0.0.1:18765/";

	private static final boolean WIREMOCK_RECORDING = false;

	private static final String SERVER_STATUS_RESPONSE_FAIL = """
			{
			  "version" : "0.5.2",
			  "protocol" : 2,
			  "server_epoch" : "edd009ed1bd08b53cffc72f1",
			  "connected" : false,
			  "session" : "",
			  "last_seen_seconds" : null,
			  "context" : { },
			  "pending_commands" : 0,
			  "event_cursor" : 0
			} 
			""";
	
	private static final String SERVER_STATUS_RESPONSE_SUCCESS = """
			{
			  "version" : "0.5.2",
			  "protocol" : 2,
			  "server_epoch" : "05d33dc6d778bf31d224df64",
			  "connected" : true,
			  "session" : "1794511555-305799",
			  "last_seen_seconds" : 2.05,
			  "context" : {
			    "ruleset" : "5E",
			    "host" : true,
			    "version" : "0.4.4",
			    "campaign" : "SimpleTestCampaign"
			  },
			  "pending_commands" : 0,
			  "event_cursor" : 1
			}
			""";
	
	private final RestClientChatRetriever underTest;

	public RestClientChatRetrieverTest(@Autowired RestClient.Builder restClientBuilder, 
									   @Autowired JsonDataFactory jsonDataFactory,
									   @Value("${wiremock.server.baseUrl}") String wireMockUrl
									   ) {
		this.underTest = new RestClientChatRetriever(restClientBuilder, new DefaultUriBuilderFactory(wireMockUrl).builder(), jsonDataFactory);
		super();
	}

	@BeforeEach
	void setUp(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
		if (WIREMOCK_RECORDING) {
			WireMock.startRecording(ACTUAL_SERVER_URL);
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		if (WIREMOCK_RECORDING) {
			SnapshotRecordResult recordings = WireMock.stopRecording();
			List<StubMapping> mappings = recordings.getStubMappings();
			System.out.println("Found " + mappings.size() + " recordings.");
			for (StubMapping mapping : mappings) {
				ResponseDefinition response = mapping.getResponse();
				var jsonBody = response.getJsonBody();
				System.out.println(jsonBody == null ? "JsonBody is null" : jsonBody.toPrettyString());
			}
		}
	}
	
	@Test
	void testStatus_Success() {
		stubFor(WireMock.get(WireMock.urlEqualTo("/api/status"))
				.willReturn(WireMock.aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody(SERVER_STATUS_RESPONSE_SUCCESS)));
		boolean serverUp = underTest.isServerUp();
		assertTrue(serverUp);
		
		verify(getRequestedFor(urlEqualTo("/api/status"))
				.withHeader("Authorization", WireMock.equalTo("Bearer 9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11")))
				;	
	}

	@Test
	void testStatus_Failure() {
		stubFor(WireMock.get(WireMock.urlEqualTo("/api/status"))
				.willReturn(WireMock.aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody(SERVER_STATUS_RESPONSE_FAIL)));
		boolean serverUp = underTest.isServerUp();
		assertFalse(serverUp);
		
		verify(getRequestedFor(urlEqualTo("/api/status"))
				.withHeader("Authorization", WireMock.equalTo("Bearer 9cfae89ac504066a9bdc2368970b552acbe7cea66caea5f7f6d0cb5ffa8a9b11")))
				;	
	}

}
