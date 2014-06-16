package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;

import models.protein.Protein;
import models.protein.ProteinRepository;
import models.protein.ProteinService;

import org.junit.Before;
import org.junit.Test;

public class ProteinTest {

	private Protein p;
	private final String name = "name";
	private final String disease = "disease";
	private final ProteinRepository repositoryMock = mock(ProteinRepository.class);
	private final ProteinService proteinService = new ProteinService(
			repositoryMock);

	@Before
	public void setUp() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(disease);
		p = new Protein(name, list);
	}

	/**
	 * Tests the constructor of protein.
	 */
	@Test
	public void testConstructor() {
		assertEquals(name, p.getName());
		assertEquals(disease, p.getDisease());
	}

	/**
	 * Tests the equals function of protein.
	 */
	@Test
	public void testEquals() {
		ArrayList<String> list = new ArrayList<String>();
		Protein p = new Protein("name", list);
		Protein p2 = new Protein("name", list);
		Protein q = new Protein("othername", list);
		assertTrue(p.equals(p));
		assertTrue(p.equals(p2));
		assertFalse(p.equals(q));
		assertTrue(p.equals("name"));
		assertFalse(p.equals("othername"));
		assertFalse(p.equals(0));
	}

	@Test
	public void testAddConnection() {
		// TODO
	}

	@Test
	public void testHasConnection() {
		// TODO
	}

	@Test
	public void testGetAnnotations() throws SQLException {
		String annotation = "annotation";
		when(repositoryMock.getAnnotations(name)).thenReturn(annotation);

		String res = proteinService.getAnnotations(name);
		assertEquals(res, annotation);
		verify(repositoryMock).getAnnotations(name);
	}
}
