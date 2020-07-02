package Server.Endpoints;

import Shared.Network.Request;
import Shared.Schedule.Event;

import java.io.IOException;
import java.util.ArrayList;

/***
 * end point for fetching the event list from the database
 */
public class GetEvents extends Endpoint {
	public GetEvents(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.getEvents;
	}

	/***
	 * end point for retrieving the event list from the database
	 * @param input
	 * @return ArrayList of events
	 */
	public Object executeEndpoint(Request input){
		ArrayList<Event> eventList = null;
		try {
			eventList = server.db.returnEventList();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return eventList;
	}

}
