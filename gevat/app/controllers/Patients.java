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
    Patient a = new Patient(id,"A","A");
    
    return ok(patient.render(a, Application.getUser()));
  }

  /**
   * List all patients.
   */
  @Security.Authenticated(Secured.class)
  public static Result showAll() {
    Patient a = new Patient(1,"A","A");
    Patient b = new Patient(2,"B","B");
    Patient c = new Patient(3,"C","C");
    
    return ok(patients.render(Arrays.asList(a,b,c), Application.getUser()));
  }

}
