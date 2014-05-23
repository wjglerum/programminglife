package test.models;



import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import models.Mutation;

import org.junit.Test;

public class MutationTest {

	private Mutation m = new Mutation(1, "SNP", "rsID", 1, new char[] { 'A',
			'T', 'T', 'T', 'T', 'T' });

	@Test
	public void testConstructor() {
		assertEquals(m.id, 1);
		assertEquals(m.sort, "SNP");
		assertEquals(m.rsID, "rsID");
		assertEquals(m.chromosome, 1);
		assertArrayEquals(m.alleles,
				new char[] { 'A', 'T', 'T', 'T', 'T', 'T' });
	}

	@Test
	public void testAlleles() {
		assertEquals(m.child(), "[A, T]");
		assertEquals(m.father(), "[T, T]");
		assertEquals(m.mother(), "[T, T]");
	}
}
