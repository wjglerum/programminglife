package models.application;

import java.sql.SQLException;

public class UserService {
	private final UserRepository userRepository;

	public UserService(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getUser(String username) throws SQLException {
		return userRepository.getUser(username);
	}
	
	public User authenticate(String username, String password) throws SQLException {
		return userRepository.authenticate(username, password);
	}
}