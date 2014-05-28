package test.models;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import models.Database;
import models.Mutation;

import org.junit.Test;

import play.Logger;
import scalaz.std.list;

public class MutationTest {

	private Mutation m = new Mutation(1, "SNP", "rsID", 1, new char[] { 'A',
			'T', 'T', 'T', 'T', 'T' });

	@Test
	public void testConstructor() {
		assertEquals(m.getId(), 1);
		assertEquals(m.getMutationType(), "SNP");
		assertEquals(m.getRsID(), "rsID");
		assertEquals(m.getChromosome(), 1);
	}

	@Test
	public void testAlleles() {
		assertEquals("[A, T]", m.child());
		assertEquals("[T, T]", m.father());
		assertEquals("[T, T]", m.mother());
	}

	@Test
	public void findById() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				try {
					List<Mutation> list = Mutation.getMutations(1);
					assertEquals(list.size(), 2);

					Logger.info("done balblalal");
				} catch (SQLException e) {
					Logger.error(e.toString());
				}
			}
		});
	}

	@Test
	public void testToAllelesString() {
		assertEquals("ATTTTT", m.toAllelesString());
	}
}