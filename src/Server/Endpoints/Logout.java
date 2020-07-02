package Server.Endpoints;

import Shared.Network.Request;
import Shared.Network.Response;

/***
 * end point allowing log out functionality
 */
public class Logout extends Endpoint {
	public Logout(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.logout;
	}

	public Response Run(Request request) {
		if (server.socketHandler.logout(request.getToken())) {
			return new Response("success", "Logged out successfully", null);
		} else {
			return new Response("error", "Invalid token", null);
		}
	}
}
