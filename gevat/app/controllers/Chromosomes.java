package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.Mutation;
import models.Patient;
import models.Proteine;
import models.ProteineConnection;
import models.ProteineGraph;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.chromosome;
import views.html.patient;
import views.html.patients;
import views.html.mutation;

public class Chromosomes extends Controller {

  /**
   * Display all the mutations in a certain chromosome of a patient.
   * @throws SQLException 
   */
  @Security.Authenticated(Secured.class)
  public static Result show(int p_id, int c_id) throws SQLException {
	  Patient p = Patient.get(p_id, Authentication.getUser().id);
	  models.Chromosome c = new models.Chromosome(1);
	  return ok(chromosome.render(p, c, Authentication.getUser()));
	  //        return ok(mutation.render(p, m, Authentication.getUser(), JSON));
  }
}