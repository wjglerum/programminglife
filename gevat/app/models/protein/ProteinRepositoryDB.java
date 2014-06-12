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

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine
	 * and it's possible connected proteines to ProteineGraph
	 * 
	 * @param snp
	 *            the rsid of the location
	 * @throws SQLException
	 */
	@Override
	public void addConnectionsOfSnp(ProteinGraph pg, int snp, int limit,
			int threshold) throws SQLException {
		QueryProcessor.findGeneConnections(snp, limit, threshold, pg);
	}

	/**
	 * adds the proteine and it's possible connected proteines to ProteineGraph
	 * 
	 * @param proteine
	 *            A proteine to find neighbours of
	 * @throws SQLException
	 */
	@Override
	public void addConnectionsOfProteine(ProteinGraph pg, String proteine)
			throws SQLException {
		QueryProcessor.findGeneConnections(proteine, 10, 700, pg);
	}

	@Override
	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m) throws SQLException {
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
