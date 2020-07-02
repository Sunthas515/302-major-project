package Server.Endpoints;

import Shared.Network.Request;

/**
 * The echo class is meant to be a simple demonstration of the Endpoint system.
 * You can copy its setup to get your own Endpoint up and running
 */
public class Echo extends Endpoint {

	public Echo(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.echo;
	}

	/**
	 * Overridden from parent class. Will be called automatically when the data is needed.
	 * If you're looking for more advanced control, override Run() instead:
	 * 	public Response Run(Request request) {}
	 *
	 * This demo just echoes the input.
	 *
	 * @param input the data given to the server, used to process the request. Use this however you like!
	 * @return the resulting data to give back to the sender. Can be null.
	 */
	public Object executeEndpoint(Request input) {
		return input;
	}
}
