package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;
import models.patient.PatientRepositoryDB;
import models.patient.PatientService;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.chromosome;

/**
 * Renders the chromosome view.
 * 
 */
public class Chromosomes extends Controller {

	private static PatientRepositoryDB patientRepository = new PatientRepositoryDB();
	private static PatientService patientService = new PatientService(
			patientRepository);
	private static MutationRepositoryDB mutationRepository = new MutationRepositoryDB();
	private static MutationService mutationService = new MutationService(
			mutationRepository);
	
	private static final int round = 1000;

	/**
	 * Display all the mutations in a certain chromosome of a patient.
	 * 
	 * @param patientId
	 *            The ID of the patient
	 * @param chromosomeId
	 *            The ID of the chromosome
	 * @return action result
	 * @throws SQLException
	 *             exception
	 * @throws IOException
	 *             exception
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int patientId, String chromosomeId)
			throws SQLException, IOException {
		Patient p = patientService.get(patientId, Authentication.getUser()
				.getId());
		List<Mutation> mutations = mutationService.getMutations(patientId,
				chromosomeId);
		HashMap<Mutation, Double> map = new HashMap<Mutation, Double>();
		for (Mutation m : mutations) {
			map.put(m, (double) Math.round(((double) mutationService
					.getScore(m)) * round) / round);
		}

		models.chromosome.Chromosome c = new models.chromosome.Chromosome(
				chromosomeId);

		if (p == null) {
			return Patients.patientNotFound();
		}

		String mutationsJSON = mutationsJSON(mutations, patientId);

		// Render the chromosome view if there are mutations in the view
		return ok(chromosome.render(p, c, map, Authentication.getUser(),
				mutationsJSON));
	}

	/**
	 * Transform a list of mutations to JSON.
	 * 
	 * @param mutations
	 *            list of mutations
	 * @param pid
	 *            id op patient
	 * @return JSON
	 */
	@SuppressWarnings("unchecked")
	public static String mutationsJSON(List<Mutation> mutations, int pid) {
		JSONArray mutationsJSON = new JSONArray();

		for (Mutation mutation : mutations) {
			JSONObject mutationJSON = new JSONObject();
			mutationJSON.put("rsid", mutation.getRsID());
			mutationJSON.put("sort", mutation.getMutationType());
			mutationJSON.put("position", mutation.getPositionGRCH37());
			mutationJSON.put("mid", mutation.getId());
			mutationJSON.put("pid", pid);
			mutationsJSON.add(mutationJSON);
		}
		return mutationsJSON.toString();
	}
}
