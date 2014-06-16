package models.patient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Service to interact with Patients.
 * 
 * @author willem
 * 
 */
public class PatientService {
	private final PatientRepository patientRepository;

	public PatientService(final PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	public Patient get(final int pId, final int uId) throws SQLException {
		return patientRepository.get(pId, uId);
	}

	public Patient add(final int uId, final String name, final String surname,
			final String vcfFile, final Long vcfLength, boolean female) throws SQLException {
		return patientRepository.add(uId, name, surname, vcfFile, vcfLength, female);
	}

	public List<Patient> getAll(final int uId) throws SQLException, IOException {
		return patientRepository.getAll(uId);
	}

	public void remove(final Patient patient) {
		patientRepository.remove(patient);
	}
}
