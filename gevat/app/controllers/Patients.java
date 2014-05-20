package controllers;

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
    Patient p = Patient.get(id);
    
    return ok(patient.render(p, Application.getUser()));
  }

  /**
   * List all patients.
   */
  @Security.Authenticated(Secured.class)
  public static Result showAll() {
    return ok(patients.render(Patient.getAll(), Application.getUser()));
  }

}
