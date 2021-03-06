package test.models.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.mutation.Mutation;
import models.reader.VCFReader;

import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.junit.Before;
import org.junit.Test;

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
	public void before() {
		correct = new ArrayList<VariantContext>();
		wrong = new ArrayList<VariantContext>();
		FeatureReader<VariantContext> reader = AbstractFeatureReader
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
		} catch (IOException e) { fail("IOException"); }
	}

	@Test
	public void testHasMutation() {
		for (VariantContext vc: correct) {
			assertFalse(VCFReader.hasMutation(vc));
		}
		for (VariantContext vc: wrong) {
			assertTrue(VCFReader.hasMutation(vc));
		}
	}

	@Test
	public void testToMutation() {
		Mutation m = VCFReader.toMutation(wrong.get(0), "SNP");
		assertEquals("SNP", m.getMutationType());
	}
}
