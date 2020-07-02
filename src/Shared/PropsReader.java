package Shared;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * PropsReader is short for Properties File Reader. This class can read data from files, as long as it's formatted
 * correctly. The formatting just asks for a unique key to the left of a '=' or ':', with the value on the right:
 * myKey: value
 * Another key = value
 *
 * Keys must be unique, and they are case-insensitive.
 * Values can be anything, putting ':' or '=' in them isn't a problem
 *
 * To use a PropsReader, you must first instantiate one, tell it which file to use, then access your value using the
 * provided helper functions:
 * PropsReader reader = new PropsReader();
 * reader.ReadPropFile("relative/path/to/file.props");
 * reader.GetStringValue("myKey", "default value");
 *
 * There's also GetIntValue and GetBoolValue that auto convert your value to the correct type.
 *
 * @author Lucas Maldonado n10534342
 */
public class PropsReader {
	/**
	 * Try to get a property from the file.
	 * @param propName Name of the property you want
	 * @param defaultValue What to return if we can't find the property
	 * @return returns prop string
	 */
	public String GetStringProperty(String propName, String defaultValue)
	{
		String value = propMap.getOrDefault(propName.toLowerCase(), defaultValue);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Try to get a property from the file.
	 * @param propName Name of the property you want
	 * @param defaultValue What to return if we can't find the property
	 * @return int, the default value
	 */
	public int GetIntProperty(String propName, int defaultValue)
	{
		String defaultValueString = Integer.toString(defaultValue);
		String value = propMap.getOrDefault(propName.toLowerCase(), defaultValueString);
		try{
			return Integer.parseUnsignedInt(value);
		} catch (NumberFormatException e) {
			Log.warning("Could not parse '" + value + "' as int for '" + propName + "' key. " +
				"Using default value: " + defaultValueString);
			return defaultValue;
		}
	}

	/**
	 * Try to get a property from the file.
	 * @param propName Name of the property you want
	 * @param defaultValue What to return if we can't find the property
	 * @return boolean, the default value
	 */
	public boolean GetBoolProperty(String propName, boolean defaultValue)
	{
		propName = propName.toLowerCase();
		if (propMap.containsKey(propName)) {
			String value = propMap.get(propName).toLowerCase();
			if (value == "false" || value == "f" || value == "no" || value == "n") {
				return false;
			} else if (value == "true" || value == "t" || value == "yes" || value == "y"){
				return true;
			} else {
				Log.warning("Could not parse '" + value + "' as bool for" + propName + "key. " +
					"Using default value: " + (defaultValue ? "true" : "false"));
			}
		}
		return defaultValue;
	}

	/**
	 * Reads a .props file and returns a HashMap of keys and values.
	 * The .props file should be a list of keys and values, like this:
	 * 	username = admin
	 * 	password = p@ssw0rd
	 * 	this=works too
	 * 	and= values with = signs
	 * 	or: this
	 * That will then get placed into a HashMap for easy access.
	 * Note that the key (left side) will always be converted to lowercase.
	 * @param fileName string containing the file name
	 * @param filePath string containing the file path
	 * @author Lucas Maldonado N10534342
	 */
	public void ReadPropFile(String filePath, String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(GetCWD() + "/" + filePath + fileName));
			String line = reader.readLine();
			while (line != null) {
				try {
					String[] splitLine = line.split(" ?=|: ?", 2);
					if (splitLine.length < 2) {
						throw new Exception(line);
					}
					propMap.put(splitLine[0].toLowerCase(), splitLine[1].trim());
				} catch (Exception e) {
					// Badly formatted lines can be ignored, empty lines aren't reported at all
					if (!line.isEmpty()) {
						Log.info("Ignored badly formatted line: '" + e.getMessage() + "'");
					}
				}

				// Read next line, do this last
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file: '" + filePath + fileName + "'. Creating it...");

			try {
				createPropsFile(filePath, fileName);
				ReadPropFile(filePath, fileName); // try again
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createPropsFile(String filePath, String fileName) throws IOException {
		StringBuilder fileContents = new StringBuilder();
		for (Map.Entry<String, String> entry : defaultKeysValues.entrySet()) {
			fileContents.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
		}

		File file = new File(filePath);
		file.mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(GetCWD() + "/" + filePath + fileName));
		writer.write(fileContents.toString());
		writer.close();
		System.out.println("Default props file created successfully.");
	}

	/***
	 *
	 * Gets the Current Working Directory of the server. This is the absolute path to where the application was started.
	 * When debugging, it will return the folder in which the entire project is in, something like:
	 * C:\Users\maldo\Projects\CAB302-JavaProject
	 * Note that the final slash is absent, remember to add it in yourself.
	 * Also, Java uses backslashes internally (/), but will display forward slashes on Windows (like in example above).
	 * @return string containing property
	 */
	public static String GetCWD(){
		return System.getProperty("user.dir");
	}

	/** The map we store the contents of the prop file in, for easy access. Use this for more control */
	private HashMap<String, String> propMap = new HashMap<>();

	/** The default keys and values expected to be found in the props file */
	public HashMap<String, String> defaultKeysValues = new HashMap<>();

	/** Log class for logging PropsReader specific messages with more helpful info */
	private static final Logger Log = Logger.getLogger(PropsReader.class.getName());
}
