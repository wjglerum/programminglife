package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;
import play.db.DB;

public class Database {

	/**
	 * Make connection and execute query, does not return results
	 * @param database
	 * @param query
	 */
	private static void doQuery(String database, String query) {
		try (Connection con = DB.getConnection(database);) {
			con.createStatement().execute(query);
		} catch (SQLException e) {
			Logger.error((e.toString()));
		}
	}

	/**
	 * Returns a ResultSet with the results from the SELECT query.
	 * @param database
	 * @param query
	 * @return ResultSet
	 */
	public static ResultSet select(String database, String query) {
		try (Connection con = DB.getConnection(database);) {
			return con.createStatement().executeQuery(query);
		} catch (SQLException e) {
			Logger.error((e.toString()));
		}
		return null;
	}

	/**
	 * Execute insert statement
	 * @param database
	 * @param query
	 */
	public static void insert(String database, String query) {
		doQuery(database, query);
	}

	/**
	 * Execute delete statement
	 * @param database
	 * @param query
	 */
	public static void delete(String database, String query) {
		doQuery(database, query);
	}

}
