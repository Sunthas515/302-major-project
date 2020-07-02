package Server.Endpoints;

import Shared.Network.Request;
import Shared.Network.Response;

/***
 * end point for getting a billboard from the database
 */
public class GetBillboard extends Endpoint {
	public GetBillboard(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.getBillboard;
	}

	/***
	 * end point to request a specific billboard
	 * @param input
	 * @return null
	 */
	public Response Run(Request input) {
		try{
			return new Response("success", server.db.requestBillboard((String) input.getData()), null);
		} catch (Exception ex) {
			return new Response("error", null, null);
		}
	}
}
