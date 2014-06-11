package controllers;

import static play.data.Form.form;

import java.sql.SQLException;

import models.application.User;
import models.application.UserService;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

public class Authentication extends Controller {

	/**
	 * Login form class used for authentication.
	 */
	public static class Login {

		public String username;
		public String password;

		public String validate() throws SQLException {
			if (UserService.authenticate(username, password) == null)
				return "Invalid username or password";
			else
				return null;
		}

	}

	/**
	 * Render the login form.
	 */
	public static Result login() {
		session().clear();

		return ok(login.render(form(Login.class)));
	}

	/**
	 * Authenticate and start a new session.
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
	 */
	public static Result logout() {
		session().clear();

		flash("logged-out", "You've been successfully logged out");

		return redirect(routes.Authentication.login());
	}

	/**
	 * Get the current session User
	 * 
	 * @throws SQLException
	 */
	public static User getUser() throws SQLException {
		String username = session("username");

		if (username != null)
			return UserService.getUser(username);

		return null;
	}

}
