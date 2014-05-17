package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

  public static Result index() {
    return redirect("/dashboard");
  }

  public static Result dashboard() {
    return ok(dashboard.render("Welcome to GEVATT!!! Dit is even een test om te kijken of de auto deploy functie werkt."));
  }

  public static Result help() {
    return ok(help.render());
  }

  public static Result about() {
    return ok(about.render());
  }

}
