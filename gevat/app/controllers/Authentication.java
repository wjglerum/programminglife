package controllers;

import static play.data.Form.form;

import java.sql.SQLException;

import models.user.User;
import models.user.UserRepositoryDB;
import models.user.UserService;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

/**
 * Provides the secure authentication.
 *
 */
public class Authentication extends Controller {

	private static UserRepositoryDB userRepository =
			new UserRepositoryDB();
	private static UserService userService =
			new UserService(userRepository);

	/**
	 * Login form class used for authentication.
	 */
	public static class Login {

		public String username;
		public String password;

		/**
		 * Performs the validation.
		 * @return Returns a string or null depending on
		 * 			failure or succes
		 * @throws SQLException exception
		 */
		public final String validate() throws SQLException {
			if (userService.authenticate(this.username,
					this.password) == null) {
				return "Invalid username or password";
			}
			else {
				return null;
			}
		}
	}

	/**
	 * Render the login form.
	 * @return action result
	 */
	public static Result login() {
		session().clear();
		return ok(login.render(form(Login.class)));
	}

	/**
	 * Authenticate and start a new session.
	 * @return action result
	 */
	public static Result authenticate() {
		Form<Login> loginForm = form(Login.class).bindFromRequest();

		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		} else {
			session().clear();
			session("username", loginForm.get().username);

			return redirect(routes.Application.dashboard());
		}
	}

	/**
	 * Logout and clean the session.
	 * @return action result
	 */
	public static Result logout() {
		session().clear();

		flash("logged-out", "You've been successfully logged out");

		return redirect(routes.Authentication.login());
	}

	/**
	 * Get the current session User.
	 *
	 * @return action result
	 * @throws SQLException exception
	 */
	public static User getUser() throws SQLException {
		String username = session("username");
		if (username != null) {
			return userService.getUser(username);
		}
		return null;
	}

}
