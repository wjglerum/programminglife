package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class Proteine {
	private Collection<ProteineConnection> connections = new ArrayList<ProteineConnection>();
	String name;
	
	public Proteine(String name) {
		this.name = name;
	}
	
	public void addConnection(ProteineConnection pc)
	{
		connections.add(pc);
	}
	
	public boolean equals(Object that)
	{
		if(that instanceof Proteine)
			return this.name.equals(((Proteine)that).getName());
		if(that instanceof String)
			return this.name.equals((String)that);
		return false;
	}
  
  public String getName() {
    return name;
  }
  
  public Collection<ProteineConnection> getConnections() {
    return connections;
  }

	public static Collection<Proteine> getProteinesByID(int[] ids, int limit, int threshold) throws SQLException
	{
		ArrayList<String> proteineStrings = QueryProcessor.findGenes(ids, limit, threshold);
		
		return ProteineParser.parseStringArray(proteineStrings);
	}
	
	private static class ProteineParser
	{
		static Map<String, Proteine> proteines;
		
		public static Collection<Proteine> parseStringArray(ArrayList<String> proteineStrings)
		{
		  proteines = new HashMap<String,Proteine>();
		  
		  for(int i=0; i<proteineStrings.size(); i+=2)
			{
				Proteine curr = getProteine(proteineStrings.get(i));
				Map<String, Integer> connections = parseConnections(proteineStrings.get(i+1));
				for(String s: connections.keySet())
				{
					new ProteineConnection(curr, getProteine(s), connections.get(s));
				}
			}
			return proteines.values();
		}
		
		private static Map<String, Integer> parseConnections(String input)
		{
			input = input.substring(1, input.length()-1);
			String[] list = input.split(", ");
			Map<String, Integer> map = new HashMap<String, Integer>();
			for(int i=0; i<list.length; i++) {
			  String[] splitted = list[i].split("\t");
				map.put(splitted[0], Integer.parseInt(splitted[1]));
			}
			return map;
		}
		
		private static Proteine getProteine(String name)
		{
			if(!proteines.containsKey(name))
				proteines.put(name, new Proteine(name));
			return proteines.get(name);
		}
	}
}
