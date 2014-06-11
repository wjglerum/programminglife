package models.user;

import java.sql.SQLException;

/**
 * Interface for Users.
 * @author willem
 *
 */
public interface UserRepository {
	public User getUser(String username) throws SQLException;

	public User authenticate(String username, String password)
			throws SQLException;
}
