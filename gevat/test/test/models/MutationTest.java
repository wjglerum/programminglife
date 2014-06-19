package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.mutation.Mutation;
import models.mutation.MutationRepository;
import models.mutation.MutationService;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.Before;
import org.junit.Test;

public class MutationTest {

	private Mutation m;
	private Mutation m2;
    private Mutation m3;
    private Mutation m4;
	private static int id = 1;
	private static String mutationType = "SNP";
	private static String rsID = "rsID";
	private static String chromosome = "1";
	private static char[] alleles1 = { 'A', 'T', 'T', 'T', 'T', 'T' };
    private static char[] alleles2 = { 'T', 'A', 'T', 'T', 'T', 'T' };
    private static char[] alleles3 = { 'A', 'T', 'A', 'T', 'A', 'T' };
    private static char[] alleles4 = { 'A', 'A', 'A', 'T', 'T', 'T' };
	private static Collection<Allele> alleleCollection =
	        Mutation.toAlleleCollection(new String(alleles1));
	private static GenotypesContext genotypesContext =
	        Mutation.toGenotypesContext(new String(alleles1));
	private static int startPoint = 1;
	private static int endPoint = 2;
	private static int position = 3;
	private static float cadd = 2;
	private static float frequency = 0;

	private final MutationRepository repositoryMock =
	        mock(MutationRepository.class);
	private final MutationService mutationService = new MutationService(
			repositoryMock);

	@Before
	public void setUp() {
        m = new Mutation(id, mutationType , rsID, chromosome, alleleCollection,
                startPoint, endPoint, genotypesContext,
                position, cadd, frequency);
		m2 = new Mutation(id, mutationType, rsID, chromosome, alleles2,
				startPoint, endPoint, position, cadd, frequency);
        m3 = new Mutation(id, mutationType, rsID, chromosome, alleles3,
                startPoint, endPoint, position, cadd, frequency);
        m4 = new Mutation(id, mutationType, rsID, chromosome, alleles4,
                startPoint, endPoint, position, cadd, frequency);
	}

    @Test
    public void testFirstConstructor() {
        assertEquals(m.getId(), id);
        assertEquals(m.getMutationType(), mutationType);
        assertEquals(m.getRsID(), rsID);
        assertEquals(m.getChromosome(), chromosome);
        assertEquals(m.getStartPoint(), startPoint);
        assertEquals(m.getEndPoint(), endPoint);
        assertEquals(m.getPositionGRCH37(), position);        
    }

	@Test
	public void testSecondConstructor() {
		assertEquals(m2.getId(), id);
		assertEquals(m2.getMutationType(), mutationType);
		assertEquals(m2.getRsID(), rsID);
		assertEquals(m2.getChromosome(), chromosome);
		assertEquals(m2.getStartPoint(), startPoint);
		assertEquals(m2.getEndPoint(), endPoint);
		assertEquals(m2.getPositionGRCH37(), position);
	}

	@Test
	public void testGetUnigueBase() {
		assertEquals(m.getUniqueBase(), "A");
		assertEquals(m2.getUniqueBase(), "A");
        assertEquals(m3.getUniqueBase(), "");
        assertEquals(m4.getUniqueBase(), "A");
	}

	@Test
	public void testToBaseString() {
		List<Allele> list = new ArrayList<Allele>();
		for (char allele : alleles1) {
			list.add(Mutation.toAllele(allele + "", true));
		}
		assertEquals(m.toBaseString(list), "[A, T]");
	}

	@Test
	public void testToAllelesString() {
		assertEquals("ATTTTT", m.toAllelesString());
	}

	@Test
	public void testAlleles() {
		assertEquals("[A, T]", m.child());
		assertEquals("[T, T]", m.father());
		assertEquals("[T, T]", m.mother());
	}

	@Test
	public void testToAllele() {
		// TODO
	}

	@Test
	public void testToAlleleCollection() {
		// TODO
	}

	@Test
	public void testToGenotypeContext() {
		// TODO
	}

	@Test
	public void testToGenotype() {
		// TODO
	}

	@Test
	public void testGetPositions() {
		// TODO
	}

	@Test
	public void testGetMutations() throws SQLException {
		List<Mutation> list = new ArrayList<Mutation>();
		list.add(m);
		when(repositoryMock.getMutations(1)).thenReturn(list);

		List<Mutation> res = mutationService.getMutations(1);
		m = res.get(0);
		assertEquals(m.getId(), id);
		assertEquals(m.getMutationType(), mutationType);
		assertEquals(m.getRsID(), rsID);
		assertEquals(m.getChromosome(), chromosome);
		assertEquals(m.getStartPoint(), startPoint);
		assertEquals(m.getEndPoint(), endPoint);
		assertEquals(m.getPositionGRCH37(), position);

		verify(repositoryMock).getMutations(1);
	}

	@Test
	public void testGetMutationsPerChromosome() throws SQLException {
		List<Mutation> list = new ArrayList<Mutation>();
		list.add(m);
		when(repositoryMock.getMutations(1, "1")).thenReturn(list);

		List<Mutation> res = mutationService.getMutations(1, "1");
		m = res.get(0);
		assertEquals(m.getId(), id);
		assertEquals(m.getMutationType(), mutationType);
		assertEquals(m.getRsID(), rsID);
		assertEquals(m.getChromosome(), chromosome);
		assertEquals(m.getStartPoint(), startPoint);
		assertEquals(m.getEndPoint(), endPoint);
		assertEquals(m.getPositionGRCH37(), position);

		verify(repositoryMock).getMutations(1, "1");
	}

	@Test
	public void testGetScore() throws SQLException {
		float score = 1000;
		when(repositoryMock.getScore(m)).thenReturn(score);

		float res = mutationService.getScore(m);
		assertEquals(res, score, 0.00001);

		verify(repositoryMock).getScore(m);
	}

	@Test
    public void testSetGetScoreMutation() {
        assertEquals(m.getScore(), cadd, 0.0001);
	    final float newScore = 1;
        assertNotEquals(m.getScore(), newScore, 0.0001);
        m.setScore(newScore);
        assertEquals(m.getScore(), newScore, 0.0001);
    }
	
	@Test
	public void testGetSetFrequency() {
        assertEquals(m.getFrequency(), frequency, 0.0001);
        final float newFrequency = 1;
        assertNotEquals(m.getFrequency(), newFrequency, 0.0001);
        m.setFrequency(newFrequency);
        assertEquals(m.getFrequency(), newFrequency, 0.0001);	    
	}
}
