package controllers;

import java.util.Arrays;

import play.*;
import play.mvc.*;

import models.*;
import views.html.*;

public class Patients extends Controller {

  /**
   * Display a patient.
   */
  @Security.Authenticated(Secured.class)
  public static Result show(int id) {
    Patient a = new Patient("A", id);
    
    return ok(patient.render(a, Application.getUser()));
  }

  /**
   * List all patients.
   */
  @Security.Authenticated(Secured.class)
  public static Result showAll() {
    Patient a = new Patient("A", 1);
    Patient b = new Patient("B", 2);
    Patient c = new Patient("C", 3);
    
    return ok(patients.render(Arrays.asList(a,b,c), Application.getUser()));
  }

}
