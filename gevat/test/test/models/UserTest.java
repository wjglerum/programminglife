package test.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import models.application.User;

import models.application.UserRepository;
import models.application.UserService;

import org.junit.Test;

public class UserTest {

	@Test
	public void testGetUser() throws SQLException {
		UserRepository repositoryMock = mock(UserRepository.class);
		User u = new User(1, "John", "Doe", "jdoe");
		when(repositoryMock.getUser("jdoe")).thenReturn(u);

		UserService userService = new UserService(repositoryMock);
		User x = userService.getUser("jdoe");
		assertEquals(x.id, 1);
		assertEquals(x.name, "John");
		assertEquals(x.surname, "Doe");
		assertEquals(x.username, "jdoe");

		verify(repositoryMock).getUser("jdoe");

	}
	
	@Test
	public void testAuthentication() throws SQLException {
		UserRepository repositoryMock = mock(UserRepository.class);
		User u = new User(1, "John", "Doe", "jdoe");
		when(repositoryMock.authenticate("jdoe", "password")).thenReturn(u);
		
		UserService userService = new UserService(repositoryMock);
		User x = userService.authenticate("jdoe", "password");
		assertEquals(x.id, 1);
		assertEquals(x.name, "John");
		assertEquals(x.surname, "Doe");
		assertEquals(x.username, "jdoe");

		verify(repositoryMock).authenticate("jdoe", "password");
	}

}
