package models;

import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;

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

	/**
	 * Returns a new user with data from the database, based on the username.
	 * 
	 * @param username
	 * @return User
	 * @throws SQLException
	 */
	public static User getUser(String username) throws SQLException {
		String query = "SELECT u_id, name, surname FROM users WHERE username = '"
				+ username + "';";
		ResultSet rs = Database.select("data", query);
		if (rs.next()) {
			int u_id = rs.getInt("u_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			return new User(u_id, name, surname, username);
		}
		return null;
	}

	/**
	 * Authenticate user by checking username and password.
	 * 
	 * @param username
	 * @param password
	 * @return User
	 * @throws SQLException
	 */
	public static User authenticate(String username, String password)
			throws SQLException {
		String query = "SELECT * FROM users WHERE username = '" + username
				+ "';";
		ResultSet rs = Database.select("data", query);
		if (rs.next()) {
			int u_id = rs.getInt("u_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String pw = rs.getString("password");
			Logger.info(name + " " + surname + " logged in!");

			// check with BCrypt if hashed pw matches password
			if (BCrypt.checkpw(password, pw)) {
				return new User(u_id, name, surname, username);
			}
		}
		return null;
	}

}
