package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.database.QueryProcessor;
import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;

/**
 * Repository for protein.
 * 
 * @author willem
 * 
 */
public class ProteinRepositoryDB implements ProteinRepository {

	private MutationRepositoryDB mutationRepository = new MutationRepositoryDB();
	private MutationService mutationService = new MutationService(
			mutationRepository);

	@Override
	public final String getAnnotations(String protein) throws SQLException {
		return QueryProcessor.getAnnotationsOfProtein(protein);
	}

	@Override
	public ArrayList<Mutation> getRelatedMutations(int patientId, Mutation m)
			throws SQLException {
		List<Mutation> mutations = mutationService.getMutations(patientId);
		ArrayList<Mutation> related = new ArrayList<Mutation>();

		// Find mutation related to this protein (should be faster)
		for (Mutation mutation : mutations) {
			if (mutation.getId() != m.getId()) {
				assert (true);
			}
		}

		return related;
	}
}
