package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import play.Logger;
import play.db.DB;

public class Database {

	/**
	 * Make connection and execute query, does not return results.
	 * 
	 * @param database
	 *            The name of the database
	 * @param query
	 *            The query to be executed, in string format
	 */
	private static void doQuery(final String database, final String query) {
		try (Connection connection = DB.getConnection(database);) {
			try (Statement statement = connection.createStatement();) {
				statement.execute(query);
			}
		} catch (SQLException e) {
			Logger.error((e.toString()));
		}
	}

	/**
	 * Returns a ResultSet with the results from the SELECT query.
	 * 
	 * @param database
	 *            The name of the database
	 * @param query
	 *            The query to be executed, in string format
	 * @return ResultSet ResultSet containing the results of the query
	 * @throws SQLException 
	 */
	public static ResultSet select(final String database, final String query) throws SQLException {
		try (Connection connection = DB.getConnection(database);) {
			return connection.createStatement().executeQuery(query);
		}
		connection.close();
	}

	/**
	 * Execute insert statement.
	 * 
	 * @param database
	 *            The name of the database
	 * @param query
	 *            The query to be executed, in string format
	 */
	public static void insert(final String database, final String query) {
		doQuery(database, query);
	}

	/**
	 * Execute delete statement.
	 * 
	 * @param database
	 *            The name of the database
	 * @param query
	 *            The query to be executed, in string format
	 */
	public static void delete(final String database, final String query) {
		doQuery(database, query);
	}

}
