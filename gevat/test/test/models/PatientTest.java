package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import models.application.Patient;
import models.database.Database;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import play.test.FakeApplication;

@RunWith(MockitoJUnitRunner.class)
public class PatientTest {
	
	@Mock Database mockedDatabase;
	@Captor private ArgumentCaptor<String> stringCaptor;
	@Captor private ArgumentCaptor<String> queryCaptor;
	
	Patient p;
	final int id = 1;
	final String name = "Test";
	final String surname = "T1000";
	final String vcfFile = "VCF.vcf";
	final Long vcfLength = new Long(1024*1024);
	
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
	
	@Before
	public void setUp() {
		p = new Patient(id, name, surname, vcfFile, vcfLength);
	}

	@Test
	public void patientConstructorTest() {
		assertEquals(id, p.getId());
		assertEquals(name, p.getName());
		assertEquals(surname, p.getSurname());
		assertEquals(vcfFile, p.getVcfFile());
		assertTrue((double)1 == p.getVcfLengthMB());
		p.setSurname("Doe");
		assertNotEquals(surname, p.getSurname());
	}
	
	@Test
	public void idGetSetTest() {
		assertNotEquals(2, p.getId());
		p.setId(2);
		assertEquals(2, p.getId());		
	}
	
	@Test
	public void nameGetSetTest() {
		assertNotEquals("John", p.getName());
		p.setName("John");
		assertEquals("John", p.getName());
	}
	
	@Test
	public void vcfFileGetSetTest() {
		assertNotEquals("TestFile.vcf", p.getVcfFile());
		p.setVcfFile("TestFile.vcf");
		assertEquals("TestFile.vcf", p.getVcfFile());
	}
	
	@Test
	public void addPatientTest() throws SQLException {
		//when(mockedDatabase.insert("data", "INSERT INTO patient VALUES (nextval('p_id_seq'::regclass),1,'Test', 'T1000', 'VCF.vcf', 1048576);")).thenReturn(false);
		//System.out.println(Patient.add(id, name, surname, vcfFile, vcfLength));
		//when(Database.delete(anyString(), anyString())).doNothing();
		//System.out.println(stringCaptor.getValue());
		//System.out.println(queryCaptor.getValue());
	}
	@Test
	public void getPatientTest() {
		
	}
	
	@Test
	public void getAllPatientsTest() {
		
	}
	
	@Test
	public void removePatientTest() {
		
	}

	@AfterClass
	public static void shutDownFakeApplication() {
		stop(fakeApplication);
	}
}
