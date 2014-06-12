package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;
import models.patient.PatientRepository;
import models.patient.PatientRepositoryDB;
import models.patient.PatientService;
import models.protein.Protein;
import models.protein.ProteinConnection;
import models.protein.ProteinGraph;
import models.user.UserRepositoryDB;
import models.user.UserService;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.chromosome;
import views.html.patient;
import views.html.patients;
import views.html.mutation;

public class Chromosomes extends Controller {

	private static PatientRepositoryDB patientRepository = new PatientRepositoryDB();
	private static PatientService patientService = new PatientService(
			patientRepository);
	private static MutationRepositoryDB mutationRepository = new MutationRepositoryDB();
	private static MutationService mutationService = new MutationService(
			mutationRepository);

	/**
	 * Display all the mutations in a certain chromosome of a patient.
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int p_id, int c_id) throws SQLException {
		Patient p = patientService.get(p_id, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(p_id, c_id);
		models.chromosome.Chromosome c = new models.chromosome.Chromosome(c_id);

		if (p == null)
			return Patients.patientNotFound();

		// Render the chromosome view if there are mutations in the view
		return ok(chromosome.render(p, c, mutations, Authentication.getUser()));
	}
}