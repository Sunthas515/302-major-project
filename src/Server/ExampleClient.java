package Server;

import Server.Endpoints.EndpointType;
import Shared.Billboard;
import Shared.Credentials;
import Shared.Network.RequestSender;
import Shared.Network.Response;
import Shared.Permissions.Permissions;
import Shared.Schedule.Event;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * ExampleClient is a class which can send a single request
 * and print the response provided back by a server.
 *
 * The intended use is debugging.
 *
 * @author Colby Derix n10475991
 */
public class ExampleClient {
	public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
		// Define server address
		String serverIP = "localhost";
		int serverPort = 9977;

		// Create RequestSender
		RequestSender requestSender = new RequestSender(serverIP, serverPort);

		Credentials admin_creds = new Credentials("admin", "secure_password", null);
		Credentials user_creds = new Credentials("user", "password", null);

		Credentials selected = admin_creds;

		// Login
		System.out.println("Logging in with: " + selected);
		Response response = requestSender.login(selected);
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		System.out.println("Sending: " + requestSender.toString("getUserDetails", "admin"));
		response = requestSender.SendData(EndpointType.getUserDetails, "admin");
		System.out.println("Response: " + (response));

		System.out.println();
		Thread.sleep(1500);

		// Register new account
		String password2 = "password";

		Credentials newUser = new Credentials("user", password2, new Permissions(0));

		System.out.println("Sending: " + requestSender.toString("register", newUser));
		response = requestSender.SendData(EndpointType.addUser, newUser);
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		System.out.println("Sending: " + requestSender.toString("listUsers", null));
		response = requestSender.SendData(EndpointType.listUsers, null);
		System.out.println("Response: " + (response));
		System.out.println("List of users: " + response.getData());

		System.out.println();
		Thread.sleep(1500);

		Billboard billboard = new Billboard("example_billboard_illegal", "sample", "sample2", Color.red, Color.blue, Color.white, null, "illegal");
		System.out.println("Sending: " + requestSender.toString("addBillboard", billboard));
		response = requestSender.SendData(EndpointType.addBillboard, billboard);
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		System.out.println("Sending: " + requestSender.toString("listBillboards", null));
		response = requestSender.SendData(EndpointType.listBillboards, null);
		System.out.println("Response: " + (response));
		System.out.println("List of billboards: " + response.getData());

		System.out.println();
		Thread.sleep(1500);

		// Print the payload to console, send it to the server, amd print the response
		System.out.println("Sending: " + requestSender.toString("echo", "hello world!"));
		response = requestSender.SendData(EndpointType.echo, "hello world!");
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		// Get list of events
		System.out.println("Sending: " + requestSender.toString("GetEvents", null));
		response = requestSender.SendData(EndpointType.getEvents, null);
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		// Add event
		System.out.println("Sending: " + requestSender.toString("AddEvents", null));
		Event eventObj = new Event(10000, 20000, "bb_1", "hi", 0);
		response = requestSender.SendData(EndpointType.addEvents, eventObj);
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		// Get list of events again
		System.out.println("Sending: " + requestSender.toString("GetEvents", null));
		response = requestSender.SendData(EndpointType.getEvents, null);
		System.out.println("Response: " + response);

		System.out.println();
		Thread.sleep(1500);

		// Logout
		System.out.println("Logging out");
		response = requestSender.logout();
		System.out.println("Response: " + response);
	}
}
