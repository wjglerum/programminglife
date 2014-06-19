package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import models.database.QueryProcessor;
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
import models.reader.GeneDiseaseLinkReader;

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
	 * @param patientId
	 *            The id of the patient
	 * @param mutationId
	 *            The id of the mutation
	 * 
	 * @return action result
	 * 
	 * @throws SQLException
	 *             SQL Exception
	 * @throws IOException
	 *             IO Exception
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int patientId, int mutationId)
			throws SQLException, IOException {
		Patient p = patientService.get(patientId, Authentication.getUser()
				.getId());
		List<Mutation> mutations = mutationService.getMutations(patientId);

		if (p == null) {
			return Patients.patientNotFound();
		}
		// Render the mutation if it's found in the requested patient's
		// mutations
		final int limit = 10;
		final int threshold = 700;
		final int amount = 20;
		for (Mutation m : mutations) {
			if (m.getId() == mutationId) {
				String jSON = mutationJSON(p, m, limit, threshold);
				String positions = positionJSON(m, amount);
				String nearby = nearbyMutationsJSON(m, amount, patientId);
				return ok(mutation.render(p, m, Authentication.getUser(), jSON,
						positions, nearby));
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
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static String mutationJSON(final Patient patient,
			final Mutation mutation, final int limit, final int threshold)
			throws SQLException, IOException {
		ProteinGraph proteinGraph = createProteinGraph(mutation, limit,
				threshold);

		Collection<Protein> proteins = proteinGraph.getProteines();
		Collection<ProteinConnection> connections = proteinGraph
				.getConnections();

		// Setup JSON data
		JSONObject dataJSON = new JSONObject();

		JSONArray proteinsJSON = new JSONArray();
		JSONArray connectionsJSON = new JSONArray();

		for (Protein protein : proteins) {
			JSONObject proteinJSON = createProteinJSON(patient, mutation,
					protein);
			proteinsJSON.add(proteinJSON);
		}
		for (ProteinConnection connection : connections) {
			JSONObject connectionJSON = createConnectionJSON(connection);
			connectionsJSON.add(connectionJSON);
		}
		dataJSON.put("proteins", proteinsJSON);
		dataJSON.put("relations", connectionsJSON);
		dataJSON.put("limit", limit);
		dataJSON.put("threshold", threshold);

		return dataJSON.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static JSONObject createConnectionJSON(ProteinConnection connection) {
		JSONObject connectionJSON = new JSONObject();

		connectionJSON.put("from", connection.getProteineFrom().getName());
		connectionJSON.put("to", connection.getProteineTo().getName());
		connectionJSON.put("score", connection.getCombinedScore());
		return connectionJSON;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject createProteinJSON(final Patient patient,
			final Mutation mutation, Protein protein) throws SQLException {
		JSONObject proteinJSON = new JSONObject();

		proteinJSON.put("name", protein.getName());
		proteinJSON.put("hasMutation", protein.hasMutation());
		proteinJSON.put("annotations",
				proteinService.getAnnotations(protein.getName()));
		proteinJSON.put("disease", protein.getDisease());

		JSONArray mutationsJSON = new JSONArray();

		ArrayList<Mutation> relatedMutations = proteinService
				.getRelatedMutations(patient, mutation);

		for (Mutation m : relatedMutations) {
			JSONObject mutationJSON = createMutationJSON(patient, m);
			mutationsJSON.add(mutationJSON);
		}

		proteinJSON.put("related", mutationsJSON);
		return proteinJSON;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject createMutationJSON(final Patient patient,
			Mutation m) {
		JSONObject mutationJSON = new JSONObject();

		mutationJSON.put("rsid", m.getRsID());
		mutationJSON.put("id", m.getId());
		mutationJSON.put("patient", patient.getId());
		return mutationJSON;
	}

	/**
	 * Makes a proteinGraph for a mutation.
	 * 
	 * @param mutation
	 *            The mutation to make the graph for
	 * @param limit
	 *            The maximum amount of proteinConnection added per protein
	 * @param threshold
	 *            The minimum threshold for an added connection
	 * @return a ProteinGraph with proteins on the location of the mutation and
	 *         related proteins
	 * @throws IOException
	 *             IO Exception
	 * @throws SQLException
	 *             SQL Exception
	 */
	public static ProteinGraph createProteinGraph(final Mutation mutation,
			final int limit, final int threshold) throws IOException,
			SQLException {
		// Remove the 'rs' part of the rsID
		int rsID = Integer.parseInt(mutation.getRsID().substring(2));

		ProteinGraph proteinGraph = new ProteinGraph(rsID, limit, threshold);

		for (String protein : QueryProcessor.findGenesAssociatedWithSNP(rsID)) {
			proteinGraph.putMutation(protein);
		}
		return proteinGraph;
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
	 * @return action result
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 * @throws IOException
	 *             IO Exception
	 */
	public static Result proteinsJSON(final int pId, final int mId,
			final int limit, final int threshold) throws SQLException,
			IOException {
		Patient p = patientService.get(pId, Authentication.getUser().getId());
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
	 * @return action result
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
	 * Make positions of the mutation and related genes a JSON string.
	 * 
	 * @param m
	 *            A mutation to find related genes for
	 * @param amount
	 *            The maximum amount of related genes
	 * @return The JSON string
	 * @throws SQLException
	 *             SQL Exception
	 * @throws IOException exception
	 */
	@SuppressWarnings("unchecked")
	public static String positionJSON(Mutation m, int amount)
			throws SQLException, IOException {
		HashMap<String, ArrayList<Integer>> map = mutationService.getPositions(
				m, amount);

		JSONArray positionsJSON = new JSONArray();

		for (String name : map.keySet()) {
			ArrayList<String> diseases = GeneDiseaseLinkReader
					.findGeneDiseaseAssociation(name);
			String disease = diseases.toString().substring(1, diseases.toString().length() - 1);
			System.out.println(disease);
			
			JSONObject positionJSON = new JSONObject();
			positionJSON.put("name", name);
			positionJSON.put("start", map.get(name).get(0));
			positionJSON.put("end", map.get(name).get(1));
			positionJSON.put("annotation", proteinService.getAnnotations(name));
			positionJSON.put("disease", disease);
			positionsJSON.add(positionJSON);
		}

		return positionsJSON.toString();
	}

	/**
	 * Search for nearby mutations and make a JSON string out of it.
	 * 
	 * @param m
	 *            The mutation which location is searched in
	 * @param amount
	 *            The maximum amount of nearby mutations
	 * @param pid
	 *            The id of the patient
	 * @return JSON string representation of a List<Mutation>
	 * @throws SQLException
	 *             SQL Exception
	 */
	@SuppressWarnings("unchecked")
	public static String nearbyMutationsJSON(Mutation m, int amount, int pid)
			throws SQLException {
		List<Mutation> list = mutationService
				.getNearbyMutations(m, amount, pid);

		JSONArray nearbyMutationsJSON = new JSONArray();
		for (Mutation mutation : list) {
			JSONObject nearbyMutationJSON = new JSONObject();
			nearbyMutationJSON.put("rsid", mutation.getRsID());
			nearbyMutationJSON.put("sort", mutation.getMutationType());
			nearbyMutationJSON.put("position", mutation.getPositionGRCH37());
			nearbyMutationJSON.put("mid", mutation.getId());
			nearbyMutationJSON.put("pid", pid);
			nearbyMutationsJSON.add(nearbyMutationJSON);
		}
		return nearbyMutationsJSON.toString();
	}

}
