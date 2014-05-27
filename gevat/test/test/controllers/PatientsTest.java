package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Http.Cookie;
import play.mvc.Result;
import controllers.Patients.Add;

public class PatientsTest {

	private Result result;
	private Cookie playSession;

	/**
	 * Check if we get an an error message if a name is not set
	 */
	@Test
	public void NameIsNull() {
		Add newAdd = new Add();
		String warning = new String();
		warning = "An invalid name is entered. Please fill in a name consisting of at least 3 characters";
		assertThat(warning).isEqualTo(newAdd.validate());
	}

	/**
	 * Check if we get an an error message if a name has less than 3 characters
	 */
	@Test
	public void NameIsSmallerThan3() {
		Add newAdd = new Add();
		String warning = new String();
		newAdd.name = "j";
		newAdd.surname = "Doe";
		warning = "An invalid name is entered. Please fill in a name consisting of at least 3 characters";
		assertThat(warning).isEqualTo(newAdd.validate());
	}

	/**
	 * Check if we get an an error message if a surname is less than 3
	 * characters
	 */
	@Test
	public void SurnameIsSmallerThan3() {
		Add newAdd = new Add();
		String warning = new String();
		newAdd.name = "John";
		newAdd.surname = "D";
		warning = "An invalid surname is entered. Please fill in a surname consisting of at least 3 characters";
		assertThat(warning).isEqualTo(newAdd.validate());
	}

	/**
	 * Check if we get an error message if a surname is not set
	 */
	@Test
	public void SurnameIsNull() {
		Add newAdd = new Add();
		newAdd.name = "John";
		String warning = new String();
		warning = "An invalid surname is entered. Please fill in a surname consisting of at least 3 characters";
		assertThat(warning).isEqualTo(newAdd.validate());
	}

	@Before
	public void setupLogin() {
		running(fakeApplication(), new Runnable() {
			public void run() { // Login
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				// Recover cookie from login result
				playSession = play.test.Helpers.cookie("PLAY_SESSION", result);
			}
		});
	}

	/**
	 * Test that when a logged in user wants to see all patients, it really
	 * arrives at the patients overview page
	 * 
	 */
	@Test
	public void testPatientsControllerShowAll() {
		running(fakeApplication(), new Runnable() {
			public void run() { // Login
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				// Recover cookie from login result
				final Cookie playSession = play.test.Helpers.cookie(
						"PLAY_SESSION", result);
				// List something (using cookie of the login result)
				result = callAction(controllers.routes.ref.Patients.showAll(),
						fakeRequest().withCookies(playSession));
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentAsString(result)).contains(
						"Patients overview");
			}
		});
	}

	/**
	 * Test that when a non excisting patient is called, the user get's a 404
	 * page
	 */
	@Test
	public void testPatientsControllerPatientNotFound() {
		running(fakeApplication(), new Runnable() {
			public void run() { // Login
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				// Recover cookie from login result
				final Cookie playSession = play.test.Helpers.cookie(
						"PLAY_SESSION", result);
				int nonExistingPatientID = Integer.MAX_VALUE;
				// List something (using cookie of the login result)
				result = callAction(controllers.routes.ref.Patients
						.show(nonExistingPatientID),
						fakeRequest().withCookies(playSession));
				assertThat(status(result)).isEqualTo(NOT_FOUND);
			}
		});
	}

	/**
	 * Test that when a client wants to add a patient, the correct fields are
	 * shown
	 */
	@Test
	public void testPatientsControllerAddPatient() {
		running(fakeApplication(), new Runnable() {
			public void run() { // Login
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				// Recover cookie from login result
				final Cookie playSession = play.test.Helpers.cookie(
						"PLAY_SESSION", result);
				// Request the add page
				result = callAction(controllers.routes.ref.Patients.add(),
						fakeRequest().withCookies(playSession));
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentAsString(result)).contains("Add new patient");
			}
		});
	}

	/**
	 * Test that when a client wants to add a patient without filling in the
	 * forms, a http 400 error is shown that
	 */
	@Test
	public void testPatientsControllerAddInvalidPatient() {
		running(fakeApplication(), new Runnable() {
			public void run() { // Login
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				// Recover cookie from login result
				final Cookie playSession = play.test.Helpers.cookie(
						"PLAY_SESSION", result);
				// Request the add page
				result = callAction(controllers.routes.ref.Patients.insert(),
						fakeRequest().withCookies(playSession));
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
			}
		});
	}

	/**
	 * Test that when a client wants to remove a patient that does not exist, a
	 * http 400 error is shown
	 */
	@Test
	public void testPatientsControllerRemoveInvalidPatient() {
		running(fakeApplication(), new Runnable() {
			public void run() { // Login
				final Map<String, String> data = new HashMap<String, String>();
				data.put("username", "user");
				data.put("password", "pass");
				Result result = callAction(
						controllers.routes.ref.Authentication.authenticate(),
						fakeRequest().withFormUrlEncodedBody(data));
				int nonExistingPatientID = Integer.MAX_VALUE;
				// Recover cookie from login result
				final Cookie playSession = play.test.Helpers.cookie(
						"PLAY_SESSION", result);
				// Request the add page
				result = callAction(controllers.routes.ref.Patients
						.remove(nonExistingPatientID), fakeRequest()
						.withCookies(playSession));
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
			}
		});
	}
}
