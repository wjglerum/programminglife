package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;
import models.patient.PatientRepositoryDB;
import models.patient.PatientService;
import models.protein.Protein;
import models.protein.ProteinConnection;
import models.protein.ProteinGraph;
import models.protein.ProteinRepositoryDB;
import models.protein.ProteinService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.mutation;
import views.html.patient;

public class Mutations extends Controller {

	private static PatientRepositoryDB patientRepository = new PatientRepositoryDB();
	private static PatientService patientService = new PatientService(
			patientRepository);
	private static MutationRepositoryDB mutationRepository = new MutationRepositoryDB();
	private static MutationService mutationService = new MutationService(
			mutationRepository);
	private static ProteinRepositoryDB proteinRepository = new ProteinRepositoryDB();
	private static ProteinService proteinService = new ProteinService(
			proteinRepository);

	/**
	 * Display a mutation of a patient.
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int p_id, int m_id) throws SQLException {
		Patient p = patientService.get(p_id, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(p_id);

		if (p == null)
			return Patients.patientNotFound();

		// Render the mutation if it's found in the requested patient's
		// mutations
		for (Mutation m : mutations) {
			if (m.getId() == m_id) {
				String JSON = mutationJSON(p, m, 10, 700);

				return ok(mutation.render(p, m, Authentication.getUser(), JSON));
			}
		}

		return mutationNotFound(p, mutations);
	}

	private static String mutationJSON(Patient patient, Mutation mutation,
			int limit, int threshold) throws SQLException {
		// Remove the 'rs' part of the rsID
		int rsID = Integer.parseInt(mutation.getRsID().substring(2));

		ProteinGraph proteinGraph = new ProteinGraph(rsID, limit, threshold);

		Collection<Protein> proteins = proteinGraph.getProteines();
		Collection<ProteinConnection> connections = proteinGraph
				.getConnections();

		// Setup JSON data
		JSONObject dataJSON = new JSONObject();

		JSONArray proteinsJSON = new JSONArray();
		JSONArray connectionsJSON = new JSONArray();

		for (Protein proteine : proteins) {
			JSONObject proteinJSON = new JSONObject();

			proteinJSON.put("name", proteine.getName());
			proteinJSON.put("annotations", proteinService.getAnnotations(proteine.getName()));
			proteinJSON.put("disease", proteine.getDisease());

			JSONArray mutationsJSON = new JSONArray();

			ArrayList<Mutation> relatedMutations = proteinService
					.getRelatedMutations(patient, mutation);

			for (Mutation m : relatedMutations) {
				JSONObject mutationJSON = new JSONObject();

				mutationJSON.put("rsid", m.getRsID());
				mutationJSON.put("id", m.getId());
				mutationJSON.put("patient", patient.getId());

				mutationsJSON.add(mutationJSON);
			}

			proteinJSON.put("related", mutationsJSON);

			proteinsJSON.add(proteinJSON);
		}

		for (ProteinConnection connection : connections) {
			JSONObject connectionJSON = new JSONObject();

			connectionJSON.put("from", connection.getProteineFrom().getName());
			connectionJSON.put("to", connection.getProteineTo().getName());
			connectionJSON.put("score", connection.getCombinedScore());

			connectionsJSON.add(connectionJSON);
		}

		dataJSON.put("proteins", proteinsJSON);
		dataJSON.put("relations", connectionsJSON);
		dataJSON.put("limit", limit);
		dataJSON.put("threshold", threshold);

		return dataJSON.toJSONString();
	}

	/**
	 * Handle the ajax request for removing patients
	 * 
	 * @throws SQLException
	 */
	public static Result proteinsJSON(int p_id, int m_id, int limit,
			int threshold) throws SQLException {
		Patient p = patientService.get(p_id, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(p_id);

		if (p == null)
			return badRequest();

		// Render the mutation if it's found in the requested patient's
		// mutations
		for (Mutation m : mutations) {
			if (m.getId() == m_id) {
				String JSON = mutationJSON(p, m, limit, threshold);

				response().setContentType("application/json");

				return ok(JSON);
			}
		}

		return badRequest();
	}

	/**
	 * Return to the patient page and display a message the requested mutation
	 * isn't found
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result mutationNotFound(Patient p, List<Mutation> mutations)
			throws SQLException {
		flash("mutation-not-found",
				"The requested mutation could not be found or you don't have permissions to view the mutation. Select another one in the overview below.");

		HashMap<Mutation, Double> map = new HashMap<Mutation, Double>();
		for (Mutation m : mutations) {
			map.put(m, (double) mutationService.getScore(m));
		}
		return notFound(patient.render(p, map, Authentication.getUser()));
	}

}
