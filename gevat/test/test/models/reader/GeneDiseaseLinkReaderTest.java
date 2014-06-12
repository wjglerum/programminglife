package test.models.reader;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import models.reader.GeneDiseaseLinkReader;

import org.junit.Test;

/**
 * A test suite for GeneDiseaseLinkReader.
 *
 * @author rbes
 *
 */
public class GeneDiseaseLinkReaderTest {

	/**
	 * Tests the function findGeneDiseaseAssociation with normal input.
	 *
	 * @throws IOException In case reading the file goes wrong
	 */
	@Test
	public final void testFindGeneDiseaseAssociation() throws IOException {
		ArrayList<String> result = GeneDiseaseLinkReader.
				findGeneDiseaseAssociation("A2M");
		String shouldBe = "[Alzheimer's disease,"
				+ " Alpha-2-macroglobulin deficiency]";
		assertTrue(shouldBe.equals(result.toString()));
	}

	/**
	 * Tests the function findGeneDiseaseAssociation
	 * with input it does not contain.
	 *
	 * @throws IOException In case reading the file goes wrong
	 */
	@Test
	public final void testEmptyFindGeneDiseaseAssociation()
			throws IOException {
		ArrayList<String> result =
				GeneDiseaseLinkReader.
				findGeneDiseaseAssociation("XXX");
		assertTrue(result.isEmpty());
	}

}
