package models.reader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GTFReader {

	/**
	 * Open connection to database and read file.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, SQLException, ClassNotFoundException {
		readFile("homo_sapiens.gtf");
	}

	/**
	 * Read file per line and build query and send to DB in batches.
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void readFile(String filename) throws FileNotFoundException,
			IOException, ClassNotFoundException, SQLException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			// Omit the beginning of the file
			for (int i = 0; i < 5; i++) {
				br.readLine();
			}

			// Read file and add to DB
			String line;
			String query = "INSERT INTO genes VALUES ";
			int counter = 0;
			int total = 0;
			while ((line = br.readLine()) != null) {
				String s = formatString(line);
				if (s != null) {
					query += s;
					query += ", ";
					counter++;
				}
				if (counter == 10000) {
					query = query.substring(0, query.length() - 2);
					query += ";";
					doQuery(query);
					total += 10000;
					System.out.println("Added " + total
							+ " records to database!");
					query = "INSERT INTO genes VALUES ";
					counter = 0;
				}
			}
			query = query.substring(0, query.length() - 2);
			query += ";";
			doQuery(query);
		}
	}

	/**
	 * Add each line as a gene to the query string in correct format.
	 * 
	 * @param line
	 */
	public static String formatString(String line) {
		String[] list = line.split("\t");
		if (list[2].equals("gene")) {
			String attributes[] = list[list.length - 1].split(";");

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

			return "('" + geneId + "', '" + geneName + "', " + list[3] + ", "
					+ list[4] + ", '" + list[0] + "')";
		}
		return null;
	}

	/**
	 * Open connection and execute query.
	 * 
	 * @param query
	 * @throws ClassNotFoundException
	 * @throws SQLException
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
