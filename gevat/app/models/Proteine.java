package models;

import java.util.ArrayList;
import java.util.Collection;

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
	
	public boolean hasConnection(ProteineConnection pc)
	{
		return connections.contains(pc);
	}
}
