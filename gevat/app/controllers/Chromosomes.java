package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.application.Patient;
import models.dna.Mutation;
import models.protein.Protein;
import models.protein.ProteinConnection;
import models.protein.ProteinGraph;
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
	  List<Mutation> mutations = Mutation.getMutations(p_id, c_id);
	  models.dna.Chromosome c = new models.dna.Chromosome(c_id);
	  
	  if (p == null)
	      return Patients.patientNotFound();
	  
	  // Render the chromosome view if there are mutations in the view
	  return ok(chromosome.render(p, c, mutations, Authentication.getUser()));
  }
}