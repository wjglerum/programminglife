package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class reads the GeneDiseaseInformation file.
 *
 * @author rbes
 *
 */
public final class GeneDiseaseLinkReader {

	/**
	 * Done because it is a utility-class.
	 */
	private GeneDiseaseLinkReader() {
		//not called
	}

	/**
	 * Reads the file and searches for the matching gene-name.
	 *
	 * @param gene The name of the gene you are looking for
	 *
	 * @return Returns an ArrayList<String> containing the found diseases
	 *
	 * @throws IOException In case reading the file goes wrong
	 */
	public static ArrayList<String> findGeneDiseaseAssociation(
			final String gene) throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader("private/"
						+ "GeneDiseaseInformation"));
		String line;
		ArrayList<String> result = new ArrayList<String>();

		while ((line = br.readLine()) != null) {
			String[] splitted = line.split("\t");
			if (splitted[1].equals(gene)) {
				result.add(splitted[3]);
			}
		}
		br.close();
		return result;
	}
}
