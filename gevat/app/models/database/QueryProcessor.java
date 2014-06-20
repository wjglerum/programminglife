package models.database;

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

import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;

import models.mutation.Mutation;
import models.mutation.MutationService;
import play.Logger;
import play.db.DB;

/**
 * This class processes queries and passes them on to Database.
 * 
 * @author rbes
 * 
 */
public class QueryProcessor {

    private static PreparedStatement findRelatedProteins;
    private static PreparedStatement getConnectedProteinScore;
    private static PreparedStatement getFrequency;
    private static PreparedStatement getAnnotationsOfProtein;
    private static PreparedStatement findGenesAssociatedWithSNP;
    private static PreparedStatement executeScoreQuery;
    private static PreparedStatement getSNPFunction;
    private static PreparedStatement listMutatedProteins;
    private static PreparedStatement insertMutation;

    /**
     * Done because it is a utility-class.
     */
    public QueryProcessor() {
        try {
            prepareQueries();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param database
     * @throws IOException
     *             When queries are not found.
     */
    public static void prepareQueries() throws IOException {
        try (Connection connection = DB.getConnection("string");) {
            findRelatedProteins = prepareQuery("findRelatedProteins",
                    connection);
            getConnectedProteinScore = prepareQuery("getConnectedProteinScore",
                    connection);
            getAnnotationsOfProtein = prepareQuery("getAnnotationsOfProtein",
                    connection);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
        try (Connection connection2 = DB.getConnection("snp");) {
            findGenesAssociatedWithSNP = prepareQuery(
                    "findGenesAssociatedWithSNP", connection2);
            getFrequency = prepareQuery("getFrequency", connection2);
            getSNPFunction = prepareQuery("getSNPFunction", connection2);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
        try (Connection connection3 = DB.getConnection("score");) {
            executeScoreQuery = prepareQuery("executeScoreQuery", connection3);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
        try (Connection connection4 = DB.getConnection("data");) {
        	listMutatedProteins = prepareQuery("listMutatedProteins",
        			connection4);
        	insertMutation = prepareQuery("insertMutation", connection4);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
    }

    /**
     * 
     * @param queryName
     *            Name of the query (Make sure the sql file has the same name!).
     * @param connection
     *            Name of the connection.
     * @return String A query string
     * @throws IOException
     *             Turns up when the query can not be found.
     * @throws SQLException
     *             In case the query has a bug.
     */
    public static PreparedStatement prepareQuery(String queryName,
            Connection connection) throws IOException, SQLException {
        String path = "private/sql/queryprocessor/" + queryName + ".sql";
        return connection.prepareStatement(new String(Files.readAllBytes(Paths
                .get(path))));
    }

    /**
     * Filters a hashmap with mutations based on their snp allele frequency.
     * 
     * @param hm
     *            HashMap that has uses a mutation ID as key for a mutation
     *            object.
     * @param list The splitted list
     *
     * @return An ArrayList<Mutation> with mutations that had a frequency that
     *         is low enough.
     *
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public static ArrayList<Mutation> filterOnFrequency(
            final HashMap<Integer, Mutation> hm, final ArrayList<ArrayList<Mutation>> list)
                    throws SQLException {
        ArrayList<Mutation> output = new ArrayList<Mutation>();
        int counter = 0, counter2 = 0;
        final int split = 10000;
        char[] allele = {'A', 'T', 'C', 'G'};
        for (ArrayList<Mutation> ml : list) {
            counter = 0;
            String q = "SELECT DISTINCT snp_id, allele, chr_cnt, "
                    + "freq FROM snpallelefreq join allele "
                    + "ON "
                    + "snpallelefreq.allele_id = "
                    + "allele.allele_id "
                    + "WHERE snp_id IN (";
            for (Mutation m : ml) {
                String[] idAsString = m.getID().split(";");
                int id = Integer.parseInt(idAsString[0]
                        .substring(2));
                q += id + ",";
                if (++counter % split == 0 || counter
                        == ml.size() - 1) {
                    q = q.substring(0, q.length() - 1);
                    q += ") AND allele = '"
                            + allele[counter2]
                            + "' AND freq < 0.005 "
                            + "AND freq > 0;";
                    ResultSet rs = Database.select(
                            "snp", q);
                    q = "SELECT DISTINCT snp_id, allele, "
                            + "chr_cnt, freq FROM "
                            + "snpallelefreq "
                            + "join allele "
                            + "ON snpallelefreq."
                            + "allele_id "
                            + "= allele.allele_id "
                            + "WHERE "
                            + "snp_id IN (";
                    while (rs.next()) {
                        ArrayList<String> geneList =
                                QueryProcessor.findGenesAssociatedWithSNP(Integer
                                .parseInt(
                                rs.getString(
                                "snp_id")));
                        if (!geneList.isEmpty()) {
                            output.add(hm.get(Integer
                                    .parseInt(
                                    rs.getString(
                                    "snp_id"))));                           
                        }
                    }
                }
            }
        }
        return output;
    }

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
     * @throws IOException
     */
    public static ArrayList<String> findRelatedProteins(final String proteine,
            final int limit, final int threshold) throws SQLException {
        ArrayList<String> list = new ArrayList<String>();
        assert (limit > 0) : "Needs at least 1";
        assert (threshold >= 0) : "Needs at least 0";
        int counter = 1;
        findRelatedProteins.setString(counter++, proteine);
        findRelatedProteins.setInt(counter++, threshold);
        findRelatedProteins.setInt(counter++, limit);
        ResultSet rs = findRelatedProteins.executeQuery();
        while (rs.next()) {
            String codeName = rs.getString("preferred_name");
            int score = rs.getInt("combined_score");
            list.add(codeName.replaceAll(",", "") + "\t" + score);
        }
        return list;
    }

    /**
     * Gets the connections and scores between all proteins in the
     * ArrayList<String>.
     * 
     * @param proteins
     *            The ArraList<String> containing the protein names
     * 
     * @return Returns the list of connections that look like this: proteinA ->
     *         proteinB = score
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public static ArrayList<String> getConnectedProteinScore(
            final Collection<String> proteins) throws SQLException {
        String formatted = formatForIN(proteins);
        ArrayList<String> list = new ArrayList<String>();

        if (formatted.length() == 0) {
            return list;
        }
        
        String query = getConnectedProteinScore.toString().replaceAll("\\?", formatted);
        
        ResultSet rs = Database.select("string", query);

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
     * Formats the stringList to be used in a 'IN' query.
     * 
     * @param proteins
     *            The list of strings to be formatted
     * 
     * @return Returns the formatted String
     */
    public static String formatForIN(final Collection<String> proteins) {
        String formatted = "";
        int i = 0;

        for (String protein : proteins) {
            if (i > 0) {
                formatted += ",";
            }

            formatted += "'" + protein + "'";

            i++;
        }

        return formatted;
    }

    /**
     * Finds the score.
     * 
     * @param chr The chromome the mutation is on
     * @param positionGRCH37 The position according to GRCH37
     * 
     * @return Returns a ResultSet containing the score
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public ResultSet executeScoreQuery(final String chr,
    		final int positionGRCH37) throws SQLException {
        executeScoreQuery.setString(1, chr);
        executeScoreQuery.setInt(2, positionGRCH37);
        // Get all the records from database score that have mutations
        // on the
        // same position as our position
        return executeScoreQuery.executeQuery();
    }

    public static void insertMutation(int patientId, String mutationType,
    		String rsId, String chromosome, String alleles, int start,
    		int end, int position, float score, float frequency)
			throws SQLException {
    			int i = 1;
		        insertMutation.setInt(i++, patientId);
		        insertMutation.setString(i++, mutationType);
		        insertMutation.setString(i++, rsId);
		        insertMutation.setString(i++, chromosome);
		        insertMutation.setString(i++, alleles);
		        insertMutation.setInt(i++, start);
		        insertMutation.setInt(i++, end);
		        insertMutation.setInt(i++, position);
		        insertMutation.setFloat(i++, score);
		        insertMutation.setFloat(i++, frequency);
		        insertMutation.executeUpdate();
	}

	/**
     * Gets the snp allele frequency of a mutation.
     * 
     * @param idString The id of the mutation
     * @param base The base
     * @return float Returns the frequency
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public static float getFrequency(final String idString, final String base)
            throws SQLException {
        String[] idAsString = idString.split(";");
        // Get variables for query
        int id = Integer.parseInt(idAsString[0].substring(2));
        // Insert variables in query
        getFrequency.setInt(1, id);
        getFrequency.setString(2, base);
        // Execute query
        ResultSet rs = getFrequency.executeQuery();
        while (rs.next()) {
            return rs.getFloat("freq");
        }
        return 0;
    }

    /**
     * Gets the annotation of a protein.
     * 
     * @param protein
     *            The preferred name of the protein
     * 
     * @return Returns the annotation of the protein
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public static String getAnnotationsOfProtein(final String protein)
            throws SQLException {
        String s = "";
        getAnnotationsOfProtein.setString(1, protein);
        ResultSet rs = getAnnotationsOfProtein.executeQuery();
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
        findGenesAssociatedWithSNP.setInt(1, id);
        ResultSet rs = findGenesAssociatedWithSNP.executeQuery();
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
     * 
     * @return Returns an ArrayList<String> containing the gene name, with the
     *         names and scores of connected genes
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     * @throws IOException
     *             In case the query can not be found.
     */
    public static ArrayList<String> findGenes(final int id, final int limit,
            final int threshold) throws SQLException, IOException {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> qResult = QueryProcessor
                .findGenesAssociatedWithSNP(id);
        for (String gene : qResult) {
            list.add(gene);
            list.add(QueryProcessor.findRelatedProteins(gene, limit, threshold)
                    .toString());
        }
        return list;
    }

    /**
     * Finds the connections between genes, based on the SNP.
     * 
     * @param id
     *            The id of the SNP
     * @param limit
     *            The maximum amount of genes retrieved
     * @param threshold
     *            The minimum value of the bonds
     * 
     * @return Returns a String containing the connections
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     * @throws IOException
     *             In case the query can not be found.
     */
    public static String findGeneConnections(final int id, final int limit,
            final int threshold) throws SQLException, IOException {
        ArrayList<String> qResult = QueryProcessor
                .findGenesAssociatedWithSNP(id);
        if (!qResult.isEmpty()) {
            return findGeneConnections(qResult.get(0), limit, threshold);
        }
        return "";
    }

    /**
     * Finds the connections between genes.
     * 
     * @param p1
     *            The name of the gene
     * @param limit
     *            The maximum amount of genes retrieved
     * @param threshold
     *            The minimum value of the bonds
     * 
     * @return Returns the String containing the connections
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     * @throws IOException IO Exception
     */
    public static String findGeneConnections(final String p1, final int limit,
            final int threshold) throws SQLException, IOException {
        return QueryProcessor.findRelatedProteins(p1, limit, threshold)
                .toString();
    }

    /**
     * Gets the function of a SNP. Informs what it changes.
     * @param id The id of the SNP
     *
     * @return Returns a list containing strings with information
     *
     * @throws SQLException In case SQL goes wrong
     * @throws IOException In case the .sql cannot be found
     */
    public static ArrayList<String> getSNPFunction(final int id)
    		throws SQLException, IOException {
        ArrayList<String> list = new ArrayList<String>();
        getSNPFunction.setInt(1, id);
        ResultSet rs = findGenesAssociatedWithSNP.executeQuery();
        while (rs.next()) {
            list.add(rs.getInt("snp_id") + " " + rs.getString("abbrev")
            		+ " " + rs.getString("descrip") + " " + rs.getBoolean("is_coding")
            		+ " " + rs.getBoolean("is_exon"));
        }
        return list;
    }

    /**
     * @param patientId Id of the patient
     * @param p1 The first protein (with mutation)
     * @param p2 The second protein
     * @param combinedScore The combined score or threshold of the two proteins
     */
	public static void insertConnectionIntoDB(int patientId, String p1, String p2, int combinedScore)	{
		String query = "INSERT INTO protein_connections VALUES ("
			+ patientId
			+ ",'"
			+ p1
			+ "','"
			+ p2
			+ "',"
			+ combinedScore
			+ ");";
		// Logger.info(query);
		Database.insert("data", query);
	}
	
	public static Collection<String> listMutatedProteins(int patientId) throws SQLException
	{
		listMutatedProteins.setInt(1, patientId);
		ResultSet rs = listMutatedProteins.executeQuery();
		Collection<String> list = new ArrayList<String>();
		while(rs.next()) {
			list.add(rs.getString("protein_a_id"));
		}
		return list;
	}
	
	public static ResultSet getOtherConnectedMutatedProteins(int patientId, Collection<String> currMutation, Collection<String> proteins) throws SQLException
	{
		String query = "SELECT * "
				+ "FROM protein_connections "
				+ "WHERE p_id = "
				+ patientId
				+ " AND protein_a_id NOT IN ( "
				+ formatForIN(currMutation)
				+ " ) AND protein_b_id IN ( "
				+ formatForIN(proteins)
				+ " );";
			return Database.select("data", query);
	}
}
