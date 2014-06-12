package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;

import models.mutation.Mutation;
import models.patient.Patient;

/**
 * Service for protein.
 * @author willem
 *
 */
public class ProteinService {

	private final ProteinRepository proteinRepository;

	public ProteinService(final ProteinRepository proteinRepository) {
		this.proteinRepository = proteinRepository;
	}

	public String getAnnotations(String name) throws SQLException {
		return proteinRepository.getAnnotations(name);
	}

	public void addConnectionsOfSnp(ProteinGraph pg, int snp, int limit,
			int threshold) throws SQLException {
		proteinRepository.addConnectionsOfSnp(pg, snp, limit, threshold);
	}

	public void addConnectionsOfProteine(ProteinGraph pg, String proteine)
			throws SQLException {
		proteinRepository.addConnectionsOfProteine(pg, proteine);
	}
	
	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m) throws SQLException {
		return proteinRepository.getRelatedMutations(p, m);
	}

}