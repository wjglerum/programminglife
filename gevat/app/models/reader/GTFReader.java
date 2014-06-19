package models.reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Reads Gene Transfer Format.
 */
public final class GTFReader {

	private GTFReader() {
	}
	
	/**
	 * Open connection to database and read file.
	 * 
	 * @param args unused
	 * @throws IOException IO Exception
	 * @throws SQLException SQL Exception
	 * @throws ClassNotFoundException Class not found Exception
	 */
	public static void main(String[] args) throws
			IOException, SQLException, ClassNotFoundException {
		readFile("homo_sapiens.gtf");
	}

	/**
	 * Read file per line and build query and send to DB in batches.
	 * 
	 * @param filename Name of the file
	 * @throws IOException IO Exception
	 * @throws SQLException SQL Exception
	 * @throws ClassNotFoundException Class not found Exception
	 */
	public static void readFile(String filename) throws
			IOException, ClassNotFoundException, SQLException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			final int linesToOmit = 5;
			final int maxQuerySize = 10000;
			for (int i = 0; i < linesToOmit; i++) {
				br.readLine();
			}

			// Read file and add to DB
			int total = 0;
			String query = "";
			while (br.ready()) {
				query = "INSERT INTO genes VALUES ";
				int counter = 0;
				total = readGenes(br, maxQuerySize, total, query, counter);
			}
			query = query.substring(0, query.length() - 2);
			query += ";";
			doQuery(query);
		}
	}

	private static int readGenes(BufferedReader br, final int maxQuerySize,
			int total, String query, int counter) throws IOException,
			ClassNotFoundException, SQLException {
		while (br.ready()) {
			String line = br.readLine();
			String s = formatString(line);
			if (s != null) {
				query += s;
				query += ", ";
				counter++;
			}
			if (counter == maxQuerySize) {
				query = query.substring(0, query.length() - 2);
				query += ";";
				doQuery(query);
				total += maxQuerySize;
				System.out.println("Added " + total
						+ " records to database!");
				return total;
			}
		}
		return total;
	}

	/**
	 * Add each line as a gene to the query string in correct format.
	 * 
	 * @param line One line
	 */
	private static String formatString(String line) {
		final int startpoint = 3;
		final int endpoint = 4;
		final int chromosome = 0;
		String[] list = line.split("\t");
		if (list[2].equals("gene")) {
			String[] attributes = list[list.length - 1].split(";");

			String geneId = null;
			String geneName = null;
			for (String attribute : attributes) {
				if (attribute.contains("gene_id")) {
					geneId = attribute.split(" ")[1].replace("\"", "");
				}
				if (attribute.contains("gene_name")) {
					geneName = attribute.trim().split(" ")[1].replace("\"", "");
				}
			}

			return "('" + geneId + "', '" + geneName + "', " + list[startpoint] + ", "
					+ list[endpoint] + ", '" + list[chromosome] + "')";
		}
		return null;
	}

	/**
	 * Open connection and execute query.
	 * 
	 * @param query The query
	 * @throws SQLException SQL Exception
	 * @throws ClassNotFoundException Class not found Exception
	 */
	public static void doQuery(String query) throws ClassNotFoundException,
			SQLException {
		Class.forName("org.postgresql.Driver");

		try (Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://web398.webfaction.com/programminglife",
				"programminglife", "ikgaorasstemmen");
				Statement statement = connection.createStatement();) {
			statement.execute(query);
		} catch (SQLException e) {
			System.out.println((e.toString()));
		}
	}
}
