package test.models;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import models.Mutation;
import models.VCFReader;

import java.io.IOException;

import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.vcf.VCFCodec;

import java.util.Iterator;

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

	private VariantContext correct1;
	private VariantContext correct2;
	private VariantContext wrong1;
	private VariantContext wrong2;
	private VariantContext correct3;
	
	@Before
	public void Before()
	{
		FeatureReader reader = AbstractFeatureReader
				.getFeatureReader(testFile, new VCFCodec(),
						false);
		try {
			Iterator<VariantContext> it = reader.iterator();
			reader.getHeader();
			correct1 = it.next();
			correct2 = it.next();
			wrong1 = it.next();
			wrong2 = it.next();
			correct3 = it.next();
			reader.close();
		} catch (IOException e) {fail("IOException");}
	}
	
	@Test
	public void testGetMutations() {
//		VCFReader.getMutations(testFile);
		fail("TODO");
	}

	@Test
	public void testHasMutation() {
		assertFalse(VCFReader.hasMutation(correct1));
		assertFalse(VCFReader.hasMutation(correct2));
		assertFalse(VCFReader.hasMutation(correct3));
		assertTrue(VCFReader.hasMutation(wrong1));
		assertTrue(VCFReader.hasMutation(wrong2));
	}

	@Test
	public void testToMutation() {
		Mutation m = VCFReader.toMutation(wrong1, "SNP");
		assertEquals("SNP", m.getMutationType());
	}

	@Test
	public void testPossibleAlleleSet() {
		fail("TODO");
	}
}
