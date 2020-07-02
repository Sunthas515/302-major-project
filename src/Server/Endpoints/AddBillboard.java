package Server.Endpoints;

import Shared.Billboard;
import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Network.Token;
import Shared.Permissions.Perm;
import Shared.Schedule.Event;

import java.io.IOException;

/***
 * end point for adding a billboard to the database
 */
public class AddBillboard extends Endpoint {
	public AddBillboard(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.addBillboard;

		requiredPermission = Perm.CREATE_BILLBOARDS;
	}

	/***
	 * end point for addBillboard method which adds a billboard to the DB
	 * @param input contains data sent from client
	 * @return boolean
	 */
	public Object executeEndpoint(Request input){
		Billboard billboard = (Billboard) input.getData();
		try {
			//adds an event to the schedule and database
			if (!billboard.author.equals(input.getToken().getUser())) {
				return new Response("error", "Illegal billboard (wrong username, provided: " + billboard.author + ", expected: " + input.getToken().getUser() +")", null);
			}
			server.db.addBillboard(billboard);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

}
