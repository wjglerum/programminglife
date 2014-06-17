package models.protein;

/**
 * 
 * @author robbertvs
 *
 * A connection between two proteins;
 * 
 */
public class ProteinConnection {
	private Protein p1;
	private Protein p2;
	private int combinedScore;

	/**
	 * Constructor of ProteinConnection also add itself to the list of connections of both proteins.
	 * @param p1 first protein
	 * @param p2 second protein
	 * @param score the combined score of the proteins
	 */
	public ProteinConnection(Protein p1, Protein p2, int score) {
		this.p1 = p1;
		this.p2 = p2;
		combinedScore = score;
		if (!p1.hasConnection(this)) {
			p1.addConnection(this);
			p2.addConnection(this);
		}
	}

	/**
	 * Gives the protein on the other end of the connection.
	 * @param p The protein on one end
	 * @return The protein on the other end, null if p is not one of the proteins of this connection
	 */
	public Protein getOtherProteine(Protein p) {
		if (p == p1) {
			return p2;
		}
		if (p == p2) {
			return p1;
		}
		return null;
	}

	public Protein getProteineFrom() {
		return p1;
	}

	public Protein getProteineTo() {
		return p2;
	}

	public int getCombinedScore() {
		return combinedScore;
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof ProteinConnection) {
			ProteinConnection pc = (ProteinConnection) that;
			if (this.p1.equals(pc.p1)) {
				return this.p2.equals(pc.p2);
			} else {
				return this.p1.equals(pc.p2) && this.p2.equals(pc.p1);
			}
		}
		return false;
	}
}
