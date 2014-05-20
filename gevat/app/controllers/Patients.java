package controllers;

import static play.data.Form.form;

import java.util.List;

import models.Mutation;
import models.Patient;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patient_add;
import views.html.patients;

public class Patients extends Controller {

	/**
	 * Display a patient.
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int id) {
		Patient p = Patient.get(id);
		List<Mutation> mutations = Mutation.getMutations(id);

		// Return to the patients overview and display a message the requested
		// patient isn't found
		if (p == null) {
			flash("patient-not-found",
					"The requested patient could not be found. Please select another one below.");

			return notFound(patients.render(
					Patient.getAll(Authentication.getUser().id),
					Authentication.getUser()));
		}

		// Render the patient otherwise
		return ok(patient.render(p, mutations, Authentication.getUser()));
	}

	/**
	 * List all patients.
	 */
	@Security.Authenticated(Secured.class)
	public static Result showAll() {
		return ok(patients.render(Patient.getAll(Authentication.getUser().id),
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
			else
				return null;
		}

	}

	/**
	 * Render the form to add a patient.
	 */
	public static Result add() {
		return ok(patient_add.render(form(Add.class), Authentication.getUser()));
	}

	/**
	 * Insert the newly added patient in the database
	 */
	public static Result insert() {
		Form<Add> addForm = form(Add.class).bindFromRequest();

		if (addForm.hasErrors()) {
			return badRequest(patient_add.render(addForm,
					Authentication.getUser()));
		} else {
			String name = addForm.get().name;
			String surname = addForm.get().surname;
			Patient.add(Authentication.getUser().id, name, surname);

			flash("patient-added", "The patient " + name + " " + surname
					+ " is successfully added to the database.");

			return redirect(routes.Patients.showAll());
		}
	}

}
