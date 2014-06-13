package models.protein;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import models.database.QueryProcessor;
import models.reader.GeneDiseaseLinkReader;
import play.Logger;

/**
 * 
 * @author rhvanstaveren
 * 
 */

public class ProteinGraph {

	private static Map<String, Protein> proteines = new HashMap<String, Protein>();
	public static Collection<ProteinConnection> connections = new ArrayList<ProteinConnection>();

	private final ProteinRepositoryDB repositoryMock = new ProteinRepositoryDB();
	private final ProteinService proteinService = new ProteinService(
			repositoryMock);

	/**
	 * Creates an empty ProteineGraph
	 */
	public ProteinGraph() {
	}

	/**
	 * Creates and ProteineGraph using the proteine the location of the snp.
	 * 
	 * @param snp
	 *            the rsid of the location
	 * @throws SQLException
	 */
	public ProteinGraph(int snp, int limit, int threshold) throws SQLException {
		proteinService.addConnectionsOfSnp(this, snp, limit, threshold);
		connectAllProteines();
	}

	/**
	 * /** Add proteine p1 and it's connections with proteines in connections to
	 * ProteineGraph
	 * 
	 * @param p1
	 *            a proteine
	 * @param connections
	 *            a string in the format of
	 *            "[proteine1\tcombinedscore1,...proteineN\tcombinedscoreN]"
	 *            Creates and ProteineGraph using the proteine the location of
	 *            the snp.
	 * @param snp
	 *            the rsid of the location
	 * @param distance
	 *            defines the allowed distance to the protein on the snp
	 *            location
	 * @throws SQLException
	 */
	public ProteinGraph(int snp, int limit, int threshold, int distance)
			throws SQLException {
		proteinService.addDistantConnectionsOfSnp(this, snp, limit, threshold,
				distance);
		connectAllProteines();
	}

	/**
	 * returns the proteine with this name. If this proteine does not excist in
	 * ProteineGraph a new proteine is made
	 * 
	 * @param name
	 * @return the proteine with this name
	 */
	public static Protein getProtein(String name) {
		if (!hasProtein(name))
			try {
				proteines.put(
						name,
						new Protein(name, GeneDiseaseLinkReader
								.findGeneDiseaseAssociation(name)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return proteines.get(name);
	}

	public static boolean hasProtein(String name) {
		return proteines.containsKey(name);
	}

	public String toString() {
		return "<ProteineGraph: " + proteines.toString() + ">";
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

	private void connectAllProteines() {
		try {
			ArrayList<String> connectedProteinScores = QueryProcessor
					.getConnectedProteinScore(getProteinesAsString());

			for (String connectedProteinScore : connectedProteinScores) {
				String[] proteinsAndScore = connectedProteinScore.split("=");

				String[] proteins = proteinsAndScore[0].split("->");
				int score = Integer.parseInt(proteinsAndScore[1].trim());

				String proteinA = proteins[0].trim();
				String proteinB = proteins[1].trim();

				proteinService.add(proteinA, proteinB, score);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}
}
