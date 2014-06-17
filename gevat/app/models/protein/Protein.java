package models.protein;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The proteine class contains the name, disease and connections to other
 * proteines.
 * 
 * @author rhvanstaveren
 * 
 */
public class Protein {
	/**
	 * The connections it has to other other proteins.
	 */
	private Collection<ProteinConnection> connections = new ArrayList<ProteinConnection>();

	/**
	 * The name of the protein.
	 */
	private String name;

	/**
	 * The matching diseases found in {@link GeneDiseaseLinkReader}.
	 */
	private String disease;
	
	/**
	 * True if the proteine has a mutation of type defined by the mutation class.
	 */
	private boolean hasMutation;

	/**
	 * The constructor for Proteine.
	 * 
	 * @param name
	 *            The name of the proteine
	 * @param disease
	 *            The disease(s) associated with it
	 */
	public Protein(final String name, final List<String> disease) {
		this.name = name;
		this.disease = disease.toString().substring(1,
				disease.toString().length() - 1);
	}

	@Override
	public final boolean equals(final Object that) {
		if (that instanceof Protein) {
			return this.name.equals(((Protein) that).getName());
		} else if (that instanceof String) {
			return this.name.equals((String) that);
		}
		return false;
	}

	/**
	 * Adds connections to the proteine.
	 * 
	 * @param pc
	 *            The connection
	 */
	public final void addConnection(final ProteinConnection pc) {
		connections.add(pc);
	}

	/**
	 * Returns wether the graph contains the connection pc.
	 * @param pc A proteinConnection
	 * @return True when the ProteinGraph contains pc
	 */
	public final boolean hasConnection(final ProteinConnection pc) {
		return connections.contains(pc);
	}

	public final Collection<ProteinConnection> getConnections() {
		return this.connections;
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
	
	public final void setMutation(boolean mutation) {
		hasMutation = mutation;
	}
	
	/**
	 * returns whether ProteinGraph contains this mutation.
	 * @return True when ProteinGraph contains this mutation
	 */
	public final boolean hasMutation() {
		return hasMutation;
	}

}