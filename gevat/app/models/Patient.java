package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

public class Patient {

	public int id;
	public String name;
	public String surname;

	public Patient(int id, String name, String surname) {
		this.id = id;
		this.name = name;
		this.surname = surname;
	}

	/*
	 * Lookup patient in the database by p_id
	 */
	public static Patient get(int id) {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT * FROM patient WHERE p_id=" + id + ";";
			ResultSet rs = con.createStatement().executeQuery(query);

			if (rs.next()) {
				id = rs.getInt("p_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				return new Patient(id, name, surname);
			}
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}

	/*
	 * Make a list of all the patients in the database
	 */
	public static List<Patient> getAll() {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT * FROM patient;";
			ResultSet rs = con.createStatement().executeQuery(query);
			List<Patient> patients = new ArrayList<Patient>();

			while (rs.next()) {
				int id = rs.getInt("p_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				patients.add(new Patient(id, name, surname));
			}
			return patients;
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}

	/*
	 * Add users to the database, id's will be auto incremented
	 */
	public static void add(String name, String surname) {
		try (Connection con = DB.getConnection("data");) {
			String query = "INSERT INTO patient VALUES (nextval('p_id_seq'::regclass), 1 ,'"
					+ name + "', '" + surname + "');";
			Statement st = con.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
	}
}
