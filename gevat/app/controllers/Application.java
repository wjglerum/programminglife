package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.about;
import views.html.dashboard;
import views.html.help;

public class Application extends Controller {

  /**
   * Redirect the index page to the dashboard
   */
  public static Result index() {
    return redirect("/dashboard");
  }

  /**
   * Secure the dashboard page.
   */
  @Security.Authenticated(Secured.class)
  public static Result dashboard() {
    return ok(dashboard.render("Welcome to GEVATT!", Authentication.getUser()));
  }

  /**
   * Render the help page (no authentication required).
   */
  public static Result help() {
    return ok(help.render(Authentication.getUser()));
  }

  /**
   * Render the about page (no authentication required).
   */
  public static Result about() {
    return ok(about.render(Authentication.getUser()));
  }    // -- Javascript routing

  /**
   * JavaScript routing for handling Ajax request
   */
  public static Result javascriptRoutes() {
      response().setContentType("text/javascript");
      
      return ok(
          Routes.javascriptRouter("jsRoutes",

              // Routes for Projects
              controllers.routes.javascript.Patients.remove()

          )
      );
  }

}
