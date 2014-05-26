package models;

public class GenerateHash {

	/**
	 * Class to manually hash and salt passwords for user logins
	 * @param args
	 */
	public static void main(String[] args){
		String password = "pass";
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println(hashed);
	}
}
