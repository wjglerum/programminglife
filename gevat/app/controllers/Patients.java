package controllers;

import static play.data.Form.form;

import java.io.File;
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
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patient_add;
import views.html.patients;

/**
 * Provides the patient view.
 */
public class Patients extends Controller {

	private static PatientRepositoryDB patientRepository =
			new PatientRepositoryDB();
	private static PatientService patientService =
			new PatientService(
			patientRepository);
	private static MutationRepositoryDB mutationRepository =
			new MutationRepositoryDB();
	private static MutationService mutationService =
			new MutationService(
			mutationRepository);

	/**
	 * List all patients.
	 *
	 * @return action result
	 * @throws SQLException SQL Exception
	 * @throws IOException IO Exception
	 */
	@Security.Authenticated(Secured.class)
	public static Result showAll() throws SQLException, IOException {
		return ok(patients.render(
				patientService.getAll(Authentication.
						getUser().id),
				Authentication.getUser()));
	}

	/**
	 * Display a patient.
	 *
	 * @param patientId The id of the patient
	 *
	 * @return action result
	 *
	 * @throws SQLException SQL Exception
	 * @throws IOException IO Exception
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int patientId) throws SQLException, IOException {
		Patient p = patientService.get(patientId, Authentication.getUser().id);
		List<Mutation> mutations = mutationService.getMutations(patientId);
		HashMap<Mutation, Double> map = new HashMap<Mutation, Double>();
		for (Mutation m : mutations) {
			map.put(m, (double) mutationService.getScore(m));
		}
		if (p == null) {
			return patientNotFound();
		}

		// Render the patient otherwise
		return ok(patient.render(p, map, Authentication.getUser()));
	}

	/**
	 * Return to the patients overview and display a message the requested
	 * patient isn't found.
	 * 
	 * @return action result
	 *
	 * @throws SQLException SQL Exception
	 * @throws IOException IO Exception
	 */
	@Security.Authenticated(Secured.class)
	public static Result patientNotFound() throws SQLException, IOException {
		flash("patient-not-found",
				"The requested patient could not be found. "
				+ "Please select another one below.");

		return notFound(patients.render(
				patientService.getAll(
						Authentication.getUser().id),
						Authentication.getUser()));
	}

	/**
	 * Form class for adding new patients.
	 */
	public static class Add {

		private String name;
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSurname() {
			return surname;
		}

		public void setSurname(String surname) {
			this.surname = surname;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		private String surname;
		private String sex;

		/**
		 * Checks if the provided details are allowed.
		 *
		 * @return Returns a String with the result message
		 */
		public final String validate() {
			String checkName = checkName();
			if (checkName == null) {
				return checkVCF();
			} else {
				return checkName;
			}
		}

		private String checkName() {
			final int minimumLength = 3;
			if (name == null || name.length() < minimumLength) {
				return Errors.errorType.INVALID_NAME.getMessage();
			}
			else if (surname == null
					|| surname.length() < minimumLength) {
				return Errors.errorType.INVALID_SURNAME.getMessage();
			}
			return null;
		}

		private String checkVCF() {
			MultipartFormData body =
					request().body().asMultipartFormData();
			FilePart vcf = body.getFile("vcf");

			if (vcf != null) {
				String fileName = vcf.getFilename();
				String contentType =
						vcf.getContentType();

				if (!checkFileExtension(fileName)) {
					return Errors.errorType.INVALID_EXTENSION.getMessage();
				}

				// Check content type
				if (!contentType.equals("text/"
						+ "directory")
						&& !contentType.equals(
								"text/vcard")) {
					return Errors.errorType.WRONG_CONTENT_TYPE.getMessage();
				}
			} else {
				return Errors.errorType.NO_VCF.getMessage();
			}
			return null;
		}

		/**
		 * Check if the file extension is .vcf.
		 * @param fileName The name of the file
		 *
		 * @return Returns wether or not the extension is correct
		 */
		private boolean checkFileExtension(final String fileName) {
			final int minimumLength = 4;
			if (fileName.length() < minimumLength) {
				return false;
			}

			return fileName.substring(fileName.length()
					- minimumLength)
					.equals(".vcf");
		}

	}

