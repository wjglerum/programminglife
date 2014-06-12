package test.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import models.mutation.Mutation;
import models.mutation.MutationRepository;
import models.mutation.MutationService;

import org.junit.Before;
import org.junit.Test;

public class MutationTest {

	private Mutation m;
	private static int id = 1;
	private static String mutationType = "SNP";
	private static String rsID = "rsID";
	private static int chromosome = 1;
	private static char[] alleles = { 'A', 'T', 'T', 'T', 'T', 'T' };
	private static int startPoint = 2;
	private static int endPoint = 2;
	private static int position = 3;

	private final MutationRepository repositoryMock = mock(MutationRepository.class);
	private final MutationService mutationService = new MutationService(
			repositoryMock);

	@Before
	public void setUp() {
		m = new Mutation(id, mutationType, rsID, chromosome, alleles,
				startPoint, endPoint, position);
	}

	@Test
	public void testConstructor() {
		assertEquals(m.getId(), id);
		assertEquals(m.getMutationType(), mutationType);
		assertEquals(m.getRsID(), rsID);
		assertEquals(m.getChromosome(), chromosome);
		//assertEquals(m.getStartPoint(), startPoint);
		//assertEquals(m.getEndPoint(), endPoint);
		assertEquals(m.getPositionGRCH37(), position);
	}

	@Test
	public void testAlleles() {
		assertEquals("[A, T]", m.child());
		assertEquals("[T, T]", m.father());
		assertEquals("[T, T]", m.mother());
	}

	@Test
	public void testToAllelesString() {
		assertEquals("ATTTTT", m.toAllelesString());
	}
}
