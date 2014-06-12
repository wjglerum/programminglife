package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.patient.Patient;
import models.patient.PatientRepository;
import models.patient.PatientService;

import org.junit.Before;
import org.junit.Test;

public class PatientTest {

	private Patient p;
	private final int id = 1;
	private final String name = "Test";
	private final String surname = "T1000";
	private final String vcfFile = "VCF.vcf";
	private final Long vcfLength = new Long(1024 * 1024);
	private final PatientRepository repositoryMock = mock(PatientRepository.class);
	private final PatientService patientService = new PatientService(
			repositoryMock);

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
		assertTrue((double) 1 == p.getVcfLengthMB());
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
	public void getPatientTest() throws SQLException {
		when(repositoryMock.get(id, id)).thenReturn(p);

		Patient x = patientService.get(id, id);
		assertEquals(id, x.getId());
		assertEquals(name, x.getName());
		assertEquals(surname, x.getSurname());
		assertEquals(vcfFile, x.getVcfFile());
		assertTrue((double) 1 == x.getVcfLengthMB());

		verify(repositoryMock).get(id, id);
	}

	@Test
	public void getAllPatientsTest() throws SQLException {
		List<Patient> list = new ArrayList<Patient>();
		list.add(p);
		when(repositoryMock.getAll(id)).thenReturn(list);

		List<Patient> result = patientService.getAll(id);
		assertEquals(1, result.size());

		verify(repositoryMock).getAll(id);
	}

	@Test
	public void addPatientTest() throws SQLException {
		when(repositoryMock.add(id, name, surname, vcfFile, vcfLength))
				.thenReturn(p);

		Patient x = patientService.add(id, name, surname, vcfFile, vcfLength);
		assertEquals(id, x.getId());
		assertEquals(name, x.getName());
		assertEquals(surname, x.getSurname());
		assertEquals(vcfFile, x.getVcfFile());
		assertTrue(1.0 == x.getVcfLengthMB());

		verify(repositoryMock).add(id, name, surname, vcfFile, vcfLength);
	}

	@Test
	public void removePatientTest() {

	}

}
