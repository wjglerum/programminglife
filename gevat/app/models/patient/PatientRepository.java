package models.patient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Patients.
 * 
 * @author willem
 * 
 */
public interface PatientRepository {
    /**
     * Lookup patient in the database by p_id.
     * 
     * @param pId
     *            The patient-id
     * @param uId
     *            The user-id
     * @return
     * 
     * @return Patient Returns the patient
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
	Patient get(final int pId, final int uId) throws SQLException;

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
     * @param female True when patient is female
     * 
     * @return Return the patient
     * 
     * @throws SQLException
     *             In case the SQL goes wrong
     */
	Patient add(final int uId, final String name, final String surname,
			final String vcfFile, final Long vcfLength, boolean female) throws SQLException;

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
     * @throws IOException IO Exception
     */
	List<Patient> getAll(final int uId) throws SQLException, IOException;

    /**
     * Remove patient and linked mutations from database.
     * 
     * @param patient
     *            The patient to be deleted
     * @throws SQLException SQL Exception
     */
	void remove(final Patient patient) throws SQLException;
}
