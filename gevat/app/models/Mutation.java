package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

public class Mutation {

	public int id;
	public String sort;
	public String rsID;
	public int chromosome;

	// contains the alleles of child, father and mother
	public char[] alleles;

	public Mutation(int id, String sort, String rsID, int chromosome,
			char[] alleles) {
		this.id = id;
		this.sort = sort;
		this.rsID = rsID;
		this.chromosome = chromosome;
		this.alleles = alleles;
	}

	/*
	 * Return the alleles of the child
	 */
	public String child() {
		return "[" + alleles[0] + ", " + alleles[1] + "]";
	}

	/*
	 * Return the alleles of the father
	 */
	public String father() {
		return "[" + alleles[2] + ", " + alleles[3] + "]";
	}

	/*
	 * Return the alleles of the mother
	 */
	public String mother() {
		return "[" + alleles[4] + ", " + alleles[5] + "]";
	}

	/*
	 * Retrive a list of all the mutations of a patient
	 */
	public static List<Mutation> getMutations(int p_id) {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT * FROM mutations WHERE p_id = '" + p_id
					+ "';";
			ResultSet rs = con.createStatement().executeQuery(query);
			List<Mutation> mutations = new ArrayList<Mutation>();

			while (rs.next()) {
				int id = rs.getInt("m_id");
				String sort = rs.getString("sort");
				String rsID = rs.getString("rsID");
				int chromosome = rs.getInt("chromosome");
				char[] alleles = rs.getString("alleles").toCharArray();
				mutations
						.add(new Mutation(id, sort, rsID, chromosome, alleles));
			}
			return mutations;
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}
}
