package models;

public class Patient {

<<<<<<< HEAD
	private String name;
	private String surname;
	private int bsn;

	public Patient(String name, String surname, int bsn) {
		this.name = name;
		this.surname = surname;
		this.bsn = bsn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getBsn() {
		return bsn;
	}

	public void setBsn(int bsn) {
		this.bsn = bsn;
	}
=======
  public String name;
  public int id;
  
  public Patient(String name, int id) {
    this.name = name;
    this.id = id;
  }
  
>>>>>>> a698499449ab7ec08a1015bd3a855138e641b09f
}
