package models.protein;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import models.database.QueryProcessor;
import models.reader.GeneDiseaseLinkReader;
import play.Logger;

/**
 *
 * @author rhvanstaveren
 *
 */
public class ProteinGraph {
	/**
	 * Maps the name of proteins to the protein object with that name.
	 */
	private Map<String, Protein> proteines = new HashMap<String, Protein>();
	private Collection<ProteinConnection> connections = new ArrayList<ProteinConnection>();
	private static QueryProcessor qp = new QueryProcessor();
	
	/**
	 * Creates an empty ProteineGraph.
	 */
	public ProteinGraph() {
	    qp = new QueryProcessor();
	}

	/**
	 * Creates and ProteineGraph using the proteine the location of the snp.
	 *
	 * @param snp
	 *            the rsid of the location
	 * @param limit
	 *            the maximum amount of proteinConnection added per protein
	 * @param threshold
	 *            the minimum threshold for an added connection
	 * @throws IOException 
	 */
	public ProteinGraph(final int snp, final int limit,
			final int threshold) throws IOException {
		addConnectionsOfSnp(snp, limit, threshold);

		connectAllProteines();
	}

	/**
	 * Creates and ProteineGraph using the proteine the location of the snp.
	 *
	 * @param snp
	 *            the rsid of the location
	 * @param distance
	 *            defines the allowed distance to the protein on the snp
	 *            location
	 * @param limit
	 *            the maximum amount of proteinConnection added per protein
	 * @param threshold
	 *            the minimum threshold for an added connection
	 * @throws IOException 
	 */
	public ProteinGraph(final int snp, final int limit, final int threshold,
			final int distance) throws IOException {
		addDistantConnectionsOfSnp(snp, limit, threshold, distance);
		connectAllProteines();
	}

