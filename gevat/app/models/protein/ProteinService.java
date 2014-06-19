package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;

import models.mutation.Mutation;

/**
 * Service for protein.
 * 
 * @author willem
 * 
 */
public class ProteinService {

	private final ProteinRepository proteinRepository;

	/**
	 * @param proteinRepository A proteinRepository
	 */
	public ProteinService(final ProteinRepository proteinRepository) {
		this.proteinRepository = proteinRepository;
	}

	/**
	 * Gets the annotations of the protein.
	 * 
	 * @param protein The name of the protein
	 * @return Returns the annotations associated with the protein.
	 * @throws SQLException SQL Exception
	 */
	public String getAnnotations(String protein) throws SQLException {
		return proteinRepository.getAnnotations(protein);
	}

	/**
	 * @param patientId The ID of the patient
	 * @param m The mutation
	 * @return A list of related mutations
	 * @throws SQLException SQL Exception
	 */
	public ArrayList<Mutation> getRelatedMutations(int patientId, Mutation m)
			throws SQLException {
		return proteinRepository.getRelatedMutations(patientId, m);
	}
}
