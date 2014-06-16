package models.protein;

import models.database.Database;
import play.Logger;

public class ProteinConnection {
	protected Protein p1;
	protected Protein p2;
	private int combinedScore;

	public ProteinConnection(Protein p1, Protein p2, int score) {
		this.p1 = p1;
		this.p2 = p2;
		combinedScore = score;
		if (!p1.hasConnection(this)) {
			p1.addConnection(this);
			p2.addConnection(this);
		}
	}

	public Protein getOtherProteine(Protein p) {
		if (p == p1)
			return p2;
		if (p == p2)
			return p1;
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
			{
				if (this.p1.equals(pc.p1)) {
					return this.p2.equals(pc.p2);
				} else
					return this.p1.equals(pc.p2) && this.p2.equals(pc.p1);
			}
		}
		return false;
	}
	
	public void insertIntoDB(int patientId)
	{
		String query = "INSERT INTO protein_connections VALUES ("
				+ patientId
				+ ",'"
				+ p1.getName()
				+ "','"
				+ p2.getName()
				+ "',"
				+ combinedScore
				+ ");";
//		Logger.info(query);
		Database.insert("data", query);
	}
}
