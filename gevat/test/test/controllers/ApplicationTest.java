package test.controllers;

import org.junit.*;

import play.mvc.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ApplicationTest {

  @Test
  public void callIndex() {
    Result result = callAction(controllers.routes.ref.Application.about());   
    assertThat(status(result)).isEqualTo(OK);
    assertThat(contentType(result)).isEqualTo("text/html");
    assertThat(charset(result)).isEqualTo("utf-8");
    assertThat(contentAsString(result)).contains("About");
  }

}