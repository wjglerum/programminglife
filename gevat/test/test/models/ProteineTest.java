package test.models;

import models.Proteine;

import static org.junit.Assert.*;

import org.fest.assertions.AssertExtension;
import org.junit.Test;

public class ProteineTest {

	@Test
	public void testConstructor() {
		Proteine p = new Proteine("name");
		assertEquals("name", p.getName());
	}

	@Test
	public void testEquals() {
		Proteine p = new Proteine("name");
		Proteine p2 = new Proteine("name");
		Proteine q = new Proteine("othername");
		assertTrue(p.equals(p));
		assertTrue(p.equals(p2));
		assertFalse(p.equals(q));
		assertTrue(p.equals("name"));
		assertFalse(p.equals("othername"));
		assertFalse(p.equals(0));
	}
	
	@Test
	public void testGetProteinesByID()
	{
		
	}
}
