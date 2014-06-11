package test.models;

import static org.junit.Assert.assertEquals;
import models.application.User;

import org.junit.Test;

public class UserTest {

	User user = new User(1, "John", "Doe", "jdoe");

	@Test
	public void testUserConstructor() {
		User user = new User(1, "John", "Doe", "jdoe");
		assertEquals(user.id, 1);
		assertEquals(user.name, "John");
		assertEquals(user.surname, "Doe");
		assertEquals(user.username, "jdoe");
	}

}
