package models.protein;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import models.database.QueryProcessor;
import models.reader.GeneDiseaseLinkReader;
import play.Logger;

/**
 * 
 * @author rhvanstaveren
 * 
 */
public class ProteinGraph {
	private Map<String, Protein> proteines = new HashMap<String, Protein>();
	private Collection<ProteinConnection> connections = new ArrayList<ProteinConnection>();
	private static QueryProcessor qp = new QueryProcessor();
	
	/**
	 * Creates an empty ProteineGraph
	 */
	public ProteinGraph() {
	    qp = new QueryProcessor();
	}

	/**
	 * Creates and ProteineGraph using the proteine the location of the snp.
	 * 
	 * @param snp
	 *            the rsid of the location
	 */
	public ProteinGraph(int snp, int limit, int threshold) {
		try {
            addConnectionsOfSnp(snp, limit, threshold);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
	 * @throws IOException 
	 */
	public ProteinGraph(int snp, int limit, int threshold, int distance) throws IOException {
		addDistantConnectionsOfSnp(snp, limit, threshold, distance);
		connectAllProteines();
	}

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine
	 * and it's possible connected proteines to ProteineGraph
	 * 
	 * @param snp
	 *            the rsid of the location
	 * @throws IOException 
	 */
	public void addConnectionsOfSnp(int snp, int limit, int threshold) throws IOException {
		try {
			ArrayList<String> qResult = qp.findGenesAssociatedWithSNP(snp);
			if (!qResult.isEmpty()) {
				String protein = qResult.get(0);
				addConnectionsOfProteine(protein, limit, threshold);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
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
	 * @throws IOException 
	 */
	public void addDistantConnectionsOfSnp(int snp, int limit, int threshold,
			int distance) throws IOException {
		try {
			ArrayList<String> qResult = qp.findGenesAssociatedWithSNP(snp);
			if (!qResult.isEmpty()) {
				String protein = qResult.get(0);
				addDistantConnectionsOfProtein(protein, limit, threshold,
						distance);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * Adds the proteine and it's possible connected proteines to ProteineGraph
	 * 
	 * @param protein
	 * @param distance
	 *            the maximum amount of connections of an protein added to the
	 *            graph
	 * @throws IOException 
	 */
	public void addDistantConnectionsOfProtein(String protein, int limit,
			int threshold, int distance) throws IOException {
		Collection<Protein> currProteins = new HashSet<Protein>();
		currProteins.add(getProtein(protein));
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
	 * @throws IOException 
	 */
	public Collection<Protein> addConnectionsOfProteine(String protein,
			int limit, int threshold) throws IOException {
		try {
			return addConnections(protein, QueryProcessor.findGeneConnections(
					protein, limit, threshold));
		} catch (SQLException e) {
			Logger.info(e.toString());
			return new ArrayList<Protein>();
		}
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
	private Collection<Protein> addConnections(String p1, String connections) {
		Collection<Protein> newProteins = new ArrayList<Protein>();
		if (connections.length() == 0)
			return newProteins;
		for (String s : connections.substring(1, connections.length() - 1)
				.split(",")) {
			String p2 = s.split("\t")[0].trim();
			if (!hasProtein(p2))
				newProteins.add(getProtein(p2));
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
	private void add(String p1, String p2, int combinedScore) {
		ProteinConnection pc = new ProteinConnection(getProtein(p1),
				getProtein(p2), combinedScore);
		if (!this.connections.contains(pc)) {
			this.connections.add(pc);
		}
	}

	/**
	 * returns the proteine with this name. If this proteine does not excist in
	 * ProteineGraph a new proteine is made
	 * 
	 * @param name
	 * @return the proteine with this name
	 */
	public Protein getProtein(String name) {
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

	public boolean hasProtein(String name) {
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

				add(proteinA, proteinB, score);
			}
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}
}