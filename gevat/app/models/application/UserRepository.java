package models.application;

import java.sql.SQLException;

public interface UserRepository {
	public User getUser(String username) throws SQLException;

	public User authenticate(String username, String password)
			throws SQLException;
}
