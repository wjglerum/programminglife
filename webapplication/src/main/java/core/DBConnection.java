package core;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * The DBConnection class is meant to make a connection to the postgresql
 * server.
 *
 * @author rbes
 */
public class DBConnection {
	/**
	 * Something.
	 *
	 * @param dbUser The user that connects to the database
	 * @param dbPassword The password for the user
	 * @param url The url of the database, including ports
	 */
	private static Connection dbConn = null;
	private static final String dbUser = "student";
	private static final String dbPassword = "genomebrowser";
	private static final String url = "jdbc:postgresql://localhost:5432/string";

	/**
	 * GetConnection makes the connection and returns it.
	 *
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection() throws SQLException, ClassNotFoundException {

		//database connectivity for postgres
		Class.forName("org.postgresql.Driver");
		dbConn = DriverManager.getConnection(url, dbUser, dbPassword);

		System.out.println("Database connection established");

		return dbConn;
	}
}