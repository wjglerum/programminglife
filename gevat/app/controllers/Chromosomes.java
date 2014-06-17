package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

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

	private static PatientRepositoryDB patientRepository =
			new PatientRepositoryDB();
	private static PatientService patientService =
			new PatientService(patientRepository);
	private static MutationRepositoryDB mutationRepository =
			new MutationRepositoryDB();
	private static MutationService mutationService =
			new MutationService(mutationRepository);

	/**
	 * Display all the mutations in a certain chromosome of a patient.
	 * @param patientId The ID of the patient
	 * @param chromosomeId The ID of the chromosome
	 * @return action result
	 * @throws SQLException exception
	 * @throws IOException exception
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int patientId, String chromosomeId) throws SQLException, IOException {
		Patient p = patientService.get(patientId, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(patientId, chromosomeId);
		HashMap<Mutation, Double> map =  new HashMap<Mutation, Double>();
		for (Mutation m : mutations) {
			map.put(m, (double) mutationService.getScore(m));
		}

		models.chromosome.Chromosome c =
				new models.chromosome.Chromosome(chromosomeId);

		if (p == null) {
			return Patients.patientNotFound();
		}

		// Render the chromosome view if there are mutations in the view
		return ok(chromosome.render(p,
				c, map, Authentication.getUser()));
	}
}
