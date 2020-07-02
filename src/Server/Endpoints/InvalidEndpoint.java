package Server.Endpoints;

import Shared.Network.Request;
import Shared.Network.Response;

/**
 * A simple endpoint that says you stuffed up
 */
public class InvalidEndpoint extends Endpoint {
	public Response Run(Request request) {
		System.out.println("An invalid endpoint was invoked! Ensure your Endpoint was added to Sever.allActions");
		return new Response("error", "Invalid endpoint", null);
	}
}
