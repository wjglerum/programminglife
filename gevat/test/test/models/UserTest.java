package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import models.user.User;
import models.user.UserRepository;
import models.user.UserService;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

	private User u;
	private final int id = 1;
	private final String name = "John";
	private final String surname = "Doe";
	private final String username = "jdoe";
	private final String password = "password";
	private final UserRepository repositoryMock = mock(UserRepository.class);
	private final UserService userService = new UserService(repositoryMock);

	@Before
	public void setUp() {
		u = new User(id, name, surname, username);
	}

	@Test
	public void testGetUser() throws SQLException {
		when(repositoryMock.getUser(username)).thenReturn(u);
		User x = userService.getUser(username);

		assertEquals(x.getId(), id);
		assertEquals(x.getName(), name);
		assertEquals(x.getSurname(), surname);
		assertEquals(x.getUsername(), username);

		verify(repositoryMock).getUser(username);
	}

	@Test
	public void testAuthentication() throws SQLException {
		when(repositoryMock.authenticate(username, password)).thenReturn(u);

		User x = userService.authenticate(username, password);
		assertEquals(x.getId(), id);
		assertEquals(x.getName(), name);
		assertEquals(x.getSurname(), surname);
		assertEquals(x.getUsername(), username);

		verify(repositoryMock).authenticate(username, password);
	}

	@Test
	public void testGetSetName() {
		assertEquals(u.getName(), name);
		final String newName = "new";
		u.setName(newName);
		assertNotEquals(u.getName(), name);
		assertEquals(u.getName(), newName);
	}
	
	@Test
	public void testGetSetSurName() {
		assertEquals(u.getSurname(), surname);
		final String newName = "new";
		u.setSurname(newName);
		assertNotEquals(u.getSurname(), surname);
		assertEquals(u.getSurname(), newName);
	}
	
	@Test
	public void testGetSetUserName() {
		assertEquals(u.getUsername(), username);
		final String newName = "new";
		u.setUsername(newName);
		assertNotEquals(u.getUsername(), username);
		assertEquals(u.getUsername(), newName);
	}
	
	@Test
	public void testGetId() {
		assertEquals(u.getId(), id);
	}

}
