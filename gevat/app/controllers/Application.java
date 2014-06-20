package controllers;

import java.sql.SQLException;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard;

/**
 * Performs the redirecting.
 */
public class Application extends Controller {

	/**
	 * Redirect the index page to the dashboard.
	 * 
	 * @return action result
	 */
	public static Result index() {
		return redirect("/dashboard");
	}

	/**
	 * Secure the dashboard page.
	 * 
	 * @throws SQLException
	 *             exception
	 * @return action result
	 */
	@Security.Authenticated(Secured.class)
	public static Result dashboard() throws SQLException {
		return ok(dashboard.render(Authentication.getUser()));
	}

	/**
	 * JavaScript routing for handling Ajax request.
	 * 
	 * @return action result
	 */
	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");

		// Routes for Projects
		return ok(Routes.javascriptRouter("jsRoutes",
				controllers.routes.javascript.Patients.remove(),
				controllers.routes.javascript.Patients.isProcessed(),
				controllers.routes.javascript.Mutations.proteinsJSON()));
	}

}
