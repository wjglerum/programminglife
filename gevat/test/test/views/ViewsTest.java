package test.views;

import models.User;

import org.junit.*;

import play.mvc.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ViewsTest {
  
  private User user;
  
  @Before
  public void fakeSession() {
    user = new User(1, "Foo", "Bar", "foobar");
  }

  @Test
  public void aboutTemplate() {
    Content html = views.html.about.render(user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("About");
  }

  @Test
  public void helpTemplate() {
    Content html = views.html.help.render(user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Help");
    assertThat(contentAsString(html)).contains("App documentation.");
  }

  @Test
  public void dashboardTemplate() {
    Content html = views.html.dashboard.render("Lorem ipsum", user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Dashboard");
    assertThat(contentAsString(html)).contains("Lorem ipsum");
  }

}