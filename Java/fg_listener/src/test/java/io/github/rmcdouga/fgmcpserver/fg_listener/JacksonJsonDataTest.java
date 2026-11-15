package io.github.rmcdouga.fgmcpserver.fg_listener;

import tools.jackson.databind.json.JsonMapper;

class JacksonJsonDataTest extends AbstractJsonDataTest {

	public JacksonJsonDataTest() {
		super(s->new JacksonJsonData.JacksonJsonDataFactory(new JsonMapper()).parseJson(s));
	}
	
	// All the tests are inherited from AbstractJsonDataTest.
}
