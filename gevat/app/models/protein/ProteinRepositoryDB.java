package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.database.QueryProcessor;
import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;

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

	/**
	 * Gets the annotations of the protein.
	 * 
	 * @return Returns the annotations associated with the protein.
	 * @throws SQLException
	 */
	@Override
	public final String getAnnotations(String name) throws SQLException {
		return QueryProcessor.getAnnotationsOfProtein(name);
	}

	@Override
	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m)
			throws SQLException {
		List<Mutation> mutations = mutationService.getMutations(p.getId());
		ArrayList<Mutation> related = new ArrayList<Mutation>();

		// Find mutation related to this protein (should be faster)
		for (Mutation mutation : mutations) {
			if (mutation.getId() != m.getId()) {
				// TODO add related proteins found in mutation
			}
		}

		return related;
	}
}
