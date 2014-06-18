package models.user;

/**
 * User class.
 */
public class User {

	private int id;
	private String name;
	private String surname;
	private String username;

	/**
	 * User for access control in the application.
	 * 
	 * @param id User id
	 * @param name Users first name
	 * @param surname Users surname
	 * @param username Username
	 */
	public User(int id, String name, String surname, String username) {
		this.id = id;
		this.setName(name);
		this.setSurname(surname);
		this.setUsername(username);
	}

	public int getId() {
		return id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
