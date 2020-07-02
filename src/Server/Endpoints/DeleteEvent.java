package Server.Endpoints;

import Shared.Network.Request;
import Shared.Permissions.Perm;
import Shared.Schedule.Event;

import java.util.logging.ConsoleHandler;

/***
 * end point for deleting a user from the database
 */
public class DeleteEvent extends Endpoint {
	public DeleteEvent(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.deleteEvent;

		requiredPermission = Perm.SCHEDULE_BILLBOARDS;
	}

	/***
	 * end point for deleting an event
	 * @param input
	 * @return request status
	 */
	public Object executeEndpoint(Request input) {
		Object[] data = (Object[]) input.getData();
		Event event = (Event) data[0];
		boolean future = (boolean) data[1];

		try {
			return server.db.rmEvent(event, future);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return false;
		}
	}
}
