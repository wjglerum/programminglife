package controllers;

import static play.data.Form.form;

import java.sql.SQLException;
import java.io.File;
import java.util.List;

import org.broadinstitute.variant.variantcontext.Allele;

import models.database.Database;
import models.dna.Mutation;
import models.patient.Patient;
import models.patient.PatientRepository;
import models.patient.PatientRepositoryDB;
import models.patient.PatientService;
import models.protein.ProteinConnection;
import models.protein.ProteinGraph;
import models.reader.VCFReader;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.patient;
import views.html.patient_add;
import views.html.patients;

public class Patients extends Controller {

	private static PatientRepositoryDB patientRepository = new PatientRepositoryDB();
	private static PatientService patientService = new PatientService(
			patientRepository);

	/**
	 * List all patients.
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result showAll() throws SQLException {
		return ok(patients.render(
				patientService.getAll(Authentication.getUser().id),
				Authentication.getUser()));
	}

	/**
	 * Display a patient.
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int p_id) throws SQLException {
		Patient p = patientService.get(p_id, Authentication.getUser().id);
		List<Mutation> mutations = Mutation.getMutations(p_id);

		if (p == null)
			return patientNotFound();

		// Render the patient otherwise
		return ok(patient.render(p, mutations, Authentication.getUser()));
	}

	/**
	 * Return to the patients overview and display a message the requested
	 * patient isn't found
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result patientNotFound() throws SQLException {
		flash("patient-not-found",
				"The requested patient could not be found. Please select another one below.");

		return notFound(patients.render(
				patientService.getAll(Authentication.getUser().id),
				Authentication.getUser()));
	}

	/**
	 * Form class for adding new patients
	 */
	public static class Add {

		public String name;
		public String surname;

		public String validate() {
			if (name == null || name.length() < 3)
				return "An invalid name is entered. Please fill in a name consisting of at least 3 characters";
			else if (surname == null || surname.length() < 3)
				return "An invalid surname is entered. Please fill in a surname consisting of at least 3 characters";
			else {
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart vcf = body.getFile("vcf");

				if (vcf != null) {
					String fileName = vcf.getFilename();
					String contentType = vcf.getContentType();

					// Check file extension
					if (!checkFileExtension(fileName)) {
						/*
						 * ^^^^^^^^^^^ { || || } X \_______/
						 * 
						 * YEAH SIG WE FIXED THIS !!!
						 */
						return "The uploaded file hasn't the .vcf extension";
					}

					// Check content type
					if (!contentType.equals("text/directory")
							&& !contentType.equals("text/vcard"))
						return "File has wrong content type";
				} else {
					return "No VCF file provided";
				}
			}

			return null;
		}

		/*
		 * Whoohoo seperate method !!!
		 */
		private boolean checkFileExtension(String fileName) {
			if (fileName.length() < 4)
				return false;

			return fileName.substring(fileName.length() - 4).equals(".vcf");
		}

	}

	/**
	 * Render the form to add a patient.
	 * 
	 * @throws SQLException
	 */
	public static Result add() throws SQLException {
		return ok(patient_add.render(form(Add.class), Authentication.getUser()));
	}

	/**
	 * Insert the newly added patient in the database
	 * 
	 * @throws SQLException
	 */
	public static Result insert() throws SQLException {
		Form<Add> addForm = form(Add.class).bindFromRequest();

		if (addForm.hasErrors()) {
			return badRequest(patient_add.render(addForm,
					Authentication.getUser()));
		} else {
			String name = addForm.get().name;
			String surname = addForm.get().surname;

			// Get VCF file data
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart vcf = body.getFile("vcf");
			File file = vcf.getFile();

			Long fileSize = file.length();
			String fileName = vcf.getFilename();
			String filePath = file.getAbsolutePath();

			// Add Patient to database
			Patient p = patientService.add(Authentication.getUser().id, name,
					surname, fileName, fileSize);

			// Process VCF file
			List<Mutation> mutations = VCFReader.getMutations(filePath);

			// Add each mutation to the database
			for (Mutation m : mutations) {
				String query = "INSERT INTO mutations VALUES (nextval('m_id_seq'::regclass),"
						+ p.getId()
						+ ",'"
						+ m.getMutationType()
						+ "','"
						+ m.getRsID()
						+ "',"
						+ m.getChromosome()
						+ ",'"
						+ m.toAllelesString()
						+ "',"
						+ m.getStart()
						+ ","
						+ m.getEnd()
						+ ","
						+ m.getPositionGRCH37()
						+ ");";
				Logger.info(query);
				Database.insert("data", query);
			}
			
			findProteinConnections(mutations, p);

			// Make user happy
			flash("patient-added", "The patient " + name + " " + surname
					+ " is successfully added to the database.");

			return redirect(routes.Patients.showAll());
		}
	}
	
	public static void findProteinConnections(List<Mutation> mutations, Patient p)
	{
		Logger.info(p.getName() + " " + p.getSurname() + ": " + p.getId());
		ProteinGraph pg = new ProteinGraph();
		for(Mutation m: mutations)
		{
			pg.addConnectionsOfSnp(Integer.parseInt(m.getRsID().substring(2)), 10, 300);
		}
		for(ProteinConnection pc : pg.getConnections())
		{
			pc.insertIntoDB(p.getId());
		}
	}

	/**
	 * Handle the ajax request for removing patients
	 * 
	 * @throws SQLException
	 */
	public static Result remove(int p_id) throws SQLException {
		Patient p = patientService.get(p_id, Authentication.getUser().id);

		if (p == null)
			return badRequest();

		patientService.remove(p);

		return ok();
	}

}
