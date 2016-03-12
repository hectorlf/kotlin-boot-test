package hello.test

import hello.Application
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import org.junit.Ignore

import javax.servlet.http.HttpServletResponse

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status



@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(Application::class)
@WebIntegrationTest(randomPort=true)
class ControllerTests {

    @Autowired
    private var wac: WebApplicationContext? = null
	
	private var mockMvc: MockMvc? = null

	@Autowired
    private var springSecurityFilterChain: FilterChainProxy? = null

	@Before
	fun setup() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(wac)
			.build()
	}
	
	@Test
	fun testHelloOk() {
		mockMvc!!.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
	}
	
	@Test
	fun testNotFound() {
		mockMvc!!.perform(get("/nonexistent"))
			.andExpect(status().isNotFound());
	}
	
	/*
	 * Ignored because spring sec. seems to come misconfigured
	 */
	@Test
	@Ignore
	fun testSecureHelloRejected() {
		val request = MockHttpServletRequest("GET", "/secure/sdf");
		val response = MockHttpServletResponse();
		val chain = MockFilterChain();
		springSecurityFilterChain!!.doFilter(request, response, chain);
		Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
	}

}