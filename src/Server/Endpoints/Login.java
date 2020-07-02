package Server.Endpoints;

import Shared.Credentials;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Network.Token;

/***
 * end point allowing login functionality
 */
public class Login extends Endpoint {
	public Login(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.login;
	}

	public Response Run(Request request) {
		Credentials credentials = null;
		try {
			// Cast request data to Credentials class
			credentials = (Credentials)request.getData();
		} catch (java.lang.ClassCastException e) {
			return new Response("error", "Malformed request (data property should be class Credentials)", null);
		}
		String username = credentials.getUsername();
		String password = credentials.getPassword();

		try {
			if (server.db.checkPassword(username, password)) {
				Token token = server.socketHandler.generateToken(username);
				return new Response("success", token, token);
			} else {
				return new Response("error", "Invalid credentials", null);
			}
		} catch (java.security.NoSuchAlgorithmException e) {
			return new Response("error", "Server error - SHA-256 not implemented", null);
		}
	}
}
