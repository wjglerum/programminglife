package models.patient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Patients
 * 
 * @author willem
 * 
 */
public interface PatientRepository {
	public Patient get(final int pId, final int uId) throws SQLException;

	public Patient add(final int uId, final String name, final String surname,
			final String vcfFile, final Long vcfLength, boolean female) throws SQLException;

	public List<Patient> getAll(final int uId) throws SQLException, IOException;

	public void remove(final Patient patient);
}
