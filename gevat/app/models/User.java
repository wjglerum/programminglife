package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;
import play.db.DB;

public class User {

	public String username;
	public String name;
	public String password;

	public User(String username, String name, String password) {
		this.username = username;
		this.name = name;
		this.password = password;
	}

	/*
	 * Authenticate user by checking username and password
	 */
	public static User authenticate(String username, String password) {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT * FROM users WHERE username = '" + username
					+ "';";
			ResultSet rs = con.createStatement().executeQuery(query);
			if (rs.next()) {
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String pw = rs.getString("password");
				
				// check with BCrypt if hashed pw matches password
				if (BCrypt.checkpw(password, pw)) {
					return new User(username, name + " " + surname, password);
				}
			}
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}

}