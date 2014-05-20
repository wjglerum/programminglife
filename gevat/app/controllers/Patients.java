package controllers;

import java.sql.SQLException;

import models.Patient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patients;

public class Patients extends Controller {

	/**
	 * Display a patient.
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(int id) throws SQLException {
		Patient p = Patient.get(id);

		// Return to the patients overview and display a message the requested
		// patient isn't found
		if (p == null) {
			flash("patient-not-found",
					"The requested patient could not be found. Please select another one below.");

			return notFound(patients.render(Patient.getAll(),
					Application.getUser()));
		}

		// Render the patient otherwise
		return ok(patient.render(p, Application.getUser()));
	}

	/**
	 * List all patients.
	 * 
	 * @throws SQLException
	 */
	@Security.Authenticated(Secured.class)
	public static Result showAll() throws SQLException {
		return ok(patients.render(Patient.getAll(), Application.getUser()));
	}

}
