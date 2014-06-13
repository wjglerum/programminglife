package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import play.Logger;
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

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine
	 * and it's possible connected proteines to ProteineGraph
	 * 
	 * @param snp
	 *            the rsid of the location
	 * @throws SQLException
	 */
	@Override
	public void addConnectionsOfSnp(int snp, int limit, int threshold)
			throws SQLException {
		ArrayList<String> qResult = QueryProcessor
				.findGenesAssociatedWithSNP(snp);
		if (!qResult.isEmpty()) {
			String protein = qResult.get(0);
			addConnectionsOfProteine(protein, limit, threshold);
		}
	}

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine
	 * and it's possible connected proteines to ProteineGraph
	 * 
	 * @param snp
	 *            the rsid of the location
	 * @param distance
	 *            the maximum amount of connections of an protein added to the
	 *            graph
	 * @throws SQLException
	 */
	@Override
	public void addDistantConnectionsOfSnp(int snp, int limit, int threshold,
			int distance) throws SQLException {
		ArrayList<String> qResult = QueryProcessor
				.findGenesAssociatedWithSNP(snp);
		if (!qResult.isEmpty()) {
			String protein = qResult.get(0);
			addDistantConnectionsOfProtein(protein, limit, threshold, distance);
		}
	}

	/**
	 * Adds the proteine and it's possible connected proteines to ProteineGraph
	 * 
	 * @param protein
	 * @param distance
	 *            the maximum amount of connections of an protein added to the
	 *            graph
	 * @throws SQLException
	 */
	@Override
	public void addDistantConnectionsOfProtein(String protein, int limit,
			int threshold, int distance) throws SQLException {
		Collection<Protein> currProteins = new HashSet<Protein>();
		currProteins.add(ProteinGraph.getProtein(protein));
		while (distance-- > 0) {
			Collection<Protein> newProteins = new HashSet<Protein>();
			for (Protein p : currProteins) {
				newProteins.addAll(addConnectionsOfProteine(p.getName(), limit,
						threshold));
			}
			currProteins = newProteins;
			Logger.info("ADCOP:\t" + distance + "\t" + currProteins.size());
		}
	}

	/**
	 * adds the proteine and it's possible connected proteines to ProteineGraph
	 * 
	 * @param proteine
	 *            A proteine to find neighbours of
	 * @return gives back the proteins that have been added to the graph
	 * @throws SQLException
	 */
	@Override
	public Collection<Protein> addConnectionsOfProteine(String protein,
			int limit, int threshold) throws SQLException {
		return addConnections(protein,
				QueryProcessor.findGeneConnections(protein, limit, threshold));
	}

	/**
	 * Add proteine p1 and it's connections with proteines in connections to
	 * ProteineGraph
	 * 
	 * @param p1
	 *            a proteine
	 * @param connections
	 *            a string in the format of
	 *            "[proteine1\tcombinedscore1,...proteineN\tcombinedscoreN]"
	 * @return gives back the proteins that have been added to the graph
	 */
	@Override
	public Collection<Protein> addConnections(String p1, String connections) {
		Collection<Protein> newProteins = new ArrayList<Protein>();
		if (connections.length() == 0)
			return newProteins;
		for (String s : connections.substring(1, connections.length() - 1)
				.split(",")) {
			String p2 = s.split("\t")[0].trim();
			if (!ProteinGraph.hasProtein(p2))
				newProteins.add(ProteinGraph.getProtein(p2));
			add(p1, p2, Integer.parseInt(s.split("\t")[1].trim()));
		}
		return newProteins;
	}

	/**
	 * Add a connection between p1 and p2 to ProteineGraph
	 * 
	 * @param p1
	 *            proteine one
	 * @param p2
	 *            proteine two
	 * @param combinedScore
	 *            the combined score between proteine one and two
	 */
	@Override
	public void add(String p1, String p2, int combinedScore) {
		ProteinConnection pc = new ProteinConnection(
				ProteinGraph.getProtein(p1), ProteinGraph.getProtein(p2),
				combinedScore);
		if (!ProteinGraph.connections.contains(pc)) {
			ProteinGraph.connections.add(pc);
		}
	}
}
