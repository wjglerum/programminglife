package models.user;

import java.sql.SQLException;

/**
 * Interface for Users.
 * @author willem
 *
 */
public interface UserRepository {
	/**
	 * @param username Username
	 * @return The User
	 * @throws SQLException SQL Exception
	 */
	User getUser(String username) throws SQLException;

	/**
	 * Authenticates the user.
	 * @param username username.
	 * @param password password
	 * @return the User
	 * @throws SQLException SQL Exception
	 */
	User authenticate(String username, String password)
			throws SQLException;
}
