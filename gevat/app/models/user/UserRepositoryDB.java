package models.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import models.database.Database;
import models.security.BCrypt;
import play.Logger;

/**
 * Repository that accesses the DB for Users.
 * 
 * @author willem
 * 
 */
public class UserRepositoryDB implements UserRepository {

	/**
	 * Returns a new user with data from the database, based on the username.
	 * 
	 * @param username username
	 * @return User
	 * @throws SQLException SQL Exception
	 */
	@Override
	public User getUser(String username) throws SQLException {
		String query = "SELECT u_id, name, surname FROM users WHERE username = '"
				+ username + "';";
		try (ResultSet rs = Database.select("data", query);) {
			if (rs.next()) {
				int userId = rs.getInt("u_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				return new User(userId, name, surname, username);
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
				int userId = rs.getInt("u_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String pw = rs.getString("password");
				Logger.info(name + " " + surname + " logged in!");

				// check with BCrypt if hashed pw matches password
				if (BCrypt.checkpw(password, pw)) {
					return new User(userId, name, surname, username);
				}
			}
		}
		return null;
	}

}
