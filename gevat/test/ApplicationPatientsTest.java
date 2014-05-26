import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.mvc.Http.Cookie;
import play.mvc.Result;
import controllers.Patients.Add;

public class ApplicationPatientsTest {

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
	 * Check if we get an an error message if a name is not set
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
	 * Check if we get an an error message if a name is not set
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

	/**
	 * Check if we get back NULL if the name and surname are valid
	 */
	@Test
	public void ValidNameTest() {
		Add newAdd = new Add();
		newAdd.name = "John";
		newAdd.surname = "Doe";
		assertNull(newAdd.validate());

	}

	@Test
	public void testPatientsControllerShowAll() {
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
				result = callAction(controllers.routes.ref.Patients.showAll(),
						fakeRequest().withCookies(playSession));
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentAsString(result)).contains(
						"Patients overview");
			}
		});
	}
}
