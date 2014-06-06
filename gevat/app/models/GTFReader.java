package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GTFReader {

	/**
	 * Open connection to database and read file
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, SQLException, ClassNotFoundException {

		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://web398.webfaction.com/programminglife",
				"programminglife", "ikgaorasstemmen");

		readFile("homo_sapiens.gtf");
		connection.close();
	}

	/**
	 * Read file per line and add to db
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void readFile(String filename) throws FileNotFoundException,
			IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			// Omit the beginning of the file
			for (int i = 0; i < 5; i++) {
				br.readLine();
			}

			// Read file and add to DB
			String line;
			while ((line = br.readLine()) != null) {
				formatString(line);
			}
		}
	}

	/**
	 * Add each line as a gene to the db in correct format
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
					geneId = attribute.split(" ")[1].replace("\"", "").replace(
							"ENSG", "");
				}
				if (attribute.contains("gene_name")) {
					geneName = attribute.trim().split(" ")[1].replace("\"", "");
				}
			}

			return "(" + geneId + ", '" + geneName + "', " + list[3] + ", "
					+ list[4] + ")";
		}
		return null;
	}

}
