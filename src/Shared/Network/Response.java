package Shared.Network;

import java.io.Serializable;

/**
 * Response is a class which is returned after
 * sending a request to the server.
 *
 * @author Colby Derix n10475991
 */
public class Response implements Serializable {
	String _status;
	Object _data;
	Token _newToken;

	/**
	 * Request Constructor
	 * @param status The status of the request
	 * @param data The raw data that the server returned
	 */
	public Response(String status, Object data, Token newToken) {
		_status = status;
		_data = data;
		_newToken = newToken;
	}

	public String getStatus() {
		return _status;
	}

	public Object getData() {
		return _data;
	}

	public Token getNewToken() { return _newToken; }

	public String toString() {
		return String.format("{status: \"%s\", data: \"%s\", newTokenProvided: %b}", _status, _data, (_newToken != null));
	}
}
