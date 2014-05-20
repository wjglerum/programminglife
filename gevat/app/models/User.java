package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;
import play.db.DB;

public class User {

	public int id;
	public String name;
	public String surname;
	public String username;

	public User(int id, String name, String surname, String username) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.username = username;
	}

	/*
	 * Get user information by username
	 */
	public static User getUser(String username) {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT u_id, name, surname FROM users WHERE username = '"
					+ username + "';";
			ResultSet rs = con.createStatement().executeQuery(query);
			if (rs.next()) {
				int u_id = rs.getInt("u_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				return new User(u_id, name, surname, username);
			}
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
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
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}

}