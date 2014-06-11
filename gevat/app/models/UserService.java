package models;

import java.sql.SQLException;

import play.Logger;

public class UserService {
	private static UserRepository userRepository;

	public UserService(final UserRepository userRepository) {
		UserService.userRepository = userRepository;
	}

	public static User getUser(String username) throws SQLException {
		Logger.info("Username " + username);
		return userRepository.getUser(username);
	}
	
	public static User authenticate(String username, String password) throws SQLException {
		return userRepository.authenticate(username, password);
	}
}