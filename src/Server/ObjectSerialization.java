package Server;

import java.io.*;
import java.util.Base64;

/***
 * used to convert objects to and from strings
 * so that objects may be saved and retrieved
 * from the database
 */
public class ObjectSerialization {
	/***
	 * converts a string to a java object
	 * @param input string
	 * @return Object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object fromString(String input) throws IOException, ClassNotFoundException {
		byte [] data = Base64.getDecoder().decode(input);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object output = ois.readObject();
		ois.close();
		return output;
	}

	/***
	 * converts an object to a string
	 * @param object Serializable
	 * @return string
	 * @throws IOException
	 */
	public static String toString(Serializable object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
}
