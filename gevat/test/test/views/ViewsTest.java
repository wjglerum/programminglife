package test.views;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.data.Form.form;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import models.application.Patient;
import models.application.User;
import models.dna.Mutation;

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
   * Test the list patients template
   */
  @Test
  public void patientsTemplate() {
    Patient patient = new Patient(1, "name", "surname", "file", new Long(12345));
    List<Patient> patients = new ArrayList<Patient>();
    
    patients.add(patient);
    
    Content html = views.html.patients.render(patients, user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Patients overview");
    assertThat(contentAsString(html)).contains("No patients found...");
  }

  /**
   * Test the add a patient template
   */
  @Test
  public void patientAddTemplate() {
    Content html = views.html.patient_add.render(form(controllers.Patients.Add.class), user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Add new patient");
    assertThat(contentAsString(html)).contains("Please enter the patients details to add.");
  }

  /**
   * Test the single patient template
   */
  @Test
  public void patientTemplate() {
    Patient patient = new Patient(1, "name", "surname", "file", new Long(12345));
    Mutation mutation = new Mutation(1, "SNP", "rs12345", 1, "ATATAT".toCharArray(), 1, 2);
    List<Mutation> mutations = new ArrayList<Mutation>();
    
    mutations.add(mutation);
    
    Content html = views.html.patient.render(patient, mutations, user);
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Patient " + patient.getName() + " " + patient.getSurname());
    assertThat(contentAsString(html)).contains("<strong>VCF file</strong> used for processing: <em>" + patient.getVcfFile() + " (" + patient.getVcfLengthMB() + " MB)</em>");
  }

  /**
   * Test the mutation template
   */
  @Test
  public void mutationTemplate() {
    Patient patient = new Patient(1, "name", "surname", "file", new Long(12345));
    Mutation mutation = new Mutation(1, "SNP", "rs12345", 1, "ATATAT".toCharArray(), 1, 1);
    
    Content html = views.html.mutation.render(patient, mutation, user, "");
    
    assertThat(contentType(html)).isEqualTo("text/html");
    assertThat(contentAsString(html)).contains("Mutation " + mutation.getRsID() + " of patient " + patient.getName() + " " + patient.getSurname());
  }

  /**
   * Stop the fake application
   */
  @AfterClass
  public static void stopApp() {
      Helpers.stop(app);
  }

}