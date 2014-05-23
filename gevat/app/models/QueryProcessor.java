package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * This class processes queries.
 *
 * @author rbes
 *
 */
public class QueryProcessor {

	/**
	 * Execute a query on the string database to get the proteins interacting
	 * with the given protein. The amount of results is limited to the parameter
	 * limit and only those with with a likelihood greater than the threshold
	 * 
	 * @param proteine
	 *            The codename of the protein to be looked up
	 * @param limit
	 *            The amount of entries to get
	 * @param threshold
	 *            The lowest combined score allowed
	 * @return Returns an ArrayList<String>
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static ArrayList<String> executeStringQuery(final String proteine,
			int limit, int threshold) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		// if limit is zero, then it means get all the results
		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}
		// if threshold is zero, then it means get all the results
		if (threshold <= 0) {
			threshold = 0;
		}
		String q = "SELECT protein_b.preferred_name, combined_score "
				+ "FROM network.node_node_links, "
				+ "items.proteins AS protein_a, "
				+ "items.proteins AS protein_b, items.species WHERE "
				+ "protein_a.species_id = items.species.species_id AND "
				+ "official_name = 'Homo sapiens' AND "
				+ "protein_a.preferred_name = '" + proteine + "' AND "
				+ "node_id_a = protein_a.protein_id AND "
				+ "node_id_b = protein_b.protein_id AND combined_score > "
				+ threshold + "ORDER BY combined_score DESC LIMIT " + limit
				+ ";";

		ResultSet rs = Database.select("string", q);
		while (rs.next()) {
			String codeName = rs.getString("preferred_name");
			int score = rs.getInt("combined_score");
			list.add(codeName + "\t" + score);
		}
		return list;
	}

	/**
	 * Finds the score. Not needed at the moment.
	 * 
	 * @param chrom
	 *            Something
	 * @param positionLow
	 *            Something
	 * @param positionHigh
	 *            Something
	 * @return Returns a list containing the score
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static ArrayList<String> executeScoreQuery(final String chrom,
			final int positionLow, final int positionHigh) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("chrom \t position \t ref \t alt \t rawscore \t phred");
		String q = "SELECT * " + "FROM " + "score " + "WHERE " + "chrom = '"
				+ chrom + "' AND position >= " + positionLow
				+ " AND position <= " + positionHigh + ";";

		ResultSet rs = Database.select("score", q);
		while (rs.next()) {
			int position = rs.getInt("position");
			String ref = rs.getString("ref");
			String alt = rs.getString("alt");
			float rawscore = rs.getFloat("rawscore");
			float phred = rs.getFloat("phred");
			list.add(chrom + "\t" + position + "\t" + ref + "\t" + alt + "\t"
					+ rawscore + "\t" + phred);
		}
		return list;
	}

	/**
	 * Finds genes associated with the given rs_id.
	 * 
	 * @param id
	 *            The id of the SNP
	 * @return Returns an ArrayList<String> with all the found locus_symbols
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static ArrayList<String> findGenesAssociatedWithSNP(final int id)
			throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		String q = "SELECT DISTINCT locus_symbol FROM "
				+ "b138_SNPContigLocusId WHERE snp_id = " + id + ";";

		ResultSet rs = Database.select("snp", q);
		// go over all the results and print them
		while (rs.next()) {
			String locusSymbol = rs.getString("locus_symbol");
			list.add(locusSymbol);
		}
		return list;
	}

	/**
	 * Finds genes connected to the supplied snp's.
	 * 
	 * @param input
	 *            The id's retrieved from the VCF-file
	 * @return Returns a list containing the gene name, with the names and
	 *         scores of connected genes
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static ArrayList<String> findGenes(final int[] input)
			throws SQLException {
		// int[] input = {1342820, 1381816, 11129221, 6419994, 4273600,
		// 10052679, 1284990, 28558837, 2214772, 6988186, 7010760, 78217953,
		// 4917561, 2761830, 339454, 7993685, 112642323, 4776256, 2967158,
		// 549728};
		ArrayList<String> list = new ArrayList<String>();

		for (int id : input) {
			ArrayList<String> qResult = QueryProcessor
					.findGenesAssociatedWithSNP(id);
			if (!qResult.isEmpty()) {
				list.add(qResult.get(0));
				list.add(QueryProcessor.executeStringQuery(qResult.get(0), 10,
						700).toString());
			}
		}
		return list;
	}
}