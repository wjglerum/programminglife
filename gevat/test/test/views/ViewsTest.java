package test.views;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.data.Form.form;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;
import models.user.User;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.api.mvc.RequestHeader;
import play.mvc.Content;
import play.mvc.Http;
import play.test.FakeApplication;
import play.test.Helpers;

/**
 * Test that checks all our views.
 * 
 * @author mathijs
 * 
 */
public class ViewsTest {

    private User user;

    public static FakeApplication app;
    private final Http.Request request = mock(Http.Request.class);

    // TODO: hoort bij die test die ik er uit heb gefilterded...
    // private static MutationRepositoryDB mutationRepository = new
    // MutationRepositoryDB();
    // private static MutationService mutationService = new MutationService(
    // mutationRepository);

    /**
     * Start a fake application.
     */
    @BeforeClass
    public static void startApp() {
        app = Helpers.fakeApplication();
        Helpers.start(app);
    }

    /**
     * Setup a HTTP Context.
     */
    @Before
    public void setUp() throws Exception {
        Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();

        Long id = 2L;

        RequestHeader header = mock(RequestHeader.class);

        Http.Context context = new Http.Context(id, header, request, flashData,
                flashData, argData);
        Http.Context.current.set(context);
    }

    /**
     * Make a fake session.
     */
    @Before
    public void fakeSession() {
        user = new User(1, "Foo", "Bar", "foobar");
    }

    /**
     * Test the dashboard template
     */
    @Test
    public void dashboardTemplate() {
        Content html = views.html.dashboard.render(user);

        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Dashboard");
    }

    /**
     * Test the login template.
     */
    @Test
    public void loginTemplate() {
        Content html = views.html.login
                .render(form(controllers.Authentication.Login.class));

        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Login");
        assertThat(contentAsString(html)).contains(
                "Please log in with your credentials.");
    }

    /**
     * Test the list patients template.
     */
    @Test
    public void patientsTemplate() {
        Patient patient = new Patient(1, "name", "surname", "file", new Long(
                12345), true, true);
        List<Patient> patients = new ArrayList<Patient>();

        patients.add(patient);

        Content html = views.html.patients.render(patients, user);

        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Patients overview");
        assertThat(contentAsString(html)).contains("No patients found...");
    }

    /**
     * Test the add a patient template.
     */
    @Test
    public void patientAddTemplate() {
        Content html = views.html.patient_add.render(
                form(controllers.Patients.Add.class), user);

        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Add new patient");
        assertThat(contentAsString(html)).contains(
                "Please enter the patients details to add.");
    }

    // TODO: kijken hoe we dit aan de praat krijgen
    // /**
    // * Test the single patient template.
    // */
    // @Test
    // public void patientTemplate() throws SQLException {
    // Patient patient = new Patient(1, "name", "surname", "file", new Long(
    // 12345), true, true);
    // Mutation mutation = new Mutation(1, "SNP", "rs12345", "1",
    // "ATATAT".toCharArray(), 1, 2, 0, 2, 0);
    // List<Mutation> mutations = new ArrayList<Mutation>();
    //
    // mutations.add(mutation);
    //
    // HashMap<Mutation, Double> map = new HashMap<Mutation, Double>();
    // for (Mutation m : mutations) {
    // map.put(m, (double) mutationService.getScore(m));
    // }
    // Content html = views.html.patient.render(patient, map, user);
    //
    // assertThat(contentType(html)).isEqualTo("text/html");
    // assertThat(contentAsString(html)).contains(
    // "Patient " + patient.getName() + " " + patient.getSurname());
    // assertThat(contentAsString(html)).contains(
    // "<strong>VCF file</strong> used for processing: <em>"
    // + patient.getVcfFile() + " ("
    // + patient.getVcfLengthMB() + " MB)</em>");
    // }

    /**
     * Test the mutation template.
     */
    @Test
    public void mutationTemplate() {
        Patient patient = new Patient(1, "name", "surname", "file", new Long(
                12345), true, true);
        Mutation mutation = new Mutation(1, "SNP", "rs12345", "1",
                "ATATAT".toCharArray(), 1, 1, 0, 2, 0);

        Content html = views.html.mutation.render(patient, mutation, user, "", "", "");

        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains(
                "Mutation " + mutation.getRsID() + " of patient "
                        + patient.getName() + " " + patient.getSurname());
    }

    /**
     * Stop the fake application.
     */
    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

}
