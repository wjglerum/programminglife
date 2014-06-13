package models.patient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.database.Database;

/**
 * Class for DB access for patients.
 * @author willem
 *
 */
public class PatientRepositoryDB implements PatientRepository {

	/**
	 * Lookup patient in the database by p_id.
	 * 
	 * @param pId
	 *            The patient-id
	 * @param uId
	 *            The user-id
	 * 
	 * @return Patient Returns the patient
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@Override
	public Patient get(final int pId, final int uId) throws SQLException {
		String query = "SELECT * FROM patient WHERE p_id=" + pId + " AND u_id="
				+ uId + ";";
		ResultSet rs = Database.select("data", query);

		if (rs.next()) {
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String vcfFile = rs.getString("vcf_file");
			Long vcfLength = rs.getLong("vcf_length");
			boolean processed = rs.getBoolean("processed");
			
			return new Patient(pId, name, surname, vcfFile, vcfLength, processed);
		}
		return null;
	}
	
	/**
	 * Make a list of all the patients in the database.
	 * 
	 * @param uId
	 *            The user-id
	 * 
	 * @return List<Patient> A list of all the patients
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@Override
	public List<Patient> getAll(final int uId) throws SQLException {
		String query = "SELECT * FROM patient WHERE u_id = '" + uId + "';";
		ResultSet rs = Database.select("data", query);

		List<Patient> patients = new ArrayList<Patient>();

		while (rs.next()) {
			int id = rs.getInt("p_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String vcfFile = rs.getString("vcf_file");
			Long vcfLength = rs.getLong("vcf_length");
      boolean processed = rs.getBoolean("processed");
      
			patients.add(new Patient(id, name, surname, vcfFile, vcfLength, processed));
		}
		return patients;
	}

	/**
	 * Add patient to the database, id's will be auto incremented.
	 * 
	 * @param uId
	 *            The user-id
	 * @param name
	 *            The name
	 * @param surname
	 *            The surname
	 * @param vcfFile
	 *            The file
	 * @param vcfLength
	 *            The length of the VCF-file
	 * 
	 * @return Return the patient
	 * 
	 * @throws SQLException
	 *             In case the SQL goes wrong
	 */
	@Override
	public Patient add(final int uId, final String name, final String surname,
			final String vcfFile, final Long vcfLength, boolean female) throws SQLException {
		String query = "INSERT INTO patient VALUES"
				+ " (nextval('p_id_seq'::regclass)," + uId + ",'" + name
				+ "', '" + surname + "', '" + vcfFile + "', " + vcfLength
				+ "," + false + "," + female + ");";
		Database.insert("data", query);

		// TODO get id of added patient efficiently
		System.out.println(query);
		List<Patient> patients = getAll(uId);
		
		return patients.get(patients.size() - 1);
	}

	/**
	 * Remove patient and linked mutations from database.
	 * 
	 * @param patient
	 *            The patient to be deleted
	 */
	@Override
	public void remove(final Patient patient) {
		String queryDeletePatient = "DELETE FROM patient WHERE p_id = "
				+ patient.getId();
		String queryDeleteMutations = "DELETE FROM mutations"
				+ " WHERE p_id = " + patient.getId();
		Database.delete("data", queryDeleteMutations);
		Database.delete("data", queryDeletePatient);
	}

}
