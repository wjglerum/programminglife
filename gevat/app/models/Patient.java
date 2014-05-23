package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Patient {

	public int id;
	public String name;
	public String surname;

	/**
	 * Basic Patient information
	 * 
	 * @param id
	 * @param name
	 * @param surname
	 */
	public Patient(int id, String name, String surname) {
		this.id = id;
		this.name = name;
		this.surname = surname;
	}

	/**
	 * Lookup patient in the database by p_id
	 * 
	 * @param p_id
	 * @param u_id
	 * @return Patient
	 * @throws SQLException 
	 */
	public static Patient get(int p_id, int u_id) throws SQLException {
		String query = "SELECT * FROM patient WHERE p_id=" + p_id
				+ " AND u_id=" + u_id + ";";
		ResultSet rs = Database.select("data", query);

		if (rs.next()) {
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			return new Patient(p_id, name, surname);
		}
		return null;
	}

	/**
	 * Make a list of all the patients in the database
	 * 
	 * @param u_id
	 * @return List<Patient>
	 * @throws SQLException
	 */
	public static List<Patient> getAll(int u_id) throws SQLException {
		String query = "SELECT * FROM patient WHERE u_id = '" + u_id + "';";
		ResultSet rs = Database.select("data", query);

		List<Patient> patients = new ArrayList<Patient>();

		while (rs.next()) {
			int id = rs.getInt("p_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			patients.add(new Patient(id, name, surname));
		}
		return patients;
	}

	/**
	 * Add patient to the database, id's will be auto incremented
	 * 
	 * @param u_id
	 * @param name
	 * @param surname
	 */
	public static void add(int u_id, String name, String surname) {
		String query = "INSERT INTO patient VALUES (nextval('p_id_seq'::regclass),"
				+ u_id + ",'" + name + "', '" + surname + "');";
		Database.insert("data", query);
	}

	/**
	 * Remove patient and linked mutations from database
	 * 
	 * @param patient
	 */
	public static void remove(Patient patient) {
		String queryDeletePatient = "DELETE FROM patient WHERE p_id = "
				+ patient.id;
		String queryDeleteMutations = "DELETE FROM mutations WHERE p_id = "
				+ patient.id;

		Database.delete("data", queryDeletePatient);
		Database.delete("data", queryDeleteMutations);
	}
}
