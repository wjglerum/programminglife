package models.application;

public class User {

	public int id;
	public String name;
	public String surname;
	public String username;

	/**
	 * User for access control in the application.
	 * 
	 * @param id
	 * @param name
	 * @param surname
	 * @param username
	 */
	public User(int id, String name, String surname, String username) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.username = username;
	}
}
