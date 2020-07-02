package Server.Endpoints;

import Server.Server;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Network.TokenStatus;
import Shared.Permissions.Perm;

/**
 * An Endpoint is something that the server can be requested to do remotely.
 *
 * @author Lucas Maldonado n10534342
 */
public class Endpoint {

	/** This is the endpoint command used to access this endpoint */
	public EndpointType associatedEndpoint;

	/** What permission the caller needs to have for it to work */
	public Perm requiredPermission;

	Server server;


	/** Constructor */
	public Endpoint() {
		associatedEndpoint = null;
	}


	/**
	 * Default implementation of Run just makes sure that the given key is valid
	 */
	public Response Run(Request request) {

		TokenStatus tokenStatus = server.socketHandler.validateToken(request.getToken());

		switch (tokenStatus) {
			case valid:
				if (requiredPermission != null) {
					if (!server.socketHandler.hasPerm(request.getToken().getUser(), requiredPermission)) {
						return new Response("error", "Permission denied", null);
					}
				}

				Object result = executeEndpoint(request);
				if (result instanceof Response) {
					return (Response) result;
				} else {
					server.socketHandler.resetExpire(request.getToken());
					return new Response("success", result, request.getToken());
				}
			case expired:
				return new Response("error", "Expired token", null);
			case invalid:
				return new Response("error", "Invalid token", null);
		}

		// This line should never be reached, but it's required for compilation
		return new Response("error", "Not quite sure what happened there", null);
	}


	/**
	 * Override this method to add functionality to your Endpoint
	 */
	public Object executeEndpoint(Request input){
		return "Endpoint not implemented yet, but was called correctly";
	}


	/**
	 * Should only be called by server, equivalent to constructor
	 */
	public void init(Server inServer) {
		server = inServer;
		System.out.printf(" - Endpoint '%s' is ready, access with 'EndpointType.%s'\n", getClass().getSimpleName(), associatedEndpoint.toString());
	}
}
