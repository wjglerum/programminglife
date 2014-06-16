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
	 * Gets the positions on a gene.
	 * @param m
	 * @return Returns the list of positions
	 * @throws SQLException
	 */
	public HashMap<String, ArrayList<Integer>> getPositions(Mutation m) throws SQLException {
		return this.mutationRepository.getPositions(m);
	}
}
