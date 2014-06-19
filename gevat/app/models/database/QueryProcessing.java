package models.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class that is used to process queries by other classes in this
 * package.
 * 
 * @author mathijs
 * 
 */
public final class QueryProcessing {
    
    /**
     * Done because it is a utility-class.
     */
    private QueryProcessing() {
        
    }
    
    /**
     * Makes prepared statements.
     * 
     * @param queryName
     *            Name of the query.
     * @param connection
     *            Database connection that the prepared statement should be made
     *            for.
     * @return PreparedStatement
     * @throws IOException
     *             If the query can not be found.
     * @throws SQLException
     *             If the query can not be executed.
     */
    public static PreparedStatement generatePreparedStatement(String queryName,
            Connection connection) throws SQLException, IOException {
        String path = "private/sql/" + queryName + ".sql";
        return connection.prepareStatement(new String(Files.readAllBytes(Paths
                .get(path))));
    }
}
