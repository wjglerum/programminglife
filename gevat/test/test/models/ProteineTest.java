package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import models.protein.Protein;

import org.junit.Test;

public class ProteineTest {

	/**
	 * Tests the constructor of proteine.
	 */
	@Test
	public void testConstructor() {
		ArrayList<String> list = new ArrayList<String>();
		String listAsString = list.toString().substring(1, list.toString().length()-1);
		Protein p = new Protein("name", list);
		assertEquals("name", p.getName());
		assertEquals(listAsString, p.getDisease());
	}

	/**
	 * Tests the equals function of proteine.
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
}
