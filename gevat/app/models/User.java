package models;

public class User {

  public String username;
  public String name;
  public String password;
  
  public User(String username, String name, String password) {
    this.username = username;
    this.name = name;
    this.password = password;
  }
  
  public static User authenticate(String user, String pass) {
    if (user.equals("user") && pass.equals("pass")) {
      return new User("user", "Test User", "pass");
    }
    
    return null;
  }
    
}