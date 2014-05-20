package controllers;

import play.*;
import play.mvc.*;

import models.*;
import views.html.*;

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
  }

}
