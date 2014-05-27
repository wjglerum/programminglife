package test.models;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import models.VCFReader;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.vcf.VCFCodec;

import org.junit.Test;
import org.junit.Before;

import play.Logger;
import scalaz.std.list;

public class VCFReaderTest {
	/*
	 * testFile contains a vcf encoding of the following genotypes:
	 * FATHER	MOTHER	DAUGHTER
	 * TC	TC	TC
	 * GA	GG	GA
	 * AA	AA	GA
	 * GG	GG	GA
	 * AA	AA	AA
	 */
	private String testFile = "test/testFiles/VCFTest.vcf";
	
	@Before
	public void Before()
	{
		FeatureReader reader = AbstractFeatureReader
				.getFeatureReader(testFile, new VCFCodec(),
						false);
	}
	
	@Test
	public void testGetMutations() {
		VCFReader.getMutations(testFile);
		fail("TODO");
	}

	@Test
	public void testHasMutation() {
		fail("TODO");
	}

	@Test
	public void testToMutation() {
		fail("TODO");
	}

	@Test
	public void testPossibleAlleleSet() {
		fail("TODO");
	}
}
