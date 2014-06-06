package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class processes queries.
 *
 * @author rbes
 *
 */
public final class QueryProcessor {

	/**
	 * Done because it is a utility-class.
	 */
	private QueryProcessor() {
		//not called
	}

	/**
	 * Execute a query on the string database to get the
	 * proteins interacting with the given protein. The
	 * amount of results is limited to the parameter
	 * limit and only those with with a likelihood greater
	 * than the threshold
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
	public static ArrayList<String> executeStringQuery(
			final String proteine,
			final int limit, final int threshold)
					throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		assert (limit > 0) : "Needs at least 1";
		assert (threshold >= 0) : "Needs at least 0";
		String q = "SELECT protein_b.preferred_name, combined_score "
				+ "FROM network.node_node_links, "
				+ "items.proteins AS protein_a, "
				+ "items.proteins AS protein_b, items.species"
				+ " WHERE "
				+ "protein_a.species_id = "
				+ "items.species.species_id"
				+ " AND "
				+ "official_name = 'Homo sapiens' AND "
				+ "protein_a.preferred_name = '"
				+ proteine
				+ "' AND "
				+ "node_id_a = protein_a.protein_id"
				+ " AND "
				+ "node_id_b = protein_b.protein_id"
				+ " AND"
				+ " combined_score > "
				+ threshold + "ORDER BY combined_score DESC"
				+ " LIMIT "	+ limit	+ ";";

		ResultSet rs = Database.select("string", q);
		while (rs.next()) {
			String codeName = rs.getString("preferred_name");
			int score = rs.getInt("combined_score");
			list.add(codeName + "\t" + score);
		}
		return list;
	}

	/**
	 * Gets the connections and scores between all proteins in
	 * the ArrayList<String>.
	 *
	 * @param proteins The ArraList<String> containing the protein names
	 *
	 * @return Returns the list of connections that look like this:
	 * proteinA -> proteinB = score
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	public static ArrayList<String> getConnectedProteinScore(
			final Collection<String> proteins) throws SQLException {
		String formatted = formatForIN(proteins);
		ArrayList<String> list = new ArrayList<String>();
		
		if(formatted.length() == 0){
			return list;
		}
		
		String q = "SELECT combined_score, "
				+ "protein_a.preferred_name AS name_a, "
				+ "protein_b.preferred_name AS name_b "
				+ "FROM network.node_node_links, "
				+ "items.proteins AS protein_a, "
				+ "items.proteins AS protein_b "
				+ "WHERE "
				+ "protein_a.preferred_name IN ("
				+ formatted + ") AND "
				+ "protein_a.species_id = 9606 AND "
				+ "protein_b.preferred_name IN ("
				+ formatted + ") AND "
				+ "protein_b.species_id = 9606 AND "
				+ "node_id_a = protein_a.protein_id AND "
				+ "node_id_b = protein_b.protein_id "
				+ "ORDER BY combined_score DESC;";

		ResultSet rs = Database.select("string", q);
		
		if (rs != null) {
  		while (rs.next()) {
  			 int score = rs.getInt("combined_score");
  			 String nameA = rs.getString("name_a");
  			 String nameB = rs.getString("name_b");
  			 list.add(nameA + " -> " + nameB + " = " + score);
  		}
		}
		
		return list;
	}

	/**
	 * Formats the stringList to be  used in a 'IN' query.
	 *
	 * @param proteins The list of strings to be formatted
	 *
	 * @return Returns the formatted String
	 */
	public static String formatForIN(final Collection<String> proteins) {
	  String formatted = "";
		int i = 0;
		
	  for (String protein: proteins) {
	    if (i > 0)
	      formatted += ",";
	    
		  formatted += "'" + protein + "'";
		}
		
		return formatted;
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
	public static float executeScoreQuery(final String chrom,
			final int positionLow, final int positionHigh, final String uniqueBase)
					throws SQLException {
		String q = "SELECT * " + "FROM " + "score " + "WHERE " + "chrom = '" + chrom + 
				"' AND position >= " + positionLow + " AND position <= " + positionHigh + ";";
		// Get all the records from database score that have mutations on the same position as our position
		ResultSet rs = Database.select("score", q);
		String afwijking = uniqueBase;

		// If the mutation is the same value as the reference, return 0		
		while (rs.next()) {
			String alt = rs.getString("alt");
			float phred = rs.getFloat("phred");
			if (alt.equals(afwijking)) {
				return phred;
			}
		}
		return 0;
	}

	/**
	 * Gets the annotation of a protein.
	 *
	 * @param protein The preferred name of the protein
	 *
	 * @return Returns the annotation of the protein
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	public static String getAnnotationsOfProtein(final String protein)
			throws SQLException {
		String s = "";
		String q = "SELECT annotation "
				+ "FROM items.proteins "
				+ "WHERE preferred_name = '" + protein
				+ "' AND "
				+ "species_id = 9606";
		ResultSet rs = Database.select("string", q);
		while (rs.next()) {
			s = rs.getString("annotation");
			return s;
		}
		return s;
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
				+ "b138_SNPContigLocusId WHERE snp_id = "
				+ id + ";";

		ResultSet rs = Database.select("snp", q);
		// go over all the results and print them
		while (rs.next()) {
			String locusSymbol = rs.getString("locus_symbol");
			list.add(locusSymbol);
		}
		return list;
	}

	/**
	 * Finds genes connected to the supplied SNP.
	 *
	 * @param limit
	 *            The maximum amount of results
	 * @param threshold
	 *            The minimum score of two proteins
	 * @param id
	 *            The id of a SNP (without 'rs')
	 * @return Returns an ArrayList<String> containing the gene name,
	 * 		   with the names and scores of connected genes
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static ArrayList<String> findGenes(final int id,
			final int limit, final int threshold)
					throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> qResult = QueryProcessor.
				findGenesAssociatedWithSNP(id);
		for (String gene: qResult) {
			list.add(gene);
			list.add(QueryProcessor.executeStringQuery(
					gene,
					limit, threshold).toString());
		}
		return list;
	}
}
