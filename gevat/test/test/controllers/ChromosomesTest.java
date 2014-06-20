package test.controllers;

import java.util.ArrayList;
import java.util.List;

import models.mutation.Mutation;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controllers.Chromosomes;
import play.Logger;
import play.mvc.Result;
import play.test.FakeApplication;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;
import static play.test.Helpers.status;
import static play.test.Helpers.stop;

public class ChromosomesTest {

	private Mutation m;
	private final int pid = 10;
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
	 * Test the rendering of the chromosome page.
	 */
	@Test
	public void testShow() {
		Result result = callAction(controllers.routes.ref.Chromosomes.show(pid,
				chromosome));
		assertThat(status(result)).isEqualTo(SEE_OTHER);
	}

	/**
	 * Test the mutation list to JSON.
	 */
	@Test
	public void testMutationsJSON() {
		List<Mutation> list = new ArrayList<Mutation>();
		list.add(m);
		String res = Chromosomes.mutationsJSON(list, pid);
		assertThat(res).contains(rsID);
		assertThat(res).contains(pid + "");
		assertThat(res).contains(mid + "");
		assertThat(res).contains(type);
		assertThat(res).contains(position + "");
		
	}
}
