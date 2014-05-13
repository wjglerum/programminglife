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
	 * Execute a query on the string database to get the proteins
	 * interacting with the given protein. The amount of results 
	 * is limited to the parameter limit and only those with with
	 * a likelihood greater than the threshold
	 * 
	 * @param con The connection used
	 * @param proteine The codename of the protein to be looked up
	 * @param limit The amount of entries to get
	 * @param threshold The lowest combined score allowed
	 * @throws SQLException
	 */
	public static void executeStringQuery(Connection con, String proteine, int limit, int threshold) throws SQLException {
		//if limit is zero, then it means get all the results
		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}
		//if threshold is zero, then it means get all the results
		if (threshold <= 0) {
			threshold = 0;
		}
		String q = "SELECT "
				+ "protein_b.preferred_name, combined_score "
				+ "FROM "
				+ "network.node_node_links, "
				+ "items.proteins AS protein_a, "
				+ "items.proteins AS protein_b, "
				+ "items.species "
				+ "WHERE "
				+ "protein_a.species_id = items.species.species_id AND "
				+ "official_name = 'Homo sapiens' AND "
				+ "protein_a.preferred_name = '" + proteine + "' AND "
				+ "node_id_a = protein_a.protein_id AND "
				+ "node_id_b = protein_b.protein_id AND "
				+ "combined_score > " + threshold
				+ "ORDER BY combined_score DESC "
				+ "LIMIT " + limit;
			
			try (Statement stmt = con.createStatement()) {
				ResultSet rs = stmt.executeQuery(q);
				//go over all the results and print them
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
	 * Execute a query on the SNP database to get all the subsnps
	 * related to the given id
	 *
	 * other parameters to be determined.
	 * @param con The connection used
	 * @throws SQLException
	 */
	public static void executeSNPQuery(Connection con, int id) throws SQLException {
		String q = "SELECT "
				+ "SNP.snp_id, SNP.tax_id, SubSNP.subsnp_id "
				+ "FROM "
				+ "SNP, SNPSubSNPLink, SubSNP "
				+ "WHERE "
				+ "SNP.snp_id = " + id + " AND "
				+ "SNP.snp_id = SNPSubSNPLink.snp_id AND "
				+ "SNPSubSNPLink.subsnp_id = SubSNP.subsnp_id";
		
		try (Statement stmt = con.createStatement()) {
			ResultSet rs = stmt.executeQuery(q);
			//go over all the results and print them
			while (rs.next()) {
				int snp_id = rs.getInt("snp_id");
				int tax_id = rs.getInt("tax_id");
				int subsnp_id = rs.getInt("subsnp_id");
				System.out.println(snp_id + "\t" + tax_id + "\t" + subsnp_id);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Execute a standard cadd/score query.
	 *
	 * other parameters to be determined.
	 * @param con The connection used
	 * @throws SQLException
	 */
	public static void executeScoreQuery(Connection con) throws SQLException {
		System.out.println("to do");
	}
	/**
	 * The main method.
	 *
	 * @param args Standard
	 * @throws ClassNotFoundException In case the driver cannot be found
	 * @throws SQLException In case something goes wrong with the sql
	 */
	public static void main(final String[] args) throws ClassNotFoundException, SQLException {
		//opens the ssh tunnel
		PortForwardingL.startPortForwarding();
		
		//the databaseconnections
		Connection stringCon = DBConnection.getConnection("string");
		Connection SNPCon = DBConnection.getConnection("snp");
		
		//BRCA1 is the example given in the email
		executeStringQuery(stringCon, "BRCA1", 0, 950);
		
		//the number given is the first id found in the vcf file
		executeSNPQuery(SNPCon, 28402963);
		
		//closes the ssh tunnel
		PortForwardingL.stopPortForwarding();
	}
}
