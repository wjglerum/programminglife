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
import java.util.Map.Entry;

import models.mutation.Mutation;
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
            getFrequency = prepareQuery("getFrequency", connection);
            getAnnotationsOfProtein = prepareQuery("getAnnotationsOfProtein",
                    connection);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
        try (Connection connection2 = DB.getConnection("snp");) {
            findGenesAssociatedWithSNP = prepareQuery(
                    "findGenesAssociatedWithSNP", connection2);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
        try (Connection connection3 = DB.getConnection("score");) {
            executeScoreQuery = prepareQuery("executeScoreQuery", connection3);
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
        String path = "public/sql/queryprocessor/" + queryName + ".sql";
        return connection.prepareStatement(new String(Files.readAllBytes(Paths
                .get(path))));
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
        findRelatedProteins.setString(1, proteine);
        findRelatedProteins.setInt(2, threshold);
        findRelatedProteins.setInt(3, limit);
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
        getConnectedProteinScore.setString(1, formatted);
        getConnectedProteinScore.setString(2, formatted);
        ResultSet rs = getConnectedProteinScore.executeQuery();

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
     * @param mutation
     *            The mutation for which to find the score
     * 
     * @return Returns a list containing the score
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public float executeScoreQuery(final Mutation mutation) throws SQLException {
        executeScoreQuery.setString(1, mutation.getChr());
        executeScoreQuery.setInt(2, mutation.getPositionGRCH37());
        // Get all the records from database score that have mutations
        // on the
        // same position as our position
        ResultSet rs = executeScoreQuery.executeQuery();

        // If the mutation is the same value as the reference, return 0
        while (rs.next()) {
            String alt = rs.getString("alt");
            float phred = rs.getFloat("phred");
            String a = mutation.getAlleles().get(0).getBaseString();
            String b = mutation.getAlleles().get(1).getBaseString();
            if (alt.equals(a) || alt.equals(b)) {

                return phred;
            }
        }
        return 0;
    }

    /**
     * Gets the snp allele frequency of a mutation.
     * 
     * @param mutation
     *            The mutation for which to find the frequency
     * 
     * @return float Returns the frequency
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public static float getFrequency(final Mutation mutation)
            throws SQLException {
        String[] idAsString = mutation.getID().split(";");
        // Get variables for query
        int id = Integer.parseInt(idAsString[0].substring(2));
        String base = mutation.getAlleles().get(0).getBaseString();
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
     * Filters a hashmap with mutations based on their snp allele frequency.
     * 
     * @param hm
     *            HashMap that has uses a mutation ID as key for a mutation
     *            object.
     * @return An ArrayList<Mutation> with mutations that had a frequency that
     *         is low enough.
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public static ArrayList<Mutation> filterOnFrequency(
            final HashMap<Integer, Mutation> hm) throws SQLException {
        ArrayList<Mutation> output = new ArrayList<Mutation>();
        // SPLIT THE LIST INTO LISTS WITH A, T, C OR G
        ArrayList<Mutation> mutationListPartA = new ArrayList<Mutation>();
        ArrayList<Mutation> mutationListPartT = new ArrayList<Mutation>();
        ArrayList<Mutation> mutationListPartC = new ArrayList<Mutation>();
        ArrayList<Mutation> mutationListPartG = new ArrayList<Mutation>();

        for (Entry<Integer, Mutation> m : hm.entrySet()) {
            switch (m.getValue().child().charAt(1)) {
            case 'A':
                mutationListPartA.add(m.getValue());
                break;
            case 'T':
                mutationListPartT.add(m.getValue());
                break;
            case 'C':
                mutationListPartC.add(m.getValue());
                break;
            case 'G':
                mutationListPartG.add(m.getValue());
                break;
            default:
                break;
            }
        }
        int counter = 0;
        int counter2 = 0;
        int addCounter = 0;
        final int split = 10000;
        char[] allele = { 'A', 'T', 'C', 'G' };
        ArrayList<ArrayList<Mutation>> list = new ArrayList<ArrayList<Mutation>>();
        list.add(mutationListPartA);
        list.add(mutationListPartT);
        list.add(mutationListPartC);
        list.add(mutationListPartG);

        for (ArrayList<Mutation> ml : list) {
            counter = 0;
            String q = "SELECT DISTINCT snp_id, allele, chr_cnt, "
                    + "freq FROM snpallelefreq join allele " + "ON "
                    + "snpallelefreq.allele_id = " + "allele.allele_id "
                    + "WHERE snp_id IN (";
            for (Mutation m : ml) {
                String[] idAsString = m.getID().split(";");
                int id = Integer.parseInt(idAsString[0].substring(2));
                q += id + ",";
                if (++counter % split == 0 || counter == ml.size() - 1) {
                    q = q.substring(0, q.length() - 1);
                    q += ") AND allele = '" + allele[counter2]
                            + "' AND freq < 0.005 " + "AND freq > 0;";
                    ResultSet rs = Database.select("snp", q);
                    q = "SELECT DISTINCT snp_id, allele, "
                            + "chr_cnt, freq FROM " + "snpallelefreq "
                            + "join allele " + "ON snpallelefreq."
                            + "allele_id " + "= allele.allele_id " + "WHERE "
                            + "snp_id IN (";
                    while (rs.next()) {
                        addCounter++;
                        output.add(hm.get(Integer.parseInt(rs
                                .getString("snp_id"))));
                    }
                }
            }
        }

        Logger.info("Added " + addCounter + " recessive homozygous mutations.");
        return output;
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
     * @throws IOException
     */
    public static String findGeneConnections(final String p1, final int limit,
            final int threshold) throws SQLException, IOException {
        return QueryProcessor.findRelatedProteins(p1, limit, threshold)
                .toString();
    }
}
