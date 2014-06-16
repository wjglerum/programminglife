package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;

import org.junit.Test;

import play.mvc.Result;

/**
 * Test the standard application controllers.
 * 
 * @author WillemJan
 * 
 */
public class ApplicationTest {

	/**
	 * Test if index redirects to dashboard.
	 */
	@Test
	public void testIndex() {
		Result result = callAction(controllers.routes.ref.Application.index());
		assertThat(status(result)).isEqualTo(SEE_OTHER);
	}

	/**
	 * Test the JS routes.
	 */
	@Test
	public void testJavaScriptRoutes() {
		Result result = callAction(controllers.routes.ref.Application
				.javascriptRoutes());
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("text/javascript");
	}

}
