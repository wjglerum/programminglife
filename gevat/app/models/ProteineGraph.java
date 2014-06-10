package models;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import play.Logger;

/**
 * 
 * @author rhvanstaveren
 * 
 */

public class ProteineGraph {
	private Map<String, Proteine> proteines = new HashMap<String, Proteine>();
	private Collection<ProteineConnection> connections = new ArrayList<ProteineConnection>();

	/**
	 * Creates an empty ProteineGraph
	 */
	public ProteineGraph() {
	}

	/**
	 * Creates and ProteineGraph using the proteine the location of the snp.
	 * @param snp the rsid of the location
	 */
	public ProteineGraph(int snp, int limit, int threshold) {
		addConnectionsOfSnp(snp, limit, threshold);
		connectAllProteines();
	}

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine and it's possible connected proteines to ProteineGraph
	 * @param snp the rsid of the location
	 */
	public void addConnectionsOfSnp(int snp, int limit, int threshold) {
		try {
			QueryProcessor.findGeneConnections(snp, limit, threshold, this);
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
			add(p1,s.split("\t")[0].trim(), Integer.parseInt(s.split("\t")[1].trim()));
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
			try {
				proteines.put(name, new Proteine(name, GeneDiseaseLinkReader.findGeneDiseaseAssociation(name)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
      ArrayList<String> connectedProteinScores = QueryProcessor.getConnectedProteinScore(getProteinesAsString());
      
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
