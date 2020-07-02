package Server;

import Shared.Billboard;
import Shared.Credentials;
import Shared.Schedule.*;
import Shared.Permissions.Permissions;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

/***
 * handles the server side and database
 * @author Colby Derix n10475991
 * @author Dylan Robertson n10487310
 *
 */
public class dbServer {


	Connection cn = null;
	Statement stmt = null;
	ResultSet rs = null;

	//  Database credentials
	static String USER = "x";
	static String PASS = "x";

	/**
	 * set up the database
	 */
	Schedule schedule = new Schedule();

	/***
	 * run on start to create the database
	 * and other initialisations
	 */
	public void setupDB() {
		//create reader object
		ServerPropsReader reader = new ServerPropsReader();

		USER = reader.GetUsername();
		PASS = reader.GetPassword();

		try {
			//Open a connection
			System.out.println("Connecting to a selected database...");
//			"jdbc:sqlite:main.db"
			ServerPropsReader props = new ServerPropsReader();
			cn = DriverManager.getConnection(props.GetURL()+":"+props.GetSchema());
			stmt = cn.createStatement();

			System.out.println("Connected database successfully...");

			//create tables
			String usr_sql =
				"CREATE TABLE IF NOT EXISTS USERS (" +
					"usr_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
					"usr_Name TEXT NOT NULL UNIQUE," +
					"pw_Hash TEXT NOT NULL," +
					"salt TEXT NOT NULL UNIQUE," +
					"permissions INTEGER" +
					");";

			String bb_sql =
				"CREATE TABLE IF NOT EXISTS BILLBOARDS (" +
					"name TEXT PRIMARY KEY NOT NULL," +
					"data TEXT NOT NULL" +
					");";

			String schedule_sql =
				"CREATE TABLE IF NOT EXISTS SCHEDULE (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
					"data TEXT NOT NULL" +
					");";

			stmt.executeUpdate(usr_sql);

			Base64.Encoder base64 = Base64.getEncoder();
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] hash = md.digest("secure_password".getBytes());
			String hashed_password = new String(base64.encode(hash));

			if (addUser("admin", hashed_password, 15)) {
				System.out.println("Default user added, username `admin`, password `secure_password` ");
			}

			stmt.executeUpdate(bb_sql);
			stmt.executeUpdate(schedule_sql);

		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		}

	}//end main

	/***
	 * lists all the users in the database
	 * @return returns an array list of strings
	 */
	public ArrayList<Credentials> listUsers() {
		ArrayList<Credentials> users = new ArrayList<>();
		String sql = "SELECT * FROM USERS";

		ResultSet rs;

		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				users.add(new Credentials(rs.getString("usr_Name"), null, new Permissions(rs.getInt("permissions"))));
			}
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		return users;
	}

	/***
	 * lists all the billboards in the database
	 * @return returns an array list of strings
	 */
	public ArrayList<Billboard> listBillboards() {
		ArrayList<Billboard> billboards = new ArrayList<>();
		String sql = "SELECT * FROM BILLBOARDS";

		ResultSet rs;

		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				Billboard billboard = (Billboard) ObjectSerialization.fromString(rs.getString("data"));
				billboards.add(new Billboard(name, null, null, null, null, null, null, billboard.author));
			}
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		return billboards;
	}

	/***
	 * Method that loads the saved database schedule to memory to edit later
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadScheduleToMem() throws IOException, ClassNotFoundException {
		//query the database for the only object the SCHEDULE table stores
		String[] query = queryDB("SCHEDULE", "1", "id");

		schedule = new Schedule();

		System.out.println("should be empty : " + schedule.exportEvents().size());

		// [0] is the name of the billboard [1] is the billboard object
		if (query[1] != null) {
			//converts database stored string to object
			Object obj = ObjectSerialization.fromString((query[1]));

			//casts the object fetched from the database to Schedule class
			schedule = (Schedule) obj;
		}

		System.out.println("after fetching db :" + schedule.exportEvents().size());

	}

	/***
	 *runs the sql given in string
	 * @param sql a string containing sql
	 * @return return true if sql ran successfully else return false
	 */

	public boolean runSql(String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			return false;
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/***
	 *
	 * @return returns a list of all the events
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ArrayList<Event> returnEventList() throws IOException, ClassNotFoundException {
		schedule.getCurrentEvent();
		ArrayList<Event> test = requestEvents();
		return test;
	}

	/***
	 *  adds an event to the schedule, and saves the schedule to the database
	 * @param event the event to add
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void addEvent(Event event) throws IOException, ClassNotFoundException {
		//create a new event and add it to the memory Schedule
		schedule.scheduleEvent(event);

		//save memory schedule to database
		saveSchedule(schedule);
	}

	/***
	 * removes an event from schedule and saves schedule to the database
	 * @param event the event to add
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public boolean rmEvent(Event event, boolean future) throws IOException, ClassNotFoundException {
		//create a new event and add it to the memory Schedule
		boolean returnValue = schedule.removeEvent(event, true, future);

		//save memory schedule to database
		saveSchedule(schedule);

		return returnValue;
	}

	/***
	 *
	 * @param sql the sql for the query in the form of a string
	 * @param column_size the column size of a given table
	 * @return array of strings of the queried data
	 */
	public String[] querySql(String sql, int column_size) {
		ResultSet rs;
		String[] stringArray = new String[column_size];

		Object obj;

		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				for (int j = 0; j < column_size; j++) {
					obj = rs.getObject(j + 1);
					stringArray[j] = String.valueOf(obj);
				}
			}
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		return stringArray;
	}

	/***
	 * checks if a user exists
	 * @param usr string containing username
	 * @return
	 */
	public boolean checkUserExists(String usr) {
		String[] dbpw = queryDB("USERS", usr, "usr_Name");
		return dbpw[1] != null;
	}

	/***
	 *checks password hash and user
	 * @param pw the password hash we are checking is valid
	 * @return boolean true if password is valid, false if not valid
	 */
	public boolean checkPassword(String usr, String pw) throws NoSuchAlgorithmException {
		System.out.println(usr);
		String[] dbsalt = queryDB("USERS", usr, "usr_Name");
		String salt = null;
		if (dbsalt[3] != null) {
			salt = dbsalt[3];
		} else {
			return false;
		}

		String combined = pw + salt;

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(combined.getBytes());

		Base64.Encoder base64 = Base64.getEncoder();
		String final_hash = new String(base64.encode(hash));

		String[] dbpw = queryDB("USERS", final_hash, "pw_Hash");
		if (dbpw[2] != null && dbpw[1] != null) {
			if (dbpw[2].equals(final_hash) && dbpw[1].equals(usr)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/***
	 * returns credentials for a specific user
	 * @param usr the username to check
	 * @return credentials object
	 */
	public Credentials getUserDetails(String usr) {
		System.out.println(usr);
		String[] dbuser = queryDB("USERS", usr, "usr_Name");
		if (dbuser[4] != null) {
			return new Credentials(dbuser[1], null, new Permissions(Integer.parseInt(dbuser[4])));
		} else {
			return new Credentials(null, null, null);
		}
	}

	/***
	 * returns permissions for a given user
	 * @param usr the username to check
	 * @return permissions object
	 */
	public Permissions getPermissions(String usr) {
		System.out.println(usr);
		String[] dbperms = queryDB("USERS", usr, "usr_Name");
		int perms = -1;
		if (dbperms[4] != null) {
			perms = Integer.parseInt(dbperms[4]);
		}
		return new Permissions(perms);
	}

	/***
	 *
	 * @param table_Name the name of the table you wish to pull from
	 * @param value the value of the primary key
	 * @param name the name of the primary key
	 * @return returns the queried data in the form of a string array
	 */
	public String[] queryDB(String table_Name, String value, String name) {
		value = "'" + value + "'";

		int column_size = 0;

		if (table_Name == "USERS") {
			column_size = 5;
		} else if (table_Name == "BILLBOARDS") {
			column_size = 2;
		} else if (table_Name == "SCHEDULE") {
			column_size = 2;
		}

		String sql =
			"SELECT * FROM " + table_Name + " WHERE " + name + " = " + value + ";";
		return querySql(sql, column_size);
	}

	/***
	 * update a user in the database
	 * @param usr_Name string storing the username of the user
	 * @param pw_Hash string storing the hashed password of the user
	 * @param usr_Perms int storing permissions for the new user
	 * @return true if sql ran successfully else false
	 */
	public boolean updateUser(String usr_Name, String pw_Hash, int usr_Perms) throws NoSuchAlgorithmException {
		if (!checkUserExists(usr_Name)) {
			return false;
		}

		String sql = null;
		if (pw_Hash != null) {
			SecureRandom secureRandom = new SecureRandom();
			byte[] byes_salt = new byte[64];
			secureRandom.nextBytes(byes_salt);
			Base64.Encoder base64 = Base64.getEncoder();
			String encoded_salt = new String(base64.encode(byes_salt));

			String combined = pw_Hash + encoded_salt;

			String final_hash = null;

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(combined.getBytes());

			final_hash = new String(base64.encode(hash));

			sql = "UPDATE USERS SET" +
				" pw_hash = '" + final_hash + "' , salt = '" + encoded_salt + "' , permissions = " + usr_Perms +
				" WHERE usr_name = '" + usr_Name + "'";
		} else {
			sql = "UPDATE USERS SET" +
				" permissions = " + usr_Perms +
				" WHERE usr_name = '" + usr_Name + "'";
		}
		return runSql(sql);
	}

	/***
	 * adds a user to the database
	 * @param usr_Name string storing the username of the user
	 * @param pw_Hash string storing the hashed password of the user
	 * @param usr_Perms int storing permissions for the new user
	 * @return true if sql ran successfully else false
	 */
	public boolean addUser(String usr_Name, String pw_Hash, int usr_Perms) throws NoSuchAlgorithmException {
		if (checkUserExists(usr_Name)) {
			return false;
		}

		SecureRandom secureRandom = new SecureRandom();
		byte[] byes_salt = new byte[64];
		secureRandom.nextBytes(byes_salt);
		Base64.Encoder base64 = Base64.getEncoder();
		String encoded_salt = new String(base64.encode(byes_salt));

		String combined = pw_Hash + encoded_salt;

		String final_hash = null;

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(combined.getBytes());

		final_hash = new String(base64.encode(hash));

		String sql =
			"INSERT INTO USERS (usr_Name, pw_Hash, salt, permissions)" +
				"VALUES ( '" + usr_Name + "' , '" + final_hash + "' , '" + encoded_salt + "' , " + usr_Perms + ")";
		return runSql(sql);
	}

	/***
	 * drops a user based on the users ID
	 * @param usr_name integer storing the ID of the user (PK)
	 * @return true if sql ran successfully else false
	 */
	public boolean rmUser(String usr_name) {
		String sql =
			"DELETE FROM USERS WHERE usr_name = '" + usr_name + "'";
		return runSql(sql);
	}

	/***
	 * fetches schedule from database
	 * @return returns list of events (call returnEventList to get list)
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ArrayList<Event> requestEvents() throws IOException, ClassNotFoundException {
		//query the database for the schedule object
		String[] query = queryDB("SCHEDULE", "1", "id");
		System.out.println("QUERY LENGTH " + query.length + " id = " + query[0] + " string = " + query[1]);
		Schedule schedule = new Schedule();

		//[0] is name [1] is schedule object
		if (query[1] != null) {
			//get the database object, convert it to schedule
			schedule = (Schedule) ObjectSerialization.fromString((query[1]));
		}

		return schedule.exportEvents();
	}

	/***
	 * gets the current active event
	 * @return returns Event
	 */
	public Event requestCurrentEvent() {
		return schedule.getCurrentEvent();
	}

	/***
	 * fetches billboard from database
	 * @return returns billboard from a given ID
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Billboard requestBillboard(String name) throws IOException, ClassNotFoundException {
		//gets the database billboard object
		String[] query = queryDB("BILLBOARDS", name, "name");

		System.out.println("QUERY LENGTH " + query.length + " name = " + query[0] + " string = " + query[1]);

		Billboard billboard = null;
		if (query[1] != null) {
			//converts database billboard object to the BillboardEditor class
			billboard = (Billboard) ObjectSerialization.fromString((query[1]));
		}

		return billboard;
	}

	/***
	 * checks if a billboard exists
	 * @param name string containing name
	 * @return
	 */
	public boolean checkBillboardExists(String name) {
		String[] query = queryDB("BILLBOARDS", name, "name");
		return query[1] != null;
	}

	/***
	 * updates a billboard with a new billboard
	 * @param billboard
	 * @return
	 * @throws IOException
	 */
	public  boolean updateBillboard(Billboard billboard) throws IOException {
		if(!checkBillboardExists(billboard.name)) {
			return false;
		}

		//converts the billboard object to a string to save into the database
		String data = ObjectSerialization.toString((Serializable) billboard);

		String sql =
			"UPDATE BILLBOARDS" +
				" SET data = '" + data +"'"+
				" WHERE name = '" + billboard.name+"'";

		System.out.println("THE SQL :" + sql);

		return runSql(sql);
	}

	/***
	 * adds a billboard to the database
	 * @param billboard billboard object
	 * @return true if sql ran successfully else false
	 */
	public boolean addBillboard(Billboard billboard) throws IOException {
		if (checkBillboardExists(billboard.name)) {
			return false;
		}

		//converts the billboard object to a string to save into the database
		String data = ObjectSerialization.toString((Serializable) billboard);
		String sql =
			"INSERT INTO BILLBOARDS (name, data)" +
				"VALUES ('" + billboard.name + "', '" + data + "' )";
		return runSql(sql);
	}

	/***
	 * removes a billboard from the database
	 * @param name unique identification for the billboard (PK)
	 * @return true if sql ran successfully else false
	 */
	public boolean rmBillboard(String name) {
		String sql =
			"DELETE FROM BILLBOARDS WHERE name = '" + name + "'";
		return runSql(sql);
	}

	/***
	 * adds a schedule to the database
	 * @param schedule schedule object
	 * @return true if sql ran successfully else false
	 */
	public boolean saveSchedule(Schedule schedule) throws IOException {
		String data = ObjectSerialization.toString((Serializable) schedule);
		String sql =
			"INSERT OR REPLACE INTO SCHEDULE (id,data)" +
				"VALUES (1, '" + data + "')";
		return runSql(sql);
	}

	/***
	 * removes a schedule from the database
	 * @param bb_ID the id of the billboard the image will be displayed to(pk)
	 * @return true if sql ran successfully else false
	 */
	public boolean rmSchedule(int bb_ID) {
		String sql =
			"DELETE FROM SCHEDULE WHERE bb_ID = '" + bb_ID + "'";
		return runSql(sql);
	}

	/**
	 * close resources once finished with database
	 */
	public void closeResources() {
		//finally block used to close resources
		try {
			if (stmt != null) {
				cn.close();
			}
		} catch (SQLException se) {
		}// do nothing
		try {
			if (cn != null) {
				cn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}//end finally try
	}

}


