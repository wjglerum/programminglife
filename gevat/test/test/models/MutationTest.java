package test.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.mutation.Mutation;
import models.mutation.MutationRepository;
import models.mutation.MutationService;

import org.broadinstitute.variant.variantcontext.Allele;
import org.junit.Before;
import org.junit.Test;

public class MutationTest {

	private Mutation m;
	private static int id = 1;
	private static String mutationType = "SNP";
	private static String rsID = "rsID";
	private static String chromosome = "1";
	private static char[] alleles = { 'A', 'T', 'T', 'T', 'T', 'T' };
	private static int startPoint = 1;
	private static int endPoint = 2;
	private static int position = 3;
	private static float cadd = 2;
	private static float frequency = 0;

	private final MutationRepository repositoryMock = mock(MutationRepository.class);
	private final MutationService mutationService = new MutationService(
			repositoryMock);

	@Before
	public void setUp() {
		m = new Mutation(id, mutationType, rsID, chromosome, alleles,
				startPoint, endPoint, position, cadd, frequency);
	}

	@Test
	public void testConstructor() {
		assertEquals(m.getId(), id);
		assertEquals(m.getMutationType(), mutationType);
		assertEquals(m.getRsID(), rsID);
		assertEquals(m.getChromosome(), chromosome);
		assertEquals(m.getStartPoint(), startPoint);
		assertEquals(m.getEndPoint(), endPoint);
		assertEquals(m.getPositionGRCH37(), position);
	}

	@Test
	public void testGetUnigueBase() {
		assertEquals(m.getUniqueBase(), "A");
	}

	@Test
	public void testToBaseString() {
		List<Allele> list = new ArrayList<Allele>();
		for (char allele : alleles) {
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

}
