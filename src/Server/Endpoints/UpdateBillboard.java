package Server.Endpoints;

import Shared.Billboard;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Permissions.Perm;
import Shared.Permissions.Permissions;

import java.io.IOException;

/***
 * endpoint to update a billboard in the database
 */
public class UpdateBillboard extends Endpoint {
	public UpdateBillboard(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.updateBillboard;
	}

	/***
	 * end point for updateBillboard method which updates a billboard in the DB
	 * @param input contains data sent from client
	 * @return boolean
	 */
	public Object executeEndpoint(Request input){

		Billboard billboard = (Billboard) input.getData();
		if (!server.db.checkBillboardExists(billboard.name)) {
			return false;
		}

		if (billboard.author.equals(input.getToken().getUser())) {
			// Editing their own billboard
			try {
				//adds an event to the schedule and database
				server.db.updateBillboard(billboard);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			Permissions permissions = new Permissions(server.db.getUserDetails(input.getToken().getUser()).getPermissions());
			// Editing another billboard
			if (permissions.hasPermission(Perm.EDIT_ALL_BILLBOARDS)) {
				try {
					//adds an event to the schedule and database
					server.db.updateBillboard(billboard);
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				return new Response("error", "Permission denied", null);
			}
		}
	}
}
