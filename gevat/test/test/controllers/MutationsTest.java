package test.controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.test.Helpers.callAction;
import static play.test.Helpers.status;

import org.junit.Test;

import play.mvc.Result;

/**
 * Test the Mutations controller
 * @author willem
 *
 */
public class MutationsTest {

	/**
	 * Test the rendering of the mutation page
	 */
	@Test
	public void testShow(){
		Result result = callAction(controllers.routes.ref.Mutations.show(1, 1));   
	    assertThat(status(result)).isEqualTo(SEE_OTHER);

	    // invalid user
	    result = callAction(controllers.routes.ref.Mutations.show(Integer.MAX_VALUE, 1)); 
	    assertThat(status(result)).isEqualTo(SEE_OTHER);
	}
	
	@Test
	public void testMutationNotFound() {
		Result result = callAction(controllers.routes.ref.Mutations.show(1, Integer.MAX_VALUE)); 
	    assertThat(status(result)).isEqualTo(SEE_OTHER);
	}
}
