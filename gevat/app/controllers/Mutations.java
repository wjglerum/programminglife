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
        int[] rsID = new int[2];
        
        // Remove the 'rs' part of the rsID
        rsID[0] = Integer.parseInt(m.getRsID().substring(2));
        
        Collection<Proteine> proteins;
        
        proteins = Proteine.getProteinesByID(rsID, 10, 10);
         
        Logger.info(proteins.toString());
        Logger.info(Integer.toString(proteins.size()));
        
        // Setup JSON data
        JSONObject dataJSON = new JSONObject();
        
        JSONArray proteinsJSON = new JSONArray();
        JSONArray relationsJSON = new JSONArray();
        
        for (Proteine proteine : proteins) {
          proteinsJSON.add(proteine.getName());
          
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
        
        return ok(mutation.render(p, m, Authentication.getUser(), proteins, dataJSON.toJSONString()));
      }
    }
    
    return mutationNotFound(p, mutations);
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
