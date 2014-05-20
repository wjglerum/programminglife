package models;

public class Patient {

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
}
