package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.start;
import static play.test.Helpers.status;
import static play.test.Helpers.stop;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controllers.Authentication;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import play.test.FakeApplication;
import static org.mockito.Mockito.*;

/**
 * Test the authentication controller.
 * 
 * @author willem
 * 
 */
public class AuthenticationTest {

	/**
	 * Contains data for login form.
	 */
	private final static Map<String, String> data = new HashMap<String, String>();

	/**
	 * Save the result of the called action to authenticate.
	 */
	private static Result result;

	/**
	 * Save the logged in session in a cookie.
	 */
	private static Cookie playSession;

	/**
	 * Save the http request for the http context
	 */
	private final Http.Request request = mock(Http.Request.class);

	/**
	 * Helper to start a fakeApplication to test.
	 */
	private static FakeApplication fakeApplication;

	/**
	 * Start the fakeApplication.
	 */
	@BeforeClass
	public static void startFakeApplication() {
		fakeApplication = fakeApplication();
		start(fakeApplication);
	}

	/**
	 * Stop the fakeApplication.
	 */
	@AfterClass
	public static void shutDownFakeApplication() {
		stop(fakeApplication);
	}

	/**
	 * Setup login for each test
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		Map<String, String> flashData = Collections.emptyMap();
		Map<String, Object> argData = Collections.emptyMap();
		Long id = 2L;
		play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
		Http.Context context = new Http.Context(id, header, request, flashData,
				flashData, argData);
		Http.Context.current.set(context);

		data.put("username", "user");
		data.put("password", "pass");

		result = callAction(
				controllers.routes.ref.Authentication.authenticate(),
				fakeRequest().withFormUrlEncodedBody(data));

		playSession = play.test.Helpers.cookie("PLAY_SESSION", result);
	}

	/**
	 * Test if the login page renders correctly.
	 */
	@Test
	public void testLogin() {
		Result result = callAction(controllers.routes.ref.Authentication
				.login());
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("text/html");
		assertThat(charset(result)).isEqualTo("utf-8");
		assertThat(contentAsString(result)).contains("Login");
	}

	/**
	 * Test if a valid user can authenticate.
	 */
	@Test
	public void testAuthenticate() {
		// test valid user
		assertThat(status(result)).isEqualTo(SEE_OTHER);

		// test invalid password
		data.put("password", "wrongpass");
		result = callAction(
				controllers.routes.ref.Authentication.authenticate(),
				fakeRequest().withFormUrlEncodedBody(data));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);

		// test invalid user
		data.put("username", "wronguser");
		result = callAction(
				controllers.routes.ref.Authentication.authenticate(),
				fakeRequest().withFormUrlEncodedBody(data));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);
	}

	/**
	 * Test if the logout page renders correctly.
	 */
	@Test
	public void testLogout() {
		result = callAction(controllers.routes.ref.Authentication.logout(),
				fakeRequest().withCookies(playSession));
		assertThat(status(result)).isEqualTo(SEE_OTHER);
	}

	/**
	 * Test if the right user is returned.
	 * 
	 * @throws SQLException
	 */
	// @Test
	public void testGetUser() throws SQLException {
		User u = Authentication.getUser();

	}
}
