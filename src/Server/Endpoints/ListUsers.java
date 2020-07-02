package Server.Endpoints;

import Shared.Credentials;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Network.Token;
import Shared.Permissions.Perm;
import Shared.Schedule.Event;

import java.io.IOException;
import java.util.ArrayList;

/***
 * end point for listing users from the database
 */
public class ListUsers extends Endpoint {
	public ListUsers(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.listUsers;
	}

	/***
	 * end point for retrieving the list of all users from the database
	 * @param input
	 * @return ArrayList of usernames (String)
	 */
	public Object executeEndpoint(Request input){
		Token token = input.getToken();
		if (!server.socketHandler.hasPerm(token.getUser(), Perm.EDIT_USERS)) {
			ArrayList<Credentials> singleUser = new ArrayList<Credentials>();
			singleUser.add(server.db.getUserDetails(input.getToken().getUser()));
			return singleUser;
		}

		return server.db.listUsers();
	}

}
