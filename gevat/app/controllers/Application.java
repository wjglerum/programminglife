package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.data.*;
import static play.data.Form.*;

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
    return ok(dashboard.render("Welcome to GEVATT!", getUser()));
  }

  /**
   * Render the help page (no authentication required).
   */
  public static Result help() {
    return ok(help.render(getUser()));
  }

  /**
   * Render the about page (no authentication required).
   */
  public static Result about() {
    return ok(about.render(getUser()));
  }
  
  /**
   * Login form class used for authentication.
   */
  public static class Login {

    public String username;
    public String password;
    
    public String validate() {
      if (User.authenticate(username, password) == null)
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
      
      return redirect(
        routes.Application.index()
      );
    }
  }

  /**
   * Logout and clean the session.
   */
  public static Result logout() {
    session().clear();
    
    flash("success", "You've been logged out");
    
    return redirect(
      routes.Application.login()
    );
  }
  
  /**
   * Get the current session User
   */
  public static User getUser() {
    String username = session("username");
    
    // TODO query User from database based on username
    
    return new User(username, "Test User", "pass");
  }

}
