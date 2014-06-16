package models.mutation;

import java.sql.SQLException;
import java.util.List;

/**
 * Provides an interface for the Mutation class.
 */
public interface MutationRepository {

	/**
	 * Gets mutations.
	 *
	 * @param pId The id of the patient
	 *
	 * @return Returns a list of mutations
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	List<Mutation> getMutations(final int pId) throws SQLException;

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
	List<Mutation> getMutations(final int pId, final String cId)
			throws SQLException;

	/**
	 * Gets the score of a mutation.
	 *
	 * @param m The mutation
	 *
	 * @return Returns the float score
	 *
	 * @throws SQLException in case SQL goes wrong
	 */
	float getScore(Mutation m) throws SQLException;
}
