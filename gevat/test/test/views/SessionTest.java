package test.views;

import models.User;

import org.junit.*;

import play.mvc.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class SessionTest {
  
  private User user;
  
  @Before
  public void fakeSession() {
    user = new User(1, "Foo", "Bar", "foobar");
  }

  @Test
  public void aboutTemplateLoggedIn() {
    Content html = views.html.about.render(user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Log out");
  }

  @Test
  public void aboutTemplateLoggedOut() {
    Content html = views.html.about.render(null);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Log in");
  }

}