package Server.Endpoints;

import Shared.Network.Request;
import Shared.Network.Response;

/**
 * end point to fetch the current active billboard
 */
public class GetCurrentBillboard extends Endpoint {
	public GetCurrentBillboard(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.getCurrentBillboard;
	}

	/***
	 * end point to request the current billboard
	 * @param input
	 * @return null
	 */
	public Response Run(Request input) {
		try{
			return new Response("success", server.db.requestBillboard(server.db.requestCurrentEvent().billboardName), null);
		} catch (Exception ex) {
			return new Response("error", null, null);
		}
	}
}
