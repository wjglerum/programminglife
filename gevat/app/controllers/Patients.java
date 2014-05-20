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
    
    // Return to the patients overview and display a message the requested patient isn't found
    if (p == null) {
      flash("patient-not-found", "The requested patient could not be found. Please select another one below.");
      
      return notFound(patients.render(Patient.getAll(), Authentication.getUser()));
    }
    
    // Render the patient otherwise
    return ok(patient.render(p, Authentication.getUser()));
  }

  /**
   * List all patients.
   */
  @Security.Authenticated(Secured.class)
  public static Result showAll() {
    return ok(patients.render(Patient.getAll(), Authentication.getUser()));
  }

}
