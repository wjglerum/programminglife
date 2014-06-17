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

/**
 * Provides the mutation view.
 */
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
	 * @param pId
	 *            The id of the patient
	 * @param mId
	 *            The id of the mutation
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(final int pId, final int mId) throws SQLException {
		Patient p = patientService.get(pId, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(pId);

		if (p == null) {
			return Patients.patientNotFound();
		}
		// Render the mutation if it's found in the requested patient's
		// mutations
		final int limit = 10;
		final int threshold = 700;
		for (Mutation m : mutations) {

			if (m.getId() == mId) {
				String jSON = mutationJSON(p, m, limit, threshold);
				String positions = positionJSON(m, 10);
				return ok(mutation.render(p, m, Authentication.getUser(), jSON, positions));
			}
		}

		return mutationNotFound(p, mutations);
	}

	/**
	 * Makes mutations into JSON to be transported for use in JavaScript.
	 * 
	 * @param patient
	 *            The patient
	 * @param mutation
	 *            The mutation
	 * @param limit
	 *            The maximum amount of proteins
	 * @param threshold
	 *            The lowest value allowed
	 * @return Returns JSON as a String
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@SuppressWarnings("unchecked")
	private static String mutationJSON(final Patient patient,
			final Mutation mutation, final int limit, final int threshold)
			throws SQLException {
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
			proteinJSON.put("annotations",
					proteinService.getAnnotations(proteine.getName()));
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
	 * Handle the ajax request for removing patients.
	 * 
	 * @param pId
	 *            The id of the patient
	 * @param mId
	 *            The id of the mutation
	 * @param limit
	 *            The maximum amount of proteins
	 * @param threshold
	 *            The lowest value allowed
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static Result proteinsJSON(final int pId, final int mId,
			final int limit, final int threshold) throws SQLException {
		Patient p = patientService.get(pId, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(pId);

		if (p == null) {
			return badRequest();
		}

		// Render the mutation if it's found in the requested patient's
		// mutations
		for (Mutation m : mutations) {
			if (m.getId() == mId) {
				String jSON = mutationJSON(p, m, limit, threshold);

				response().setContentType("application/json");

				return ok(jSON);
			}
		}

		return badRequest();
	}

	/**
	 * Return to the patient page and display a message if the requested
	 * mutation isn't found.
	 * 
	 * @param p
	 *            The patient
	 * @param mutations
	 *            The list of mutations
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@Security.Authenticated(Secured.class)
	public static Result mutationNotFound(final Patient p,
			final List<Mutation> mutations) throws SQLException {
		flash("mutation-not-found",
				"The requested mutation could not be found or "
						+ "you don't have permissions to view the "
						+ "mutation. "
						+ "Select another one in the overview below.");

		HashMap<Mutation, Double> map = new HashMap<Mutation, Double>();
		for (Mutation m : mutations) {
			map.put(m, (double) mutationService.getScore(m));
		}
		return notFound(patient.render(p, map, Authentication.getUser()));
	}

	/**
	 * Make positions of the mutation and related genes a JSON string
	 * @param m
	 * @return The JSON string
	 */
	@SuppressWarnings("unchecked")
	public static String positionJSON(Mutation m, int amount) throws SQLException {
		HashMap<String, ArrayList<Integer>> map = mutationService
				.getPositions(m, amount);

		JSONArray positionsJSON = new JSONArray();

		for (String name : map.keySet()) {
			JSONObject positionJSON = new JSONObject();
			positionJSON.put("name", name);
			positionJSON.put("start", map.get(name).get(0));
			positionJSON.put("end", map.get(name).get(1));
			positionsJSON.add(positionJSON);
		}

		return positionsJSON.toString();
	}

}
