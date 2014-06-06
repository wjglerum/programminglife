package models;

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
	public ProteineGraph(int snp, int limit, int threshold) {
		addConnectionsOfSnp(snp, limit, threshold);
		connectAllProteines();
	}

	/**
	 * Looks up the proteine at the location of the snp and adds this proteine and it's possible connected proteines to ProteineGraph
	 * @param snp the rsid of the location
	 */
	public void addConnectionsOfSnp(int snp, int limit, int threshold) {
		addDistantConnectionsOfSnp(snp, limit, threshold, 1);
	}
	
	/**
	 * Looks up the proteine at the location of the snp and adds this proteine and it's possible connected (direct or indirect) proteines to ProteineGraph
	 * @param snp the rsid of the location
	 */
	public void addDistantConnectionsOfSnp(int snp, int limit, int threshold,
			int distance) {
		try {
			Collection<Proteine> currProteines = new HashSet<Proteine>();
			for (String s : QueryProcessor.findGenes(snp, limit, threshold))
				currProteines.add(getProteine(s));
			while (distance-- > 0) {
				Collection<Proteine> newProteines = new HashSet<Proteine>();
				for(Proteine p : currProteines)
				{
					newProteines.addAll(findGeneConnections(p.getName(), limit, threshold, this));
				}
				currProteines = newProteines;
			}
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
			findGeneConnections(proteine, 10, 700, this);
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}

	/**
	 * Add proteine p1 and it's connections with proteines in connections to ProteineGraph
	 * @param p1 a proteine
	 * @param connections a string in the format of "[proteine1\tcombinedscore1,...proteineN\tcombinedscoreN]"
	 * @return 
	 */
	public Collection<Proteine> add(String p1, String connections) {
		Collection<Proteine> newProteines= new ArrayList<Proteine>(); 
		for (String s : connections.substring(1, connections.length() - 1)
				.split(",")) {
			if(s.length()==0)
				continue;
			String p2 = s.split("\t")[0].trim();
			if(!hasProteine(p2))
				newProteines.add(getProteine(p2));
			Logger.info(s);
			add(p1,p2, Integer.parseInt(s.split("\t")[1].trim()));
		}
		return newProteines;
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
		if (!hasProteine(name))
			proteines.put(name, new Proteine(name));
		return proteines.get(name);
	}
	
	public boolean hasProteine(String name) {
		return proteines.containsKey(name);
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

	public static Collection<Proteine> findGeneConnections(final int id,
			final int limit, final int threshold, ProteineGraph pg)
					throws SQLException {
		ArrayList<String> qResult = QueryProcessor.
				findGenesAssociatedWithSNP(id);
		if (!qResult.isEmpty()) {
			return findGeneConnections(qResult.get(0), limit, threshold, pg);
		}
		return new ArrayList<Proteine>();
	}

	public static Collection<Proteine> findGeneConnections(final String p1,
			final int limit, final int threshold, ProteineGraph pg)
					throws SQLException {
			return pg.add(p1, QueryProcessor.executeStringQuery(
					p1, limit, threshold).toString());
	}
}
