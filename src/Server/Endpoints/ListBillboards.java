package Server.Endpoints;

import Shared.Network.Request;
import Shared.Network.Response;
import Shared.Network.Token;
import Shared.Permissions.Perm;

import java.util.ArrayList;

/***
 * end point for listing the billboards from the database
 */
public class ListBillboards extends Endpoint {
	public ListBillboards(){
		// This is the enum value bound to this endpoint
		associatedEndpoint = EndpointType.listBillboards;

		requiredPermission = Perm.CREATE_BILLBOARDS;
	}

	/***
	 * end point for retrieving the list of all billboards from the database
	 * @param input
	 * @return ArrayList of billboards (String)
	 */
	public Object executeEndpoint(Request input){
		return server.db.listBillboards();
	}

}
