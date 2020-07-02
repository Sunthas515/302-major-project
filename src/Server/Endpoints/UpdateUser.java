package Server.Endpoints;

import Shared.Credentials;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Permissions.Perm;
import Shared.Permissions.Permissions;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;

/***
 * end point to update user in the database
 */
public class UpdateUser extends Endpoint {
	public UpdateUser(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.updateUser;
	}

	/***
	 * end point for updateBillboard method which updates a billboard in the DB
	 * @param input contains data sent from client
	 * @return boolean
	 */
	public Object executeEndpoint(Request input){

		Credentials credentials = (Credentials) input.getData();
		String username = credentials.getUsername();
		String password = credentials.getPassword();
		int permissions = credentials.getPermissions();

		Permissions current_permissions = server.db.getPermissions(input.getToken().getUser());

		if (!(username.equals(input.getToken().getUser()) || current_permissions.hasPermission(Perm.EDIT_USERS))) {
			return new Response("error", "Permission denied", null);
		}

		if (!current_permissions.hasPermission(Perm.EDIT_USERS)) {
			// User must be editing themselves
			permissions = current_permissions.toInt();
		}

		try {
			//update them
			return server.db.updateUser(username, password, permissions);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}

	}

}
