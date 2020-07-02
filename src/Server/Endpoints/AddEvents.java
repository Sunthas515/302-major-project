package Server.Endpoints;

	import Shared.Network.Request;
	import Shared.Network.Response;
	import Shared.Network.Token;
	import Shared.Permissions.Perm;
	import Shared.Schedule.Event;

	import java.io.IOException;

/***
 * end point for adding an event to the database
 */
public class AddEvents extends Endpoint {
	public AddEvents(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.addEvents;

		requiredPermission = Perm.SCHEDULE_BILLBOARDS;
	}

	/***
	 * end point for addEvent method which adds an event to the schedule object
	 * and saves it to the database
	 * @param input contains data for the Event class
	 * @return boolean
	 */
	public Object executeEndpoint(Request input){

		Event event = (Event) input.getData();
		if (server.db.checkBillboardExists(event.billboardName)) {
			try {
				//adds an event to the schedule and database
				server.db.addEvent(event);
				return true;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

}
