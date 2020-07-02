package Shared.Network;

import Server.Endpoints.EndpointType;

import java.io.Serializable;

/**
 * Request is a class which should be used when
 * sending requests to the server.
 *
 * @author Colby Derix n10475991
 */
public class Request implements Serializable {
	Token _token;
	EndpointType _endpoint;
	Object _data;

	/**
	 * Request Constructor
	 * @param token The users's token (`null` for users who are not logged in)
	 * @param action Which action on the server to invoke
	 * @param data The raw data the server will receive, passed to the action
	 */
	public Request(Token token, EndpointType action, Object data) {
		_token = token;
		_endpoint = action;
		_data = data;
	}

	public Token getToken() {
		return _token;
	}

	public EndpointType getEndpoint() {
		return _endpoint;
	}

	public Object getData() {
		return _data;
	}

	public String toString() {
		return String.format("{token: \"%s\", endpoint: \"%s\", data: \"%s\"}", _token, _endpoint, _data);
	}
}
