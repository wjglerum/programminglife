package controllers;

import static play.data.Form.form;

import java.util.List;

import models.Mutation;
import models.Patient;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.patient;
import views.html.mutation;

public class Mutations extends Controller {

  /**
   * Display a mutation of a patient.
   */
  @Security.Authenticated(Secured.class)
  public static Result show(int p_id, int m_id) {
    Patient p = Patient.get(p_id);
    List<Mutation> mutations = Mutation.getMutations(p_id);
    
    for (Mutation m : mutations) {
      if (m.id == m_id)
        return ok(mutation.render(p, m, Authentication.getUser()));
    }

    // Return to the patient page and display a message the requested mutation isn't found
    flash("mutation-not-found",
        "The requested mutation could not be found or you don't have permissions to view the mutation.");
  
    return notFound(patient.render(p, mutations, Authentication.getUser()));
  }

}
