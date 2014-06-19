package models.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.mutation.Mutation;
import play.Logger;
import play.db.DB;

/**
 * Class that implementes all queries that are done with mutations.
 * 
 */
public class MutationQueries {

    private static PreparedStatement getAll;
    private static PreparedStatement get;
    private static PreparedStatement getPosition;
    private static PreparedStatement executeScoreQuery;

    /**
     * Prepares all queries and sets a database connection.
     */
    public MutationQueries() {
        try {
            prepareQueries();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all mutations.
     * 
     * @param pId
     *            Patient ID.
     * @return Result set with all the mutations.
     * @throws SQLException
     *             If the query fails.
     */
    public ResultSet getMutations(final int pId) throws SQLException {
        getAll.setInt(1, pId);
        return getAll.executeQuery();
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
     * @return Returns ResultSet.
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public ResultSet getMutations(final int pId, final String cId)
            throws SQLException {
        System.out.println("Query 1 uitgevoerd.");
        get.setInt(1, pId);
        get.setString(2, cId);
        return get.executeQuery();
    }

    /**
     * Finds the score.
     * 
     * @param chr
     *            The chromome the mutation is on
     * @param positionGRCH37
     *            The position according to GRCH37
     * 
     * @return Returns a ResultSet containing the score
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    public ResultSet executeScoreQuery(final String chr,
            final int positionGRCH37) throws SQLException {
        System.out.println("Query 2 uitgevoerd.");
        executeScoreQuery.setString(1, chr);
        executeScoreQuery.setInt(2, positionGRCH37);
        return executeScoreQuery.executeQuery();
    }

    /**
     * Gets the positions on a gene.
     * 
     * @param chromosome
     *            the chromosome.
     * @param position
     *            The mutation to find nearby mutations of
     * @param amount
     *            The amount of mutations to find
     * @return Returns the list of positions
     * @throws SQLException
     *             SQL Exception
     */
    public ResultSet getPositions(String chromosome, int position, int amount)
            throws SQLException {
        System.out.println("Query 3 uitgevoerd.");
        getPosition.setString(1, chromosome);
        getPosition.setInt(2, position);
        getPosition.setInt(3, amount);
        return getPosition.executeQuery();
    }

    /**
     * @throws IOException
     *             Gets thrown if the function isn't able to find the queries.
     */
    public void prepareQueries() throws IOException {
        System.out.println("Query 4 uitgevoerd.");
        try (Connection connection = DB.getConnection("data");) {
            getAll = QueryProcessing.generatePreparedStatement(
                    "mutations/getAll", connection);
            get = QueryProcessing.generatePreparedStatement("mutations/get",
                    connection);
            getPosition = QueryProcessing.generatePreparedStatement(
                    "mutations/getPosition", connection);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
        try (Connection connection2 = DB.getConnection("score");) {
            executeScoreQuery = QueryProcessing.generatePreparedStatement(
                    "mutations/executeScoreQuery", connection2);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
    }

}
