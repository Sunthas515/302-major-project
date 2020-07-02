package Server.Endpoints;

import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Network.Token;
import Shared.Permissions.Perm;

import java.util.ArrayList;

/***
 * end point for retrieving user details from the database
 */
public class GetUserDetails extends Endpoint {
	public GetUserDetails(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.getUserDetails;
	}

	/***
	 * end point for retrieving details for a specific user
	 * @param input
	 * @return Credentials class for the user (nulled password field)
	 */
	public Object executeEndpoint(Request input){
		Token token = input.getToken();
		if (!server.socketHandler.hasPerm(token.getUser(), Perm.EDIT_USERS)) {
			if (token.getUser().equals(input.getData())) {
				return server.db.getUserDetails(token.getUser());
			} else {
				return new Response("error", "Permission denied", null);
			}
		}

		return server.db.getUserDetails((String) input.getData());
	}

}
