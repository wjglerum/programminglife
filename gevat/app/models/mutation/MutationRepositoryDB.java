package models.mutation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import models.database.Database;
import models.database.QueryProcessor;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypesContext;

import play.Logger;

/**
 * The repository containing all database related functions.
 */
public class MutationRepositoryDB implements MutationRepository {

	/**
	 * Retrieve a list of all the mutations of a patient.
	 *
	 * @param pId The id
	 *
	 * @return Returns List<Mutation>, a list of all mutations
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	@Override
	public final List<Mutation> getMutations(final int pId)
			throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '"
			+ pId + "';";
		ResultSet rs = Database.select("data", query);
		List<Mutation> mutations = new ArrayList<Mutation>();

		while (rs.next()) {
			int id = rs.getInt("m_id");
			String sort = rs.getString("sort");
			String rsID = rs.getString("rsID");
			String chromosome = rs.getString("chromosome");
			Collection<Allele> alleles =
					Mutation.toAlleleCollection(rs
					.getString("alleles"));
			GenotypesContext genotypescontext =
					Mutation.toGenotypesContext(rs
					.getString("alleles"));
			int startPoint = rs.getInt("startpoint");
			int endPoint = rs.getInt("endpoint");
			int positionGRCH37 = rs.getInt("GRCH37_pos");
			float cadd = rs.getFloat("CADD_score");
			float frequency = rs.getFloat("frequency");
			mutations.add(new Mutation(id, sort, rsID, chromosome,
					alleles, startPoint, endPoint,
					genotypescontext, positionGRCH37,
					cadd, frequency));
		}
		return mutations;
	}

	/**
	 * Retrieve a list of all the mutations of a patient in a certain
	 * chromosome.
	 *
	 * @param pId The id
	 * @param cId The chromosome number
	 *
	 * @return Returns List<Mutation>, a list of all mutations in the
	 *         chromosome.
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	@Override
	public final List<Mutation> getMutations(
			final int pId, final String cId)
			throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '" + pId
				+ "' and chromosome = '" + cId + "';";
		ResultSet rs = Database.select("data", query);
		List<Mutation> mutations = new ArrayList<Mutation>();

		while (rs.next()) {
			int id = rs.getInt("m_id");
			String sort = rs.getString("sort");
			String rsID = rs.getString("rsID");
			String chromosome = rs.getString("chromosome");
			Collection<Allele> alleles =
					Mutation.toAlleleCollection(rs
					.getString("alleles"));
			GenotypesContext genotypescontext =
					Mutation.toGenotypesContext(rs
					.getString("alleles"));
			int startPoint = rs.getInt("startpoint");
			int endPoint = rs.getInt("endpoint");
			int positionGRCH37 = rs.getInt("GRCH37_pos");
			float cadd = rs.getFloat("CADD_score");
			float frequency = rs.getFloat("frequency");
			mutations.add(new Mutation(
					id, sort, rsID, chromosome, alleles,
					startPoint, endPoint, genotypescontext,
					positionGRCH37, cadd, frequency));
		}
		return mutations;
	}

	/**
	 * Gets the mutation score.
	 *
	 * @param m The mutation
	 *
	 * @return mutation score
	 *
	 * @throws SQLException In case SQL goes wrong
	 */
	@Override
	public final float getScore(final Mutation m) throws SQLException {
		return QueryProcessor.executeScoreQuery(m);
	}
	
	/**
	 * Gets the positions on a gene.
	 *
	 * @return Returns the list of positions
	 */
	@Override
	public HashMap<String, ArrayList<Integer>> getPositions(Mutation m) throws SQLException {
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		
		String query = "SELECT * FROM genes WHERE chromosome='"
				+ m.getChromosome()
				+ "' ORDER BY ABS((endpoint + startpoint)/2 - "
				+ m.getStart() + ") ASC LIMIT 5";
		
		Logger.info(query);
		ResultSet rs = Database.select("data", query);
		
		while (rs.next()) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(rs.getInt("startpoint"));
			list.add(rs.getInt("endpoint"));
			map.put(rs.getString("name"),list);
		}

		return map;
	}
}
