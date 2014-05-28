package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wjglerum
 *
 */
public class Patient {

	/**
	 * @param id the id of the patient
	 */
	private int id;
	/**
	 * @param name The name of the patient
	 */
	private String name;
	/**
	 * @param surname The surname of the patient
	 */
	private String surname;
	/**
	 * @param vcfFile The vcfFile of the patient
	 */
	private String vcfFile;
	/**
	 * @param vcfLength The length of the vcf-File
	 */
	private Long vcfLength;

	/**
	 * Basic Patient information.
	 *
	 * @param id The patient-id
	 * @param name The name of the patient
	 * @param surname The surname of the patient
	 * @param vcfFile The VCF-file of the patient
	 * @param vcfLength The length of the VCF-file
	 */
	public Patient(final int id, final String name, final String surname,
			final String vcfFile,
			final Long vcfLength) {
		this.id = id;
		this.setName(name);
		this.setSurname(surname);
		this.setVcfFile(vcfFile);
		this.vcfLength = vcfLength;
	}

	/**
	 * Lookup patient in the database by p_id.
	 *
	 * @param pId The patient-id
	 * @param uId The user-id
	 *
	 * @return Patient Returns the patient
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	public static Patient get(final int pId,
			final int uId) throws SQLException {
		String query = "SELECT * FROM patient WHERE p_id=" + pId
				+ " AND u_id=" + uId + ";";
		ResultSet rs = Database.select("data", query);

		if (rs.next()) {
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String vcfFile = rs.getString("vcf_file");
			Long vcfLength = rs.getLong("vcf_length");
			return new Patient(pId, name, surname,
					vcfFile, vcfLength);
		}
		return null;
	}

	/**
	 * Make a list of all the patients in the database.
	 *
	 * @param uId The user-id
	 *
	 * @return List<Patient> A list of all the patients
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	public static List<Patient> getAll(final int uId) throws SQLException {
		String query = "SELECT * FROM patient WHERE u_id = '"
				+ uId + "';";
		ResultSet rs = Database.select("data", query);

		List<Patient> patients = new ArrayList<Patient>();

		while (rs.next()) {
			int id = rs.getInt("p_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String vcfFile = rs.getString("vcf_file");
			Long vcfLength = rs.getLong("vcf_length");
			patients.add(new Patient(id, name, surname, vcfFile,
					vcfLength));
		}
		return patients;
	}

	/**
	 * Add patient to the database, id's will be auto incremented.
	 *
	 * @param uId The user-id
	 * @param name The name
	 * @param surname The surname
	 * @param vcfFile The file
	 * @param vcfLength The length of the VCF-file
	 *
	 * @return Return the patient
	 *
	 * @throws SQLException In case the SQL goes wrong
	 */
	public static Patient add(final int uId, final String name,
			final String surname, final String vcfFile,
			final Long vcfLength) throws SQLException {
		String query = "INSERT INTO patient VALUES"
				+ " (nextval('p_id_seq'::regclass),"
				+ uId + ",'" + name + "', '" + surname + "', '"
				+ vcfFile + "', " + vcfLength + ");";
		Database.insert("data", query);

		// TODO get id of added patient efficiently

		List<Patient> patients = getAll(uId);

		return patients.get(patients.size() - 1);
	}

	/**
	 * Remove patient and linked mutations from database.
	 *
	 * @param patient The patient to be deleted
	 */
	public static void remove(final Patient patient) {
		String queryDeletePatient = "DELETE FROM patient WHERE p_id = "
				+ patient.id;
		String queryDeleteMutations = "DELETE FROM mutations"
				+ " WHERE p_id = "
				+ patient.id;

		Database.delete("data", queryDeletePatient);
		Database.delete("data", queryDeleteMutations);
	}

	/**
	 * Gets the length in MB's.
	 *
	 * @return The length of the VCF-file
	 */
	public final Double getVcfLengthMB() {
		return ((double) (Math.round(vcfLength.doubleValue()
				/1024/1024 * 10))) / 10;
	}

	/**
	 * Gets the name.
	 *
	 * @return Returns the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name The name of the patient
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the VCF-file.
	 *
	 * @return Returns the VCF-file
	 */
	public final String getVcfFile() {
		return vcfFile;
	}

	/**
	 * Sets the VCF-file.
	 *
	 * @param vcfFile The file
	 */
	public final void setVcfFile(final String vcfFile) {
		this.vcfFile = vcfFile;
	}

	/**
	 * Gets the surname.
	 *
	 * @return Returns the surname
	 */
	public final String getSurname() {
		return surname;
	}

	/**
	 * Sets the surname.
	 *
	 * @param surname The surname
	 */
	public final void setSurname(final String surname) {
		this.surname = surname;
	}

	/**
	 * Gets the id.
	 *
	 * @return returns the Id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id The id of the patient
	 */
	public final void setId(int id) {
		this.id = id;
	}
}