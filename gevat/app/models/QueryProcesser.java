package model;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	public static ArrayList<String> executeStringQuery(Connection con, String proteine, int limit, int threshold) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Proteine \t Score");
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
				+ "LIMIT " + limit + ";";
			
			try (Statement stmt = con.createStatement()) {
				ResultSet rs = stmt.executeQuery(q);
				//go over all the results and print them
				while (rs.next()) {
					String codeName = rs.getString("preferred_name");
					int score = rs.getInt("combined_score");
					list.add(codeName + "\t" + score);
					//System.out.println(codeName + "\t" + score);
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
			return list;
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
		ArrayList<String> list = new ArrayList<String>();
		list.add("snp_id \t tax_id \t subsnp_id");
		String q = "SELECT "
				+ "SNP.snp_id, SNP.tax_id, SubSNP.subsnp_id "
				+ "FROM "
				+ "SNP, SNPSubSNPLink, SubSNP "
				+ "WHERE "
				+ "SNP.snp_id = " + id + " AND "
				+ "SNP.snp_id = SNPSubSNPLink.snp_id AND "
				+ "SNPSubSNPLink.subsnp_id = SubSNP.subsnp_id;";
		
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
	public static ArrayList<String> executeScoreQuery(Connection con, String chrom, int positionLow, int positionHigh) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("chrom \t position \t ref \t alt \t rawscore \t phred");
		String q = "SELECT "
				+ "* "
				+ "FROM "
				+ "score "
				+ "WHERE "
				+ "chrom = '" + chrom + "' AND "
				+ "position >= " + positionLow + " AND "
				+ "position <= " + positionHigh + ";";
		
		try (Statement stmt = con.createStatement()) {
			ResultSet rs = stmt.executeQuery(q);
			//go over all the results and print them
			while (rs.next()) {
				int position = rs.getInt("position");
				String ref = rs.getString("ref");
				String alt = rs.getString("alt");
				float rawscore = rs.getFloat("rawscore");
				float phred = rs.getFloat("phred");
				list.add(chrom + "\t" + position + "\t" + ref + "\t" + alt + "\t" + rawscore + "\t" + phred);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return list;
	}
}