package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;
import static play.test.Helpers.status;
import static play.test.Helpers.stop;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.mutation.Mutation;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controllers.Chromosomes;
import controllers.Mutations;
import play.Logger;
import play.mvc.Result;
import play.test.FakeApplication;

/**
 * Test the Mutations controller.
 * 
 * @author willem
 * 
 */
public class MutationsTest {

	private Mutation m;
	private final int amount = 20;
	private final int pid = 1;
	private final int mid = 1;
	private final String type = "SNP";
	private final String rsID = "rs001";
	private final String chromosome = "2";
	private final char[] alleles = { 'A', 'A', 'A', 'A', 'A', 'A' };
	private final int startPoint = 3;
	private final int endPoint = 4;
	private final int position = 5;
	private final float score = 6;
	private final float freq = 7;

	/**
	 * Helper to start a fakeApplication to test.
	 */
	private static FakeApplication fakeApplication;

	/**
	 * Start the fakeApplication.
	 */
	@BeforeClass
	public static void startFakeApplication() {
		fakeApplication = fakeApplication();
		start(fakeApplication);
	}

	/**
	 * Stop the fakeApplication.
	 */
	@AfterClass
	public static void shutDownFakeApplication() {
		stop(fakeApplication);
	}

	/**
	 * Setup mutation.
	 */
	@Before
	public void setup() {
		m = new Mutation(mid, type, rsID, chromosome, alleles, startPoint,
				endPoint, position, score, freq);
	}

	/**
	 * Test the rendering of the mutation page.
	 */
	@Test
	public void testShow() {
		Result result = callAction(controllers.routes.ref.Mutations.show(1, 1));
		assertThat(status(result)).isEqualTo(SEE_OTHER);

		// invalid user
		result = callAction(controllers.routes.ref.Mutations.show(
				Integer.MAX_VALUE, 1));
		assertThat(status(result)).isEqualTo(SEE_OTHER);
	}

	/**
	 * Test the mutationNotFound error.
	 */
	@Test
	public void testMutationNotFound() {
		Result result = callAction(controllers.routes.ref.Mutations.show(1,
				Integer.MAX_VALUE));
		assertThat(status(result)).isEqualTo(SEE_OTHER);
	}

	/**
	 * Test the nearby mutations to JSON.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testNearbyMutationsJSON() throws SQLException {
		String mutationsJSON = "[]";
		String res = Mutations.nearbyMutationsJSON(m, amount, pid);
		assertThat(res).isEqualTo(mutationsJSON);
	}
}