	/**
	 * Looks up the proteine at the location of the snp.
	 * adds this proteine
	 * and it's possible connected proteines to ProteineGraph.
	 *
	 * @param snp
	 *            the rsid of the location
	 * @param limit The maximum amount of connections to be added per query
	 * @param threshold The minimum threshold of connections
	 * @throws IOException IO Exception
	 */
	public void addConnectionsOfSnp(int snp, int limit, int threshold) throws IOException {
		try {
			for (String protein : QueryProcessor.findGenesAssociatedWithSNP(snp)) {
				addConnectionsOfProteine(protein, limit, threshold);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * Looks up the proteine at the location of the snp.
	 * Adds this proteine and it's possible connected proteines
	 * to ProteineGraph
	 *
	 * @param snp
	 *            the rsid of the location
	 * @param distance
	 *            the maximum amount of connections of an protein added to the
	 *            graph
	 * @param limit The maximum amount of connections to be added per query
	 * @param threshold The minimum threshold of connections
	 * @throws IOException IO Exception
	 */
	public void addDistantConnectionsOfSnp(int snp, int limit, int threshold,
			int distance) throws IOException {
		try {
			for (String protein : QueryProcessor.findGenesAssociatedWithSNP(snp)) {
				addDistantConnectionsOfProtein(protein, limit, threshold,
						distance);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * Adds the proteine and it's possible connected proteines to
	 * ProteineGraph.
	 *
	 * @param protein
	 *            A proteine to find neighbours of
	 * @param distance
	 *            the maximum amount of connections of an protein added to the
	 *            graph
	 * @param limit The maximum amount of connections to be added per query
	 * @param threshold The minimum threshold of connections
	 * @throws IOException IO Exception
	 */
	public void addDistantConnectionsOfProtein(String protein, int limit,
			int threshold, int distance) throws IOException {
		Collection<Protein> currProteins = new HashSet<Protein>();
		currProteins.add(getProtein(protein));
		for (int d = 0; d < distance; d++) {
			Collection<Protein> newProteins =
					new HashSet<Protein>();
			for (Protein p : currProteins) {
				newProteins.addAll(addConnectionsOfProteine(
						p.getName(), limit, threshold));
			}
			currProteins = newProteins;
			Logger.info("ADCOP:\t" + distance + "\t"
				+ currProteins.size());
		}
	}

	/**
	 * adds the proteine and it's possible connected proteines to
	 * ProteineGraph.
	 *
	 * @param protein
	 *            A proteine to find neighbours of
	 * @param limit
	 *            the maximum amount of proteinConnection added per protein
	 * @param threshold
	 *            the minimum threshold for an added connection
	 * @return gives back the proteins that have been added to the graph
	 * @throws IOException 
	 */
	public Collection<Protein> addConnectionsOfProteine(
			final String protein, final int limit,
			final int threshold) throws IOException {
		try {
			return addConnections(protein, QueryProcessor.
					findGeneConnections(protein, limit,
							threshold));
		} catch (SQLException e) {
			Logger.info(e.toString());
			return new ArrayList<Protein>();
		}
	}

	/**
	 * Add proteine p1 and it's connections with proteines in connections to
	 * ProteineGraph.
	 *
	 * @param p1
	 *            a proteine
	 * @param connectionsString
	 *            a string in the format of
	 *            "[proteine1\tcombinedscore1,...proteineN\tcombinedscoreN]"
	 * @return gives back the proteins that have been added to the graph
	 */
	private Collection<Protein> addConnections(final String p1,
			final String connectionsString) {
		Collection<Protein> newProteins = new ArrayList<Protein>();
		if (connectionsString.length() == 0) {
			return newProteins;
		}
		for (String s : connectionsString.substring(1,
				connectionsString.length() - 1)
				.split(",")) {
			if (!s.isEmpty()) {
				String p2 = s.split("\t")[0].trim();
				if (!hasProtein(p2)) {
					newProteins.add(getProtein(p2));
				}
				add(p1, p2, Integer.parseInt(s.split("\t")[1].
						trim()));
			}
		}
		return newProteins;
	}

	/**
	 * Add a connection between p1 and p2 to ProteineGraph.
	 *
	 * @param p1
	 *            proteine one
	 * @param p2
	 *            proteine two
	 * @param combinedScore
	 *            the combined score between proteine one and two
	 */
	private void add(final String p1, final String p2,
			final int combinedScore) {
		ProteinConnection pc = new ProteinConnection(getProtein(p1),
				getProtein(p2), combinedScore);
		if (!this.connections.contains(pc)) {
			this.connections.add(pc);
		}
	}

	/**
	 * returns the proteine with this name.
	 * If this proteine does not excist in ProteineGraph a new protein is
	 * made.
	 *
	 * @param name
	 *            the name of the protein to return
	 * @return the proteine with this name
	 */
	public Protein getProtein(final String name) {
		if (!hasProtein(name)) {
			try {
				proteines.put(name,
					new Protein(name, GeneDiseaseLinkReader
						.findGeneDiseaseAssociation(
								name)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return proteines.get(name);
	}

	/**
	 * Checks if the graph contains this protein.
	 * @param name
	 * 			the name of the protein to look for
	 * @return
	 * 			true if the graph contains a protein with this name
	 */
	public boolean hasProtein(final String name) {
		return proteines.containsKey(name);
	}

	/**
	 * Returns a string representation of ProteinGraph.
	 * @return
	 * 			a string representation of this ProteinGraph
	 */
	public String toString() {
		return "<ProteinGraph: " + proteines.toString() + ">";
	}

	public Collection<Protein> getProteines() {
		return proteines.values();
	}

	public Collection<ProteinConnection> getConnections() {
		return connections;
	}

	public Collection<String> getProteinesAsString() {
		return proteines.keySet();
	}

	/**
	 * Looks for connections between all proteins currently in the graph.
	 */
	private void connectAllProteines() {
		try {
			ArrayList<String> connectedProteinScores =
					QueryProcessor.getConnectedProteinScore(getProteinesAsString());

			for (String connectedProteinScore
					: connectedProteinScores) {
				String[] proteinsAndScore =
					connectedProteinScore.split("=");

				String[] proteins =
					proteinsAndScore[0].split("->");
				int score =
					Integer.parseInt(proteinsAndScore[1].
							trim());

				String proteinA = proteins[0].trim();
				String proteinB = proteins[1].trim();

				add(proteinA, proteinB, score);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * Marks the protein to contain a mutation.
	 *
	 * @param protein
	 *            The name of the protein that has a mutation
	 */
	public void putMutation(final String protein) {
		this.getProtein(protein).setMutation(true);
	}
	
	public Collection<String> getMutatedProteinsAsString() {
		Collection<String> proteins = new ArrayList<String>();
		for(Protein p : getProteines()) {
			if(p.hasMutation())
				proteins.add(p.getName());
		}
		return proteins;
	}
	
	public void getOtherConnectedMutatedProteins(int patientId)
	{
		ResultSet rs;
		try {
			rs = QueryProcessor.getOtherConnectedMutatedProteins(patientId, getMutatedProteinsAsString(), getProteinesAsString());
			while(rs.next()) {
				String p1 = rs.getString("protein_a_id");
				String p2 = rs.getString("protein_b_id");
				int combinedScore = rs.getInt("combined_score");
				add(p1, p2, combinedScore);
				this.putMutation(p1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
