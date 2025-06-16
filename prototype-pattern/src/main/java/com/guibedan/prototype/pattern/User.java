package com.guibedan.prototype.pattern;

public class User implements Prototype {
	private String id;
	private String email;
	private String password;
	private String username;

	public User() {
	}

	public User(String id, String email, String password, String username) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.username = username;
	}

	private User(User user) {
		this.id = user.id;
		this.email = user.email;
		this.password = user.password;
		this.username = user.username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "User{" + "id='" + id + '\'' + ", email='" + email + '\'' + ", password='" + password + '\''
				+ ", username='" + username + '\'' + '}';
	}

	@Override
	public Prototype clone() {
		return new User(this);
	}
}
