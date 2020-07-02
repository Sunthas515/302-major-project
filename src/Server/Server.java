package Server;

import Server.Endpoints.*;
import Shared.Billboard;
import Shared.Schedule.Event;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Server is a class which can receive requests and respond to them
 * along with make requests to the database
 *
 * @author Colby Derix n10475991
 * @author Dylan Robertson n10487310
 */
public class Server {

	public dbServer db;
	public ServerPropsReader propsReader;
	public ServerSocket serverSocket;
	public SocketHandler socketHandler;


	private static final Class[] allEndpoints = new Class[]{
		// List all of the Endpoints you want to make available here
		// Should mirror the EndpointType enum
		AddEvents.class,
		AddBillboard.class,
		DeleteUser.class,
		DeleteEvent.class,
		DeleteBillboard.class,
		Echo.class,
		GetBillboard.class,
		GetCurrentBillboard.class,
		GetEvents.class,
		ListUsers.class,
		ListBillboards.class,
		GetUserDetails.class,
		Login.class,
		Logout.class,
		AddUser.class,
		UpdateBillboard.class,
		UpdateUser.class,
	};


	/***
	 * Constructor. Init the server here
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public Server() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		System.out.println("\nServer Starting...");
		propsReader = new ServerPropsReader();

		System.out.println("\nInitializing database...");
		db = new dbServer();
		db.setupDB();
		db.loadScheduleToMem();

		System.out.println("\nInitializing sockets...");

		System.out.println("ALL USERS : " + db.listUsers());

		serverSocket = new ServerSocket(propsReader.GetPort());
		socketHandler = new SocketHandler(serverSocket, db);


		// Create the Endpoints specified in allEndpoints
		System.out.println("\nCreating Endpoints...");
		try {
			for (Class c : allEndpoints) {
				Endpoint newEndpoint = (Endpoint) c.getDeclaredConstructor().newInstance();
				socketHandler.endpoints.add(newEndpoint);
				newEndpoint.init(this);
			}
		} catch (Exception e) {
			System.out.println("Error: Invalid Endpoint provided, check Server.allEndpoints variable");
		}


		// Remove in final build
		//testFunc();

		// Do this last!
		System.out.println("\nServer is ready!");
		socketHandler.Run(); // Not sure if this method call will ever return     (it shouldn't)
		db.closeResources(); // Close the DB
	}


	public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		Server server = new Server();
	}


	/**
	 * Put all of your experimental code here
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void testFunc() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		System.out.println("\nRunning test functions...");
		// Add data to DB
		//db.addUser("dylan", "faljnfkan", "salt1");
		//db.addUser("colby", "gggggddd", "salt2");

		Billboard billboard = new Billboard("sample_billboard", "sample", "sample2", Color.red, Color.blue, Color.white, null, "admin");
		db.addBillboard(billboard);

		Event event1 = new Event(10000000, 300000000, "bb_ex_1", "bob", 0);
		Event event2 = new Event(10000010, 300000001, "bb_ex_2", "jerry", 0);

		db.addEvent(event1);
		db.addEvent(event2);

		ArrayList<Event> example = db.returnEventList();

		System.out.println("THE AUTHOR IS " + example.get(0).author);
		System.out.println("THE AUTHOR IS " + example.get(1).author);

		//System.out.println("THE START TIME IS" + db.requestBillBoard(1));

		System.out.println("Added items");

		if (db.checkPassword("gggggddd", "colby")) {
			System.out.println("password check for 'gggggddd' and usr colby present");
		}

		if (!db.checkPassword("faljnfkan", "lionblind")) {
			System.out.println("password check for 'faljnfkan' and user lionblind NOT PRESENT");
		}

		if (!db.checkPassword("pogchamp", "pepehands")) {
			System.out.println("password check for 'pogchamp' and user pepehands NOT PRESENT");
		}

		if (db.checkUserExists("colby")) {
			System.out.println("colby exists");
		}

		if (!db.checkUserExists("fred")) {
			System.out.println("fred does not exist");
		}


		// Query data from DB
		String[] query = db.queryDB("USERS", "1", "usr_ID");
		String[] query2 = db.queryDB("BILLBOARDS", "1", "id");
		String[] query3 = db.queryDB("SCHEDULE", "1", "id");

		// Print data from DB
		for (int i = 0; i < query.length; i++) {
			System.out.println(query[i]);
		}
		for (int i = 0; i < query2.length; i++) {
			if (i == 0) {
				System.out.println(query2[i]);
			} else {
				//System.out.println("billboard : " +ObjectSerialization.fromString(query2[i]));
				Object obj = ObjectSerialization.fromString((query2[i]));
				Billboard bbPrint = Billboard.class.cast(obj);

				System.out.println("name : " + bbPrint.name);
			}
		}
		for (int i = 0; i < query3.length; i++) {
			System.out.println(query3[i]);
		}

		// Remove data from DB
		//db.rmUser(1);
		//db.rmUser(2);
		//db.rmSchedule(1);
		//db.rmBillboard(1);

		System.out.println("Removed items");
	}
}
