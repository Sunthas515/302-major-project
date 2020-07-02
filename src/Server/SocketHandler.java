package Server;

import Server.Endpoints.Endpoint;
import Server.Endpoints.InvalidEndpoint;
import Shared.Network.*;
import Shared.Permissions.Perm;
import Shared.Permissions.Permissions;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;

/**
 * SocketHandler is a class which - given a ServerSocket - can endlessly
 * process and respond to requests provided over the network.
 *
 * @author Colby Derix n10475991
 */
public class SocketHandler {

	ServerSocket serverSocket;
	dbServer db;
	ArrayList<Token> tokens = new ArrayList<Token>();
	ArrayList<Endpoint> endpoints = new ArrayList<Endpoint>();

	/**
	 * SocketHandler Constructor
	 * @param inputSocket The ServerSocket to run on
	 * @param inputDB The database instance
	 */
	public SocketHandler(ServerSocket inputSocket, dbServer inputDB) {
		serverSocket = inputSocket;
		db = inputDB;
	}

	/***
	 * Endlessly process and respond to requests
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void Run() throws IOException, ClassNotFoundException {
		while (true) { // Endlessly process connections
			Socket socket = serverSocket.accept(); // Open the connection
			System.out.println("Incoming connection from: " + socket.getInetAddress());

			// Setup streams
			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

			// Read the request
			Object raw = objectInputStream.readObject();

			Request request = null;
			Response response = new Response("error", "An unknown error occurred", null);
			try {
				// Cast request to Request class
				request = (Request) raw;

				// Generate a response
				response = CalculateResponse(request);
				System.out.println(tokens);
			} catch (java.lang.ClassCastException e) {
				response = new Response("error", "Malformed request (should be class Response)", null);
			} finally {
				System.out.println("Received: " + request);
				objectOutputStream.writeObject(response);
				System.out.println("Sent: " + response);

				socket.close(); // End the connection
			}
		}
	}

	/**
	 * Generate a response for a given request
	 */
	private Response CalculateResponse(Request request) throws IOException, ClassNotFoundException {
		System.out.println();
		Endpoint invokedEndpoint = null;
		System.out.printf("Received request for action: %s\n", request.getEndpoint().toString());

		// Search for the requested action
		for (Endpoint a : endpoints) {
			if (a.associatedEndpoint == request.getEndpoint()) {
				invokedEndpoint = a;
			}
		}

		if (invokedEndpoint == null) {
			invokedEndpoint = new InvalidEndpoint();
		}

		return invokedEndpoint.Run(request);
	}


	/**
	 * Checks if a token is valid or not. Provides a reason why the token was not accepted.
	 * If the token is valid, will renew it.
	 * If the token is expired, will remove it from the token array.
	 *
	 * @param token the token to examine
	 * @return valid if usable, invalid or expired otherwise
	 */
	public TokenStatus validateToken(Token token) {
		if (tokens.contains(token)) {
			if (!db.checkUserExists(token.getUser())) {
				removeToken(token);
				return TokenStatus.invalid;
			}

			if (token.getExpires() >= Instant.now().getEpochSecond()) {
				// token is valid
				return TokenStatus.valid;
			} else {
				// token has expired
				removeToken(token);
				return TokenStatus.expired;
			}
		} else {
			// token is invalid
			return TokenStatus.invalid;
		}
	}

	private Token addToken(Token token) {
		tokens.add(token);
		return token;
	}

	private Token removeToken(Token token) {
		tokens.remove(token);
		return token;
	}

	public Token resetExpire(Token token) {
		System.out.println("RESETTING TOKEN, OLD: " + token);
		Token tempToken = removeToken(token);
		addToken(tempToken.resetExpire());
		System.out.println("FINISHED RESET TOKEN, NEW: " + tempToken);
		return tempToken;
	}

	public Token generateToken(String username) {
		// Generate a random array of bytes, and encode it to base64 text
		SecureRandom secureRandom = new SecureRandom();
		byte[] values = new byte[128];
		secureRandom.nextBytes(values);
		Base64.Encoder base64 = Base64.getEncoder();
		String encoded = new String(base64.encode(values));
		// Instantiate a new token object, store it, and return it
		Token token = new Token(username, Instant.now().getEpochSecond() + 86400, encoded);
		addToken(token);
		return token;
	}

	public boolean logout(Token token) {
		if (tokens.contains(token)) {
			removeToken(token);
			return true;
		} else {
			return false;
		}
	}

	public boolean hasPerm(String username, Perm perm) {
		Permissions permissions =  db.getPermissions(username);
		System.out.println(permissions);
		return permissions.hasPermission(perm);
	}
}
