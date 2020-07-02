package Shared;

/***
 * used for reading the props file
 */
public class ClientPropsReader extends PropsReader {

	/** Path to client.props file */
	private static final String clientPropsFileLocation = "ClientConfig/";

	/** Name of the client.props file */
	private static final String clientPropsFileName= "client.props";


	public String getIPAddress() {
		return GetStringProperty("ip address", "localhost");
	}

	public int getPort() {
		return GetIntProperty("port", 9977);
	}


	/** Constructor */
	public ClientPropsReader(){
		super();
		// Put all of the default values here
		defaultKeysValues.put("ip address", "localhost");
		defaultKeysValues.put("port", "9977");
		ReadPropFile(clientPropsFileLocation, clientPropsFileName);
	}
}
