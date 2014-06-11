package test.models;

import static org.junit.Assert.assertEquals;
<<<<<<< HEAD
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import models.User;
=======
import models.application.User;
>>>>>>> master

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

}
