package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import play.mvc.Http;
import play.mvc.Result;

/**
 * Test Secured controller.
 * 
 * @author willem
 * 
 */
public class SecuredTest {

	private final Http.Request request = mock(Http.Request.class);

	/**
	 * Test getUsername method.
	 */
	// @Test
	public void testGetUsername() {
		Map<String, String> flashData = Collections.emptyMap();
		Map<String, Object> argData = Collections.emptyMap();
		Long id = 2L;
		play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
		Http.Context context = new Http.Context(id, header, request, flashData,
				flashData, argData);
		//System.out.println(Secured.getUsername(context));
	}

	/**
	 * Test onUnauthorized method.
	 */
	@Test
	public void testOnUnauthorized() {
		Result result = callAction(controllers.routes.ref.Application.about());
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("text/html");
		assertThat(charset(result)).isEqualTo("utf-8");
		assertThat(contentAsString(result)).contains("About");
	}
}
