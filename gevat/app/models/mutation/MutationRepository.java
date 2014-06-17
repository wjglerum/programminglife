package models.mutation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

	HashMap<String, ArrayList<Integer>> getPositions(Mutation m, int amount)
			throws SQLException;

	/**
	 * Gets nearby mutations
	 * @param m
	 * @return list of nearby mutations
	 */
	ArrayList<Mutation> getNearbyMutations(Mutation m, int amount, int pid)
			throws SQLException;
	
	/**
	 * Prepares all the queries.
	 * 
	 * @param database The name of the database that we call to.
	 * @throws IOException If the constructor isn't able to find the sql file.
	 */
//    void prepareQueries(String database) throws IOException;
}
