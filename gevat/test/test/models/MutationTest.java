package test.models;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import models.Mutation;

import org.junit.Test;

import play.Logger;
import scalaz.std.list;

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


	@Test
	public void findById() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				try {
					List<Mutation> list = Mutation.getMutations(1);
					assertEquals(list.size(), 2);
				} catch (SQLException e) {
					Logger.error(e.toString());
				}
			}
		});
	}
}
