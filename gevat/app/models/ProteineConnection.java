package models;

public class ProteineConnection {
	private Proteine p1;
	private Proteine p2;
	private int combinedScore;
	
	public ProteineConnection(Proteine p1, Proteine p2, int score) {
		this.p1 = p1;
		this.p2 = p2;
		combinedScore = score;
		p1.addConnection(this);
		p2.addConnection(this);
	}
	
	public Proteine getOtherProteine(Proteine p)
	{
		if(p == p1)
			return p2;
		if(p == p2)
			return p1;
		return null;
	}
  
  public Proteine getProteineFrom()
  {
    return p1;
  }
  
  public Proteine getProteineTo()
  {
    return p2;
  }
  
  public int getCombinedScore()
  {
    return combinedScore;
  }
}
