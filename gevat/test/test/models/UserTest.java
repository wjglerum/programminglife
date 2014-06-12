package test.models;

import static org.junit.Assert.assertEquals;
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

		assertEquals(x.id, id);
		assertEquals(x.name, name);
		assertEquals(x.surname, surname);
		assertEquals(x.username, username);

		verify(repositoryMock).getUser(username);
	}

	@Test
	public void testAuthentication() throws SQLException {
		when(repositoryMock.authenticate(username, password)).thenReturn(u);

		User x = userService.authenticate(username, password);
		assertEquals(x.id, id);
		assertEquals(x.name, name);
		assertEquals(x.surname, surname);
		assertEquals(x.username, username);

		verify(repositoryMock).authenticate(username, password);
	}

}
