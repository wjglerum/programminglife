package models;

import java.sql.SQLException;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

import play.Logger;

/**
 * 
 * @author rhvanstaveren
 * 
 */

public class ProteineGraph {
	private Map<String, Proteine> proteines = new HashMap<String, Proteine>();
	private Collection<ProteineConnection> connections = new HashSet<ProteineConnection>();

	/**
	 * Creates an empty ProteineGraph
	 */
	public ProteineGraph() {
	}

	/**
	 * Creates and ProteineGraph using the proteine the location of the snp.
	 * @param snp the rsid of the location
	 */
	public ProteineGraph(int snp) {
		addConnectionsOfSnp(snp);
		connectAllProteines();
	}

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine and it's possible connected proteines to ProteineGraph
	 * @param snp the rsid of the location
	 */
	public void addConnectionsOfSnp(int snp) {
		try {
			QueryProcessor.findGeneConnections(snp, 10, 700, this);
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * adds the proteine and it's possible connected proteines to ProteineGraph
	 * @param proteine A proteine to find neighbours of
	 */
	public void addConnectionsOfProteine(String proteine) {
		try {
			QueryProcessor.findGeneConnections(proteine, 10, 700, this);
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * Add proteine p1 and it's connections with proteines in connections to ProteineGraph
	 * @param p1 a proteine
	 * @param connections a string in the format of "[proteine1\tcombinedscore1,...proteineN\tcombinedscoreN]"
	 */
	public void add(String p1, String connections) {
		for (String s : connections.substring(1, connections.length() - 1)
				.split(",")) {
			add(p1,s.split("\t")[0], Integer.parseInt(s.split("\t")[1]));
		}
	}

	/**
	 * Add a connection between p1 and p2 to ProteineGraph
	 * @param p1 proteine one
	 * @param p2 proteine two
	 * @param combinedScore the combined score between proteine one and two
	 */
	public void add(String p1, String p2, int combinedScore)
	{
		ProteineConnection pc = new ProteineConnection(getProteine(p1),
				getProteine(p2), combinedScore);
		if (!this.connections.contains(pc)) {
			this.connections.add(pc);
		}
	}
	
	/**
	 * returns the proteine with this name. If this proteine does not excist in ProteineGraph a new proteine is made
	 * @param name
	 * @return the proteine with this name
	 */
	public Proteine getProteine(String name) {
		if (!proteines.containsKey(name))
			proteines.put(name, new Proteine(name));
		return proteines.get(name);
	}

	public String toString() {
		return "<ProteineGraph: " + proteines.toString() + ">";
	}

	public Collection<Proteine> getProteines() {
		return proteines.values();
	}

	public Collection<ProteineConnection> getConnections() {
		return connections;
	}
	
	public Collection<String> getProteinesAsString() {
		return proteines.keySet();
	}
	
	private void connectAllProteines()
	{
		try {
			QueryProcessor.getConnectedProteinScore(getProteinesAsString());
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}
}
