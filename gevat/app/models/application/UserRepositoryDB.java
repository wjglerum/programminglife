package models.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import models.database.Database;
import models.security.BCrypt;
import play.Logger;

public class UserRepositoryDB implements UserRepository {

	/**
	 * Returns a new user with data from the database, based on the username.
	 * 
	 * @param username
	 *            adsfad
	 * @return User adfasd
	 * @throws SQLException
	 */
	public User getUser(String username) throws SQLException {
		String query = "SELECT u_id, name, surname FROM users WHERE username = '"
				+ username + "';";
		try (ResultSet rs = Database.select("data", query);) {
			if (rs.next()) {
				int u_id = rs.getInt("u_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				return new User(u_id, name, surname, username);
			}
		}
		return null;
	}

	@Override
	/**
	 * Authenticate user by checking username and password.
	 * 
	 * @param username
	 * @param password
	 * @return User
	 * @throws SQLException
	 */
	public User authenticate(String username, String password)
			throws SQLException {
		String query = "SELECT * FROM users WHERE username = '" + username
				+ "';";
		try (ResultSet rs = Database.select("data", query);) {
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
		}
		return null;
	}

}
