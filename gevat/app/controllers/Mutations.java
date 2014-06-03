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
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patients;
import views.html.mutation;

public class Mutations extends Controller {

  /**
   * Display a mutation of a patient.
   * @throws SQLException 
   */
  @Security.Authenticated(Secured.class)
  public static Result show(int p_id, int m_id) throws SQLException {
    Patient p = Patient.get(p_id, Authentication.getUser().id);
    List<Mutation> mutations = Mutation.getMutations(p_id);

    if (p == null)
      return Patients.patientNotFound();
    
    // Render the mutation if it's found in the requested patient's mutations
    for (Mutation m : mutations) {
      if (m.getId() == m_id) {
        String JSON = mutationJSON(m, 10, 700);
        
        return ok(mutation.render(p, m, Authentication.getUser(), JSON));
      }
    }
    
    return mutationNotFound(p, mutations);
  }
  
  private static String mutationJSON(Mutation mutation, int limit, int threshold) throws SQLException {
    // Remove the 'rs' part of the rsID
    int rsID = Integer.parseInt(mutation.getRsID().substring(2));
    
    Collection<Proteine> proteins = Proteine.getProteinesByID(rsID, limit, threshold);
    
    // Setup JSON data
    JSONObject dataJSON = new JSONObject();
    
    JSONArray proteinsJSON = new JSONArray();
    JSONArray relationsJSON = new JSONArray();
    
    for (Proteine proteine : proteins) {
      JSONObject proteinJSON = new JSONObject();
      
      proteinJSON.put("name", proteine.getName());
      proteinJSON.put("annotations", proteine.getAnnotations());
      
      proteinsJSON.add(proteinJSON);
      
      for (ProteineConnection connection : proteine.getConnections()) {
        JSONObject connectionJSON = new JSONObject();
        
        connectionJSON.put("from", connection.getProteineFrom().getName());
        connectionJSON.put("to", connection.getProteineTo().getName());
        connectionJSON.put("score", connection.getCombinedScore());
        
        relationsJSON.add(connectionJSON);
      }
    }
    
    dataJSON.put("proteins", proteinsJSON);
    dataJSON.put("relations", relationsJSON);
    dataJSON.put("limit", limit);
    dataJSON.put("threshold", threshold);
    
    return dataJSON.toJSONString();
  }

  /**
   * Handle the ajax request for removing patients
 * @throws SQLException 
   */
  public static Result proteinsJSON(int p_id, int m_id, int limit, int threshold) throws SQLException {
    Patient p = Patient.get(p_id, Authentication.getUser().id);
    List<Mutation> mutations = Mutation.getMutations(p_id);

    if (p == null)
      return badRequest();
    
    // Render the mutation if it's found in the requested patient's mutations
    for (Mutation m : mutations) {
      if (m.getId() == m_id) {
        String JSON = mutationJSON(m, limit, threshold);
        
        response().setContentType("application/json");
        
        return ok(JSON);
      }
    }
    
    return badRequest();
  }
  
  /**
   * Return to the patient page and display a message the requested mutation isn't found
   * @throws SQLException 
   */
  @Security.Authenticated(Secured.class)
  public static Result mutationNotFound(Patient p, List<Mutation> mutations) throws SQLException { 
    flash("mutation-not-found",
        "The requested mutation could not be found or you don't have permissions to view the mutation. Select another one in the overview below.");

    return notFound(patient.render(p, mutations, Authentication.getUser()));
  }

}
