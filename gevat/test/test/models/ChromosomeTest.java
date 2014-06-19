package test.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import models.chromosome.Chromosome;

public class ChromosomeTest {

	private Chromosome c;
	private final String id = "1";

	@Before
	public void setUp() {
		c = new Chromosome(id);
	}

	@Test
	public void testConstructor() {
		assertEquals(id, c.getID());
	}
}
