package Server.Endpoints;

import Shared.Network.Request;
import Shared.Permissions.Perm;

/***
 * end point for deleting a user from the database
 */
public class DeleteUser extends Endpoint {
	public DeleteUser(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.deleteUser;

		requiredPermission = Perm.EDIT_USERS;
	}

	/***
	 * end point for deleting a user
	 * @param input
	 * @return request status
	 */
	public Object executeEndpoint(Request input) {
		return server.db.rmUser((String) input.getData());
	}

}
