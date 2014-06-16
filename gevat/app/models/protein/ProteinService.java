package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;

import models.mutation.Mutation;
import models.patient.Patient;

/**
 * Service for protein.
 * 
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

	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m)
			throws SQLException {
		return proteinRepository.getRelatedMutations(p, m);
	}
}
