package Shared;

import Shared.Permissions.Permissions;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Request is a class which should be used to
 * store a username and (not hashed) password.
 * The only way to retrieve the password will return
 * it in hashed and encoded form.
 *
 * @author Colby Derix n10475991
 */
public class Credentials implements Serializable {
	String _username;
	String _password;
	Permissions _permissions;

	/**
	 * Credentials Constructor
	 * @param username The username
	 * @param password The (not hashed) password
	 * @param permissions permissions object
	 */
	public Credentials(String username, String password, Permissions permissions) {
		_username = username;
		_password = password;
		_permissions = permissions;
	}

	/***
	 * fetches the password hahs
	 * @return password hash as string
	 */
	public String getPassword() {
		if (_password == null) {
			return null;
		}
		Base64.Encoder base64 = Base64.getEncoder();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			return "E R R O R  -  N O  S H A - 2 5 6";
		}

		byte[] hash = md.digest(_password.getBytes());
		String final_password = new String(base64.encode(hash));
		return final_password;
	}

	public String getUsername() { return _username; }

	public int getPermissions() { return _permissions.toInt(); }

	/***
	 *
	 * @return formatted string containing username, password and permissions
	 */
	public String toString() {
		String passwordText = "N/A";
		if (_password != null) {
			passwordText = getPassword().substring(0, 9) + "...";
		}
		String permsText = "N/A";
		if (_permissions != null) {
			permsText = _permissions.toString();
		}
		return String.format("{username: \"%s\", password: \"%s\", permissions: %s}", _username, passwordText, permsText);
	}
}
