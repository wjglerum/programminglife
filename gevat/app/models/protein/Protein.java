package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.application.GeneDiseaseLinkReader;
import models.application.Patient;
import models.database.QueryProcessor;
import models.dna.Mutation;

/**
 * The proteine class contains the name,
 * disease and connections to other proteines.
 *
 * @author rhvanstaveren
 *
 */
public class Protein {
	/**
	 * The connections it has to other other proteins.
	 */
	private Collection<ProteinConnection> connections =
			new ArrayList<ProteinConnection>();

	/**
	 * The name of the protein.
	 */
	private String name;

	/**
	 * The matching diseases found in {@link GeneDiseaseLinkReader}.
	 */
	private String disease;

	/**
	 * The constructor for Proteine.
	 *
	 * @param name The name of the proteine
	 * @param disease The disease(s) associated with it
	 */
	public Protein(final String name,
			final ArrayList<String> disease) {
		this.name = name;
		this.disease = disease.toString().substring(
				1, disease.toString().length() - 1);
	}

	/**
	 * Adds connections to the proteine.
	 *
	 * @param pc The connection
	 */
	public final void addConnection(final ProteinConnection pc) {
		connections.add(pc);
	}

	@Override
	public final boolean equals(final Object that) {
		if (that instanceof Protein) {
			return this.name.equals(((Protein) that).getName());
		}
		else if (that instanceof String) {
			return this.name.equals((String) that);
		}
		return false;
	}

	/**
	 * Gets the connections.
	 * @return Returns the connections of this proteine
	 */
	public final Collection<ProteinConnection> getConnections() {
		return this.connections;
	}

	/**
	 * Gets the annotations of the protein.
	 *
	 * @return Returns the annotations associated with the proteine
	 */
	public final String getAnnotations() {
		try {
			return QueryProcessor.getAnnotationsOfProtein(name);
		} catch (SQLException e) {
			return null;
		}
	}

	public final boolean hasConnection(final ProteinConnection pc) {
		return connections.contains(pc);
	}

	public final void setConnections(
			final Collection<ProteinConnection> connections) {
		this.connections = connections;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getName() {
		return this.name;
	}

	public final void setDisease(final String disease) {
		this.disease = disease;
	}

	public final String getDisease() {
		return this.disease;
	}
	
	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m) throws SQLException {
	  List<Mutation> mutations = Mutation.getMutations(p.getId());
	  ArrayList<Mutation> related = new ArrayList<Mutation>();
	  
	  for (Mutation mutation : mutations) {
      if (mutation.getId() != m.getId()) {
	      int rsId = Integer.parseInt(mutation.getRsID().substring(2));
	      
	      ProteinGraph pg = new ProteinGraph(rsId,10,300);
	      
	      for (Protein protein : pg.getProteines()) {
	        if (protein.getName().equals(this.getName()))
	          related.add(mutation);
	      }
	    }
	  }
	  
    return related;
	}
	
}