package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.User;

import org.junit.Test;

import controllers.Authentication;
import play.mvc.Result;
import play.mvc.Http.Cookie;
import play.*;

public class AuthenticationTest {

	/**
	 * Test if the login page renders correctly
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
	 * 
	 */
	// @Test
	public void testAuthenticate() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = callAction(controllers.routes.ref.Authentication
						.authenticate());
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentType(result)).isEqualTo("text/html");
				assertThat(charset(result)).isEqualTo("utf-8");
				assertThat(contentAsString(result)).contains("Dashboard");
			}

		});
	}

	/**
	 * Test if the logout page renders correctly
	 */
	@Test
	public void testLogout() {
		running(fakeApplication(), new Runnable() {
			public void run() { 
				// LOGIN
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(controllers.routes.ref.Authentication.authenticate(), fakeRequest().withFormUrlEncodedBody(data));
				
				// RECOVER COOKIE FROM LOGIN RESULT
				final Cookie playSession = play.test.Helpers.cookie("PLAY_SESSION", result);
				// LIST SOMETHING (using cookie of the login result)
				result = callAction(controllers.routes.ref.Authentication.logout(), fakeRequest().withCookies(playSession));
				assertThat(status(result)).isEqualTo(303);
			}
		});
	}

	/**
	 * Test if the right user is returned
	 */
	// @Test
	public void testGetUser() {
		running(fakeApplication(), new Runnable() {
			public void run() { // LOGIN
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				// RECOVER COOKIE FROM LOGIN RESULT
				final Cookie playSession = play.test.Helpers.cookie(
						"PLAY_SESSION", result);
				// LIST SOMETHING (using cookie of the login result)
				try {
					User u = Authentication.getUser();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
