package models.security;

/**
 * Generates a hash.
 */
public final class GenerateHash {

	private GenerateHash() {
	}
	
	/**
	 * Class to manually hash and salt passwords for user logins.
	 *
	 * @param args Possible aguments given
	 */
	public static void main(final String[] args) {
		String password = "pass";
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println(hashed);
	}
}
