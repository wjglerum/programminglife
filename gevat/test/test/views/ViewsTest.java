package test.views;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.data.Form.form;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.util.Collections;
import java.util.Map;

import models.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.api.mvc.RequestHeader;
import play.mvc.Content;
import play.mvc.Http;
import play.test.FakeApplication;
import play.test.Helpers;

public class ViewsTest {
  
  private User user;
  
  public static FakeApplication app;
  private final Http.Request request = mock(Http.Request.class);

  /**
   * Start a fake application
   */
  @BeforeClass
  public static void startApp() {
      app = Helpers.fakeApplication();
      Helpers.start(app);
  }

  /**
   * Setup a HTTP Context
   */
  @Before
  public void setUp() throws Exception {
      Map<String, String> flashData = Collections.emptyMap();
      Map<String, Object> argData = Collections.emptyMap();
      
      Long id = 2L;
      
      RequestHeader header = mock(RequestHeader.class);
      
      Http.Context context = new Http.Context(id, header, request, flashData, flashData, argData);
      Http.Context.current.set(context);
  }
  
  /**
   * Make a fake session
   */
  @Before
  public void fakeSession() {
    user = new User(1, "Foo", "Bar", "foobar");
  }

  /**
   * Test the about template
   */
  @Test
  public void aboutTemplate() {
    Content html = views.html.about.render(user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("About");
  }

  /**
   * Test the help template
   */
  @Test
  public void helpTemplate() {
    Content html = views.html.help.render(user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Help");
    assertThat(contentAsString(html)).contains("App documentation.");
  }
  
  /**
   * Test the dashboard template
   */
  @Test
  public void dashboardTemplate() {
    Content html = views.html.dashboard.render("Lorem ipsum", user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Dashboard");
    assertThat(contentAsString(html)).contains("Lorem ipsum");
  }

  /**
   * Test the login template
   */
  @Test
  public void loginTemplate() {
    Content html = views.html.login.render(form(controllers.Authentication.Login.class));
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Login");
    assertThat(contentAsString(html)).contains("Please log in with your credentials.");
  }

  /**
   * Stop the fake application
   */
  @AfterClass
  public static void stopApp() {
      Helpers.stop(app);
  }

}