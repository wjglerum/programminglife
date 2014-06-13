package controllers;

import static play.data.Form.form;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import models.database.Database;
import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;
import models.patient.PatientRepositoryDB;
import models.patient.PatientService;
import models.reader.ReaderThread;
import models.reader.VCFReader;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patient_add;
import views.html.patients;

public class Patients extends Controller {

	private static PatientRepositoryDB patientRepository = new PatientRepositoryDB();
	private static PatientService patientService = new PatientService(
			patientRepository);
	private static MutationRepositoryDB mutationRepository = new MutationRepositoryDB();
	private static MutationService mutationService = new MutationService(
			mutationRepository);

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
		List<Mutation> mutations = mutationService.getMutations(p_id);
		HashMap<Mutation, Double> map =  new HashMap<Mutation, Double>();
		for(Mutation m : mutations){
			map.put(m, (double) mutationService.getScore(m));
		}
		if (p == null)
			return patientNotFound();

		// Render the patient otherwise
		return ok(patient.render(p, map, Authentication.getUser()));
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
		public String sex;
		

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
						 * ^^^^^^^^^^^
						 *  { || || }
						 *      X 
						 *  \_______/
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
			String sex = addForm.get().sex;
			boolean female;
			if (sex == "female") {
				female = true;
			} else {
				female = false;
			}

			// Get VCF file data
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart vcf = body.getFile("vcf");
			File file = vcf.getFile();

			Long fileSize = file.length();
			String fileName = vcf.getFilename();
			String filePath = file.getAbsolutePath();

			// Add Patient to database
			Patient p = patientService.add(Authentication.getUser().id, name,
					surname, fileName, fileSize, female);
			
			// Setup a thread for processing the VCF
			ReaderThread readerThread = new ReaderThread(p, filePath);
			
			// Let the thread process the file in the background
			readerThread.start();

			// Make user happy
			flash("patient-added", "The patient " + name + " " + surname
					+ " is successfully added to the database. The VCF file is now being processed. Please wait...");

			return redirect(routes.Patients.showAll());
		}
	}
	
  /**
   * Handle the ajax request for removing patients
   * 
   * @throws SQLException
   */
  public static Result remove(int p_id) throws SQLException {
    Patient p = patientService.get(p_id, Authentication.getUser().id);

    if (p == null || !p.isProcessed())
      return badRequest();

    patientService.remove(p);

    return ok();
  }

  /**
   * Handle the ajax request for removing patients
   * 
   * @throws SQLException
   */
  public static Result isProcessed(int p_id) throws SQLException {
    Patient p = patientService.get(p_id, Authentication.getUser().id);

    if (p == null)
      return badRequest();

    if (!p.isProcessed())
      return ok("0");
    else {
      String row = "";
      
      row += "<td>" + p.getId() + "</td>";
      row += "<td>" + p.getName() + "</td>";
      row += "<td>" + p.getSurname() + "</td>";
      row += "<td>" + p.getVcfFile() + "</td>";
      row += "<td>" + p.getVcfLengthMB() + " MB</td>";
      row += "<td class=\"remove\"><a class=\"remove-patient center-block text-danger\"><span class=\"glyphicon glyphicon-remove\"></span></a></td>";
      
      return ok(row);
    }
  }

}
