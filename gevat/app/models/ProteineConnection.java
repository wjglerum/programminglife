package models;

public class ProteineConnection {
	private Proteine p1;
	private Proteine p2;
	private int combinedScore;
	
	public ProteineConnection(Proteine p1, Proteine p2, int score) {
		this.p1 = p1;
		this.p2 = p2;
		combinedScore = score;
		if(!p1.hasConnection(this))
		{
			p1.addConnection(this);
			p2.addConnection(this);
		}
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
	
	public boolean equals(ProteineConnection that)
	{
		if(this.p1 == that.p1)
		{
			return this.p2 == that.p2;
		}
		else
			return this.p1 == that.p2 && this.p2 == that.p1;
	}

}