	/**
	 * Render the form to add a patient.
	 *
	 * @return action result
	 * @throws SQLException SQL Exception
	 */
	public static Result add() throws SQLException {
		return ok(patient_add.render(form(Add.class),
				Authentication.getUser()));
	}

	/**
	 * Insert the newly added patient in the database.
	 *
	 * @return action result
	 * @throws SQLException SQL Exception
	 */
	public static Result insert() throws SQLException {
		Form<Add> addForm = form(Add.class).bindFromRequest();

		if (addForm.hasErrors()) {
			return badRequest(patient_add.render(addForm,
					Authentication.getUser()));
		} else {
			Patient p = importPatientToDatabase(addForm);

			// Make user happy
			flash("patient-added",
					"The patient "
							+ p.getName()
							+ " "
							+ p.getSurname()
							+ " is successfully added to the database."
							+ "The VCF file is now being processed."
							+ "Please wait...");

			return redirect(routes.Patients.showAll());
		}
	}

	private static Patient importPatientToDatabase(Form<Add> addForm)
			throws SQLException {
		// Get VCF file data
		MultipartFormData body =
				request().body().asMultipartFormData();
		FilePart vcf = body.getFile("vcf");
		File file = vcf.getFile();

		Long fileSize = file.length();
		String fileName = vcf.getFilename();
		String filePath = file.getAbsolutePath();

		// Add Patient to database
		Patient p = makePatient(addForm, fileSize, fileName);

		p.setupReaderThread(filePath);
		return p;
	}

	private static Patient makePatient(Form<Add> addForm,
		Long fileSize, String fileName) throws SQLException {
		String name = addForm.get().name;
		String surname = addForm.get().surname;
		String sex = addForm.get().sex;
		boolean female = true;
		if (!sex.equals("female")) {
			female = false;
		}
		
		Patient p = patientService.add(
				Authentication.getUser().id, name,
				surname, fileName, fileSize, female);
		return p;
	}

	/**
	 * Handle the ajax request for removing patients.
	 *
	 * @param pId The id of the patient
	 * @return action result
	 * @throws SQLException In case SQL goes wrong
	 */
	public static Result remove(final int pId) throws SQLException {
		Patient p = patientService.get(pId,
				Authentication.getUser().id);

		if (p == null || !p.isProcessed()) {
			return badRequest();
		}
		patientService.remove(p);

		return ok();
	}

	/**
	 * Handle the ajax request for removing patients.
	 *
	 * @param pId the id of the patient
	 * @return action result
	 * @throws SQLException In case SQL goes wrong
	 */
	public static Result isProcessed(int pId) throws SQLException {
		Patient p = patientService.get(pId, Authentication.getUser().id);

		if (p == null) {
			return badRequest();
		}

		if (!p.isProcessed()) {
			return ok("0");
		}
		else {
			String row = "";

			row += "<td>" + p.getId() + "</td>";
			row += "<td>" + p.getName() + "</td>";
			row += "<td>" + p.getSurname() + "</td>";
			row += "<td>" + p.getVcfFile() + "</td>";
			row += "<td>" + p.getVcfLengthMB() + " MB</td>";
			row += "<td class=\"remove\">"
					+ "<a class=\"remove-patient center-block text-danger\">"
					+ "<span class=\"glyphicon glyphicon-remove\">"
					+ "</span></a></td>";

			return ok(row);
		}
	}
	
	/**
	 * A container for error messages.
	 */
	private static class Errors {
		/**
		 * Enumeration with error messages.
		 */
		private enum errorType {
		  INVALID_NAME("An invalid name is entered. "
					+ "Please fill in "
					+ "a name consisting of at "
					+ "least 3 characters"),
		  INVALID_SURNAME("An invalid surname is entered. "
					+ "Please fill in a "
					+ "surname consisting of at "
					+ "least 3 characters"),
		  INVALID_EXTENSION("The uploaded "
					+ "file doesn't"
					+ " have the "
					+ ".vcf "
					+ "extension"),
		  WRONG_CONTENT_TYPE("File has wrong "
								+ "content "
								+ "type"),
		  NO_VCF("No VCF file provided");

		  private final String message;

		  errorType(String message) {
		     this.message = message;
		  }

		  public String getMessage() { return message; }
		}
	}
}
