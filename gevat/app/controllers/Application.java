package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

  public static Result index() {
    return redirect("/dashboard");
  }

  public static Result dashboard() {
    return ok(dashboard.render("Welcome to GEVATT!"));
  }

  public static Result help() {
    return ok(help.render());
  }

  public static Result about() {
    return ok(about.render());
  }

}
