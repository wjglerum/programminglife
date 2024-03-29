package models.mutation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides a service for mutation.
 */
public class MutationService {
	private final MutationRepository mutationRepository;

	/**
	 * The constructor.
	 *
	 * @param mutationRep The repository used
	 */
	public MutationService(final MutationRepository mutationRep) {
		this.mutationRepository = mutationRep;
	}

	/**
	 * Gets mutations.
	 *
	 * @param pId The id of the patient
	 *
	 * @return Returns a list of mutations
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	public final List<Mutation> getMutations(final int pId)
			throws SQLException {
		return this.mutationRepository.getMutations(pId);
	}

	/**
	 * Gets mutations of a chromosome.
	 *
	 * @param pId The id of the patient
	 *
	 * @param cId The id of the chromosome
	 *
	 * @return Returns a list of mutations
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	public final List<Mutation> getMutations(
			final int pId, final String cId)
			throws SQLException {
		return this.mutationRepository.getMutations(pId, cId);
	}

	/**
	 * Gets the score of a mutation.
	 *
	 * @param m The mutation
	 *
	 * @return Returns the float score
	 *
	 * @throws SQLException in case SQL goes wrong
	 */
	public final float getScore(final Mutation m) throws SQLException {
		return this.mutationRepository.getScore(m);
	}
	
	/**
	 * Gets the positions of nearby genes of a mutation.
	 * 
	 * @param m The mutation
	 * @param amount The amount of genes to find
	 * @return Returns the list of positions
	 * @throws SQLException SQL Exception
	 */
	public HashMap<String, ArrayList<Integer>> getPositions(Mutation m,
			int amount) throws SQLException {
		return this.mutationRepository.getPositions(m, amount);
	}
	
	/**
	 * Get nearby Mutations.
	 * @param m The mutation
	 * @param amount The amount of mutations to find
	 * @param pid The patient ID
	 * @return A list of nearby mutations
	 * @throws SQLException SQL Exception
	 */
	public ArrayList<Mutation> getNearbyMutations(Mutation m, int amount,
			int pid) throws SQLException {
		return this.mutationRepository.getNearbyMutations(m, amount, pid);
	}
}
