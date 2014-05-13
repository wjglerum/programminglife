package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class processes queries.
 *
 * @author rbes
 *
 */
public class QueryProcesser {
	/**
	 * Processes a test query.
	 *
	 * @param con The connection to the db
	 * @throws SQLException In case it goes wrong
	 */
	public static void testQuery(final Connection con) throws SQLException {
		
		String query = "SELECT "
			+ "protein_b.preferred_name, combined_score "
			+ "FROM "
			+ "network.node_node_links, "
			+ "items.proteins AS protein_a, "
			+ "items.proteins AS protein_b, "
			+ "items.species "
			+ "WHERE "
			+ "protein_a.species_id = items.species.species_id AND "
			+ "official_name = 'Homo sapiens' AND "
			+ "protein_a.preferred_name = 'BRCA1' AND "
			+ "node_id_a = protein_a.protein_id AND "
			+ "node_id_b = protein_b.protein_id "
			+ "ORDER BY combined_score DESC "
			+ "LIMIT 50;";

		try (Statement stmt = con.createStatement()) {
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String codeName = rs.getString("preferred_name");
				int score = rs.getInt("combined_score");
				System.out.println(codeName + "\t" + score);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * The main method, calling the querytest at the moment.
	 *
	 * @param args Standard
	 * @throws ClassNotFoundException In case the driver cannot be found
	 * @throws SQLException In case something goes wrong with the sql
	 */
	public static void main(final String[] args) throws ClassNotFoundException, SQLException {
		Connection stringConn = DBConnection.getConnection("string");
		testQuery(stringConn);
	}
}
