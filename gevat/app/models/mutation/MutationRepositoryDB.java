package models.mutation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import play.db.DB;

/**
 * The repository containing all database related functions.
 */
public class MutationRepositoryDB implements MutationRepository {

    private static PreparedStatement getAll;
    private static PreparedStatement get;
    private static QueryProcessor qp;

    /**
     * Constructor.
     */
    public MutationRepositoryDB() {
        qp = new QueryProcessor();
        try {
            prepareQueries("data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public final List<Mutation> getMutations(final int pId) throws SQLException {
        getAll.setInt(1, pId);
        ResultSet rs = getAll.executeQuery();
        List<Mutation> mutations = new ArrayList<Mutation>();

        while (rs.next()) {
            int id = rs.getInt("m_id");
            String sort = rs.getString("sort");
            String rsID = rs.getString("rsID");
            String chromosome = rs.getString("chromosome");
            Collection<Allele> alleles = Mutation.toAlleleCollection(rs
                    .getString("alleles"));
            GenotypesContext genotypescontext = Mutation.toGenotypesContext(rs
                    .getString("alleles"));
            int startPoint = rs.getInt("startpoint");
            int endPoint = rs.getInt("endpoint");
            int positionGRCH37 = rs.getInt("GRCH37_pos");
            float cadd = rs.getFloat("CADD_score");
            float frequency = rs.getFloat("frequency");
            mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
                    startPoint, endPoint, genotypescontext, positionGRCH37,
                    cadd, frequency));
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
    public final List<Mutation> getMutations(final int pId, final String cId)
            throws SQLException {
        get.setInt(1, pId);
        get.setString(2, cId);
        ResultSet rs = get.executeQuery();
        List<Mutation> mutations = new ArrayList<Mutation>();

        while (rs.next()) {
            int id = rs.getInt("m_id");
            String sort = rs.getString("sort");
            String rsID = rs.getString("rsID");
            String chromosome = rs.getString("chromosome");
            Collection<Allele> alleles = Mutation.toAlleleCollection(rs
                    .getString("alleles"));
            GenotypesContext genotypescontext = Mutation.toGenotypesContext(rs
                    .getString("alleles"));
            int startPoint = rs.getInt("startpoint");
            int endPoint = rs.getInt("endpoint");
            int positionGRCH37 = rs.getInt("GRCH37_pos");
            float cadd = rs.getFloat("CADD_score");
            float frequency = rs.getFloat("frequency");
            mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
                    startPoint, endPoint, genotypescontext, positionGRCH37,
                    cadd, frequency));
        }
        return mutations;
    }

    /**
     * Gets the mutation score.
     * 
     * @param m
     *            The mutation
     * 
     * @return mutation score
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    @Override
    public final float getScore(final Mutation m) throws SQLException {
        return qp.executeScoreQuery(m);
    }
	
	/**
	 * Gets the positions on a gene.
	 *
	 * @return Returns the list of positions
	 */
	@Override
	public HashMap<String, ArrayList<Integer>> getPositions(Mutation m, int amount) throws SQLException {
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		
		String query = "SELECT * FROM genes WHERE chromosome='"
				+ m.getChromosome()
				+ "' ORDER BY ABS((endpoint + startpoint)/2 - "
				+ m.getPositionGRCH37() + ") ASC LIMIT "
				+ amount
				+ ";";
		
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

//  void prepareQueries(String database) throws IOException;
    /**
     * 
     * @param database
     *            The name of the database that the queries use.
     * @throws IOException
     *             Gets thrown if the function isn't able to find the queries.
     */
    public void prepareQueries(final String database) throws IOException {
        String getAllQuery = new String(Files.readAllBytes(Paths
                .get("public/sql/mutations/getAll.sql")));
        String getQuery = new String(Files.readAllBytes(Paths
                .get("public/sql/mutations/get.sql")));
        try (Connection connection = DB.getConnection(database);) {
            getAll = connection.prepareStatement(getAllQuery);
            get = connection.prepareStatement(getQuery);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
    }
}
