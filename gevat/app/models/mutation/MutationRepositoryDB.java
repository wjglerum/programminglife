package models.mutation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.database.Database;
import models.database.QueryProcessor;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypesContext;

public class MutationRepositoryDB implements MutationRepository {

	/**
	 * Retrieve a list of all the mutations of a patient.
	 * 
	 * @param pId
	 *            The id
	 * 
	 * @return Returns List<Mutation>, a list of all mutations
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@Override
	public List<Mutation> getMutations(final int pId) throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '" + pId + "';";
		ResultSet rs = Database.select("data", query);
		List<Mutation> mutations = new ArrayList<Mutation>();

		while (rs.next()) {
			int id = rs.getInt("m_id");
			String sort = rs.getString("sort");
			String rsID = rs.getString("rsID");
			int chromosome = rs.getInt("chromosome");
			Collection<Allele> alleles = Mutation.toAlleleCollection(rs
					.getString("alleles"));
			GenotypesContext genotypescontext = Mutation.toGenotypesContext(rs
					.getString("alleles"));
			int startPoint = rs.getInt("startpoint");
			int endPoint = rs.getInt("endpoint");
			int positionGRCH37 = rs.getInt("GRCH37_pos");
			mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
					startPoint, endPoint, genotypescontext, positionGRCH37));
		}
		return mutations;
	}

	/**
	 * Retrieve a list of all the mutations of a patient in a certain
	 * chromosome.
	 * 
	 * @param pId
	 *            The id
	 * @param cId
	 *            The chromosome number
	 * 
	 * @return Returns List<Mutation>, a list of all mutations in the
	 *         chromosome.
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	@Override
	public List<Mutation> getMutations(final int pId, final int cId)
			throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '" + pId
				+ "' and chromosome = '" + cId + "';";
		ResultSet rs = Database.select("data", query);
		List<Mutation> mutations = new ArrayList<Mutation>();

		while (rs.next()) {
			int id = rs.getInt("m_id");
			String sort = rs.getString("sort");
			String rsID = rs.getString("rsID");
			int chromosome = rs.getInt("chromosome");
			Collection<Allele> alleles = Mutation.toAlleleCollection(rs
					.getString("alleles"));
			GenotypesContext genotypescontext = Mutation.toGenotypesContext(rs
					.getString("alleles"));
			int startPoint = rs.getInt("startpoint");
			int endPoint = rs.getInt("endpoint");
			int positionGRCH37 = rs.getInt("GRCH37_pos");
			mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
					startPoint, endPoint, genotypescontext, positionGRCH37));
		}
		return mutations;
	}
	
	/**
	 * Gets the mutation score
	 * 
	 * @return mutation score
	 * @throws SQLException
	 */
	@Override
	public float getScore(Mutation m) throws SQLException {
		return QueryProcessor.executeScoreQuery(m);
	}
}
