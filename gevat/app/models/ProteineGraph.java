package models;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import play.Logger;

public class ProteineGraph {
	private Map<String, Proteine> proteines = new HashMap<String,Proteine>();

	public ProteineGraph()
	{
	}
	
	public ProteineGraph(int snp)
	{
		addConnectionsOfSnp(snp);
	}
	
	public void addConnectionsOfSnp(int snp)
	{
		try {
			QueryProcessor.findGeneConnections(snp, 10, 700, this);
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}
	
	public void addConnectionsOfProteine(String proteine)
	{
		try {
			QueryProcessor.findGeneConnections(proteine, 10, 700, this);
		} catch (SQLException e) {
			Logger.info(e.toString());
		}
	}
	
	public void add(String p1, String connections)
	{
		for(String s : connections.substring(1,connections.length()-1).split(","))
		{
			new ProteineConnection(getProteine(p1), getProteine(s.split("\t")[0]), Integer.parseInt(s.split("\t")[1]));
		}
	}
	
	public Proteine getProteine(String name)
	{
		if(!proteines.containsKey(name))
			proteines.put(name, new Proteine(name));
		return proteines.get(name);
	}
	
	public String toString()
	{
		return "<ProteineGraph: " + proteines.toString() + ">";
	}
}
