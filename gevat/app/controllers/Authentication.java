package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.data.*;
import static play.data.Form.*;

import models.*;
import views.html.*;

public class Authentication extends Controller {
  
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
    
    flash("logged-out", "You've been successfully logged out");
    
    return redirect(
      routes.Authentication.login()
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
