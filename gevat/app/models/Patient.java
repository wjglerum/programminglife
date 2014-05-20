package models;

import java.util.Arrays;
import java.util.List;

public class Patient {

  public int id;
	public String name;
	public String surname;

	public Patient(int id, String name, String surname) {
    this.id = id;
		this.name = name;
		this.surname = surname;
	}
  
  public static Patient get(int id) {
    return new Patient(id,"A","A");
  }
  
  public static List<Patient> getAll() {
    Patient a = new Patient(1,"A","A");
    Patient b = new Patient(2,"B","B");
    Patient c = new Patient(3,"C","C");
    
    return Arrays.asList(a,b,c);
  }
	
}
