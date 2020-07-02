package Server.Endpoints;

import Shared.Billboard;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Permissions.Perm;
import Shared.Permissions.Permissions;
import Shared.Schedule.Event;

import java.io.IOException;
import java.util.ArrayList;

/***
 * end point for deleting a user from the database
 */
public class DeleteBillboard extends Endpoint {
	public DeleteBillboard() {
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.deleteBillboard;
	}

	/***
	 * end point for deleting a user
	 * @param input
	 * @return request status
	 */
	public Object executeEndpoint(Request input) {
//		return server.db.rmBillboard((String) input.getData());
		if (!server.db.checkBillboardExists((String) input.getData())) {
			return false;
		}
		Billboard billboard = null;
		try {
			billboard = server.db.requestBillboard((String) input.getData());
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}

		Permissions permissions = new Permissions(server.db.getUserDetails(input.getToken().getUser()).getPermissions());
		if (permissions.hasPermission(Perm.EDIT_ALL_BILLBOARDS)) {
			try {
				ArrayList<Event> events = server.db.requestEvents();
				for (Event event : events) {
					if (event.billboardName.equals(billboard.name)) {
						server.db.rmEvent(event, true);
					}
				}
				server.db.rmBillboard(billboard.name);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (billboard.author.equals(input.getToken().getUser())) {
			// Deleting their own billboard
			try {
				ArrayList<Event> events = server.db.requestEvents();
				boolean used = false;
				for (Event event : events) {
					if (event.billboardName.equals(billboard.name)) {
						used = true;
						break;
					}
				}
				if (used) {
					return new Response("error", "Permission denied (billboard is scheduled)", null);
				} else {
					server.db.rmBillboard(billboard.name);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		return new Response("error", "Permission denied", null);
	}

}
