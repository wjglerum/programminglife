package test.models;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;
import models.application.VCFReader;
import models.dna.Mutation;

import java.io.IOException;

import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.vcf.VCFCodec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	private ArrayList<VariantContext> correct;
	private ArrayList<VariantContext> wrong;
	
	@Before
	public void Before()
	{
		correct = new ArrayList<VariantContext>();
		wrong = new ArrayList<VariantContext>();
		FeatureReader reader = AbstractFeatureReader
				.getFeatureReader(testFile, new VCFCodec(),
						false);
		try {
			Iterator<VariantContext> it = reader.iterator();
			reader.getHeader();
			correct.add(it.next());
			correct.add(it.next());
			wrong.add(it.next());
			wrong.add(it.next());
			correct.add(it.next());
			reader.close();
		} catch (IOException e) {fail("IOException");}
	}
	
	@Test
	public void testGetMutations() {
		List<Mutation> mutations = VCFReader.getMutations(testFile);
		assertEquals(mutations.size(),wrong.size());
		for(int i=0; i<mutations.size(); i++)
				assertEquals(mutations.get(i).toString(), wrong.get(i).toString());
	}

	@Test
	public void testHasMutation() {
		for(VariantContext vc: correct)
			assertFalse(VCFReader.hasMutation(vc));
		for(VariantContext vc: wrong)
			assertTrue(VCFReader.hasMutation(vc));
	}

	@Test
	public void testToMutation() {
		Mutation m = VCFReader.toMutation(wrong.get(0), "SNP");
		assertEquals("SNP", m.getMutationType());
	}
}
