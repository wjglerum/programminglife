package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;
import static play.test.Helpers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.FakeApplication;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import controllers.Patients.Add;
import static org.mockito.Mockito.*;

public class PatientsTest {

	/**
	 * Contains data for login form.
	 */
	private final static Map<String, String> data = new HashMap<String, String>();

	private final Http.Request request = mock(Http.Request.class);

	/**
	 * Helper to start a fakeApplication to test.
	 */
	private static FakeApplication fakeApplication;

	private static Cookie playSession;
	private static Result result;

	/**
	 * Start the fakeApplication.
	 */
	@BeforeClass
	public static void startFakeApplication() {
		fakeApplication = fakeApplication();
		start(fakeApplication);
		data.put("username", "user");
		data.put("password", "pass");
		result = callAction(
				controllers.routes.ref.Authentication.authenticate(),
				fakeRequest().withFormUrlEncodedBody(data));
		// Recover cookie from login result
		playSession = play.test.Helpers.cookie("PLAY_SESSION", result);
	}

	@Before
	public void setUp() throws Exception {

	}

	/**
	 * Stop the fakeApplication.
	 */
	@AfterClass
	public static void shutDownFakeApplication() {
		stop(fakeApplication);
	}

	/**
	 * Test that when a logged in user wants to see all patients, it really
	 * arrives at the patients overview page
	 * 
	 */
	@Test
	public void testPatientsControllerShowAll() {
		result = callAction(controllers.routes.ref.Patients.showAll(),
				fakeRequest().withCookies(playSession));
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentAsString(result)).contains("Patients overview");
	}

	/**
	 * Test that when a non excisting patient is called, the user get's a 404
	 * page
	 */
	@Test
	public void testPatientsControllerPatientNotFound() {
		int nonExistingPatientID = Integer.MAX_VALUE;
		result = callAction(
				controllers.routes.ref.Patients.show(nonExistingPatientID),
				fakeRequest().withCookies(playSession));
		assertThat(status(result)).isEqualTo(NOT_FOUND);
	}

	/**
	 * Test that when a client wants to add a patient, the correct fields are
	 * shown
	 */
	@Test
	public void testPatientsControllerAddPatient() {
		// Request the add page
		result = callAction(controllers.routes.ref.Patients.add(),
				fakeRequest().withCookies(playSession));
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentAsString(result)).contains("Add new patient");
	}

	/**
	 * Test that when a client wants to add a patient without filling in the
	 * forms, a http 400 error is shown that
	 */
	@Test
	public void testPatientsControllerAddInvalidPatient() {
		// Request the add page
		result = callAction(controllers.routes.ref.Patients.insert(),
				fakeRequest().withCookies(playSession));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);
	}

	/**
	 * Test that when a client wants to remove a patient that does not exist, a
	 * http 400 error is shown
	 */
	@Test
	public void testPatientsControllerRemoveInvalidPatient() {
		int nonExistingPatientID = Integer.MAX_VALUE;
		// Request the add page
		result = callAction(
				controllers.routes.ref.Patients.remove(nonExistingPatientID),
				fakeRequest().withCookies(playSession));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);
	}

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
}
