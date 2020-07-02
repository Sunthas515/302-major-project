package ControlPanel;

import Server.Endpoints.EndpointType;
import Shared.Billboard;
import Shared.ClientPropsReader;
import Shared.Credentials;
import Shared.Network.RequestSender;
import Shared.Network.Response;
import Shared.Permissions.Perm;
import Shared.Permissions.Permissions;
import Shared.Schedule.Event;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Graphical User Interface for main window form
 *
 * @author Lucas Maldonado - n10534342
 * @author Callum McNeilage - n10482652
 * @author Connor McHugh - n10522662
 */

public class ControlPanel extends JFrame {
	private JButton newBillboard_Button;
	private JButton changeYourPasswordButton;
	public JPanel mainPanel;
	private JTable schedule_Table;
	private JButton newEvent_Button;
	private JTable billboards_Table;
	private JTextPane billboardControlPanelV0TextPane;
	private JTable users_Table;
	private JButton createNewAccount_Button;
	private JPanel editUsers_Panel;
	private JButton logOut_Button;
	private JLabel permissionsList_Label;
	private JLabel numPermissions_Label;
	private JLabel yourUsername_Label;
	private JButton refreshUsers_Button;
	private JButton refreshBillboards_Button;
	private JButton refreshEvents_Button;

	// End UI Variables

	public ClientPropsReader propsReader;
	public RequestSender requestSender;
	public Permissions userPerms;


	/**
	 * Constructor of ControlPanel. Initialises many of the important classes, such as the PropsReader and
	 * RequestSender. The login process also starts from here.
	 *
	 * @author Lucas Maldonado - n10534342
	 */
	public ControlPanel() {
		System.out.println("Starting control panel...");

		// Appears as the name of the application
		setTitle("Billboard Control Panel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up important classes
		propsReader = new ClientPropsReader();
		requestSender = new RequestSender(
			// Give the requestSender connection info from the .props file
			propsReader.getIPAddress(),
			propsReader.getPort()
		);


		// Create the login window and display it
		LoginWindow loginWindow = new LoginWindow();
		setContentPane(loginWindow.loginWindow);
		setSize(800, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		System.out.println("Waiting for user to log in...");

		/**
		 * Opens the User form when editUsersButton is clicked
		 *
		 * @author Callum McNeilage - n10482652
		 */
		changeYourPasswordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new newUser(requestSender.getToken().getUser(), userPerms);
			}
		});


		/**
		 * Opens blank user window
		 *
		 * @author Callum McNeilage - n10482652
		 */
		createNewAccount_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new newUser("", null);
			}
		});

		/**
		 * Reloads billboards list. For use when you create a billboard so that you can view it in the application
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		refreshBillboards_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshBillboards();
			}
		});

		/**
		 * Reloads billboard schedule. For use when you add a billboard to the schedule so that it is viewable in application
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		refreshEvents_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshEvents();
			}
		});

		/**
		 * Reloads users list. For use when creating a new user so they are visible in application
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		refreshUsers_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshUsers();
			}
		});

		/**
		 * Logs the user out of the application.
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		logOut_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					requestSender.logout();
					dispose();
					main(null);
				} catch (IOException | ClassNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		});

		/**
		 * Opens New BillboardEditor form when newBillboard_Button is pressed
		 *
		 * @author Connor McHugh - n10522662
		 */
		newBillboard_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new BillboardEditor(null);
				//BillboardEditor.main(null, "New BillboardEditor");
			}
		});

		/**
		 * Adds billboard to schedule.
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		newEvent_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EventEditor(null);
			}
		});
	}

	/**
	 * Called by the loginWindow once the user has successfully logged in.
	 * The main window is created here.
	 *
	 * @author Lucas Maldonado - n10534342
	 */
	public void loggedIn() {
		System.out.println("Logged in successfully. Control panel is ready!");

		// Get the user's permissions
		try {
			//Query server
			Response response = requestSender.SendData(EndpointType.getUserDetails, requestSender.getToken().getUser());
			System.out.println(response);
			Credentials user = (Credentials) response.getData();
			System.out.println(user);
			userPerms = new Permissions(user.getPermissions());
		}
		catch (NullPointerException err) {
			System.out.println("No user data");
		} catch (IOException | ClassNotFoundException ex) {
			System.out.println("Error in response");
		}

		// Exit out of login screen
		setContentPane(mainPanel);
		setVisible(true); // Needs this to display properly
		setResizable(true);

		refreshBillboards();
		refreshEvents();
		refreshUsers();

		editUsers_Panel.setVisible(userPerms.hasPermission(Perm.EDIT_USERS));
	}

	/**
	 * Takes a date formatted as a long and converts it to Simple Date Format to display in schedule
	 *
	 * @author Callum McNeilage - n10482652
	 * @param unix date formatted as a long
	 * @return Date formatted as h:mm a	d/M/yyyy
	 */
	private String DateFormat(long unix) {
		Date date = new Date(unix);
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a \td/M/yyyy");
		return sdf.format(date);
	}


	/**
	 * Call to request the server for the billboards list again
	 *
	 * @author Lucas Maldonado - n10534342
	 */
	public void refreshBillboards() {
		try {
			Response response = ControlPanel.get().requestSender.SendData(EndpointType.listBillboards, null);
			ArrayList<Billboard> billboardNames = (ArrayList<Billboard>)response.getData();

			//Data to be displayed in the JTable
			billboards_Table.setModel(new DefaultTableModel());
			DefaultTableModel model = (DefaultTableModel) billboards_Table.getModel();
			model.addColumn("Billboard Name");
			model.addColumn("Author");

			String[] row = new String[2];

			for (Billboard billboard : billboardNames) {
				row[0] = billboard.name;
				row[1] = billboard.author;

				model.addRow(row);
			}



		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Queries the server for billboard information and displays in table
	 *
	 * @author Callum McNeilage - n10482652
	 */
	public void refreshEvents() {

		//Queries server to return billboard schedule
		ArrayList<Event> list = null;
		try {
			Response response = ControlPanel.get().requestSender.SendData(EndpointType.getEvents, null);

			// Check if the server replied with an error
			if (response.getStatus().equals("error")) {
				if (response.getData().equals("Invalid token")) {
					System.out.println("Your session token is invalid (probably expired), try logging in again");
				}
				else {
					System.out.println("Server replied with an error: " + response.getData().toString());
				}
				return;
			}

			if (response.getData() instanceof ArrayList) {
				list = (ArrayList<Event>) response.getData();
			} else if(response.getData().toString().equals("")){
				System.out.println("No billboards to show");
			}
			else {
				System.out.printf("Server replied with unexpected data: '%s'\n", response.getData().toString());
				return;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("There was an issue when retrieving billboards");
		}

		//Data to be displayed in the JTable
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Billboard");
		model.addColumn("Start Time");
		model.addColumn("Duration");
		model.addColumn("Repeats");
		model.addColumn("Scheduler");
		schedule_Table.setModel(model);

		Object[] row = new Object[5];
		for (Event event : list) {
			row[0] = event;
			row[1] = DateFormat(event.startTime);
			row[2] = event.getDuration() / (60 * 1000);
			row[3] = (event.loopInterval > 0 ? event.loopInterval / 1000 : "No");
			row[4] = event.author;

			model.addRow(row);
		}
	}

	/**
	 * Call to request the server for the users list again
	 *
	 * @author Lucas Maldonado - n10534342
	 */
	public void refreshUsers() {

		// Update the text section describing your account
		yourUsername_Label.setText("Logged in as: " + requestSender.getToken().getUser());
		numPermissions_Label.setText("You have " + userPerms.numPermissions() + " permission(s):");
		permissionsList_Label.setText(userPerms.toString());


		// Don't continue if the user can't see other users
		if (!userPerms.hasPermission(Perm.EDIT_USERS)) {
			changeYourPasswordButton.setText("Change Password");
			return;
		}

		changeYourPasswordButton.setText("Edit Your Account");

		//Data to be displayed in the JTable
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("User Name");
		model.addColumn("Num Permissions");
		model.addColumn("Create Billboards");
		model.addColumn("Schedule Billboards");
		model.addColumn("Edit All Billboards");
		model.addColumn("Edit Users");
		users_Table.setModel(model);

		// Get the list of users from the server

		try {

			ArrayList<Credentials> credentials = (ArrayList<Credentials>)requestSender.SendData(EndpointType.listUsers, null).getData();

			Object[] row = new Object[6];

			for (Credentials cred : credentials) {
				row[0] = cred.getUsername();
				Permissions p = new Permissions(cred.getPermissions());
				row[1] = p.numPermissions();
				row[2] = (p.hasPermission(Perm.CREATE_BILLBOARDS) ? "✓" : "");
				row[3] = (p.hasPermission(Perm.SCHEDULE_BILLBOARDS) ? "✓" : "");
				row[4] = (p.hasPermission(Perm.EDIT_ALL_BILLBOARDS) ? "✓" : "");
				row[5] = (p.hasPermission(Perm.EDIT_USERS) ? "✓" : "");

				model.addRow(row);
			}


		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Entry point for the control panel. Just sets the system Look and Feel to something
	 * that is actually pleasant to the eyes, and creates an instance of ControlPanel.
	 * Take a look at the constructor if you're looking for the actual initialisation.
	 *
	 * @author - Lucas Maldonado - n10534342
	 * @param args ControlPanel does not take in any command line arguments
	 */
	public static void main(String[] args) {
		// Set System L&F
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to set the system look and feel!");
		}
		globalInstance = new ControlPanel();
	}

	/** Reference to the main instance of the ControlPanel. Access using the static get method */
	private static ControlPanel globalInstance;

	/**
	 * Gets the main Control Panel instance.
	 * If ControlPanel.main() was called, this will never be null.
	 * Use this to give callbacks back to ControlPanel.
	 * @return the main control panel instance
	 */
	public static ControlPanel get() {
		return globalInstance;
	}

	private void createUIComponents() {
		users_Table = new JTable(){
			@Override
			public boolean editCellAt(int row, int column, java.util.EventObject e) {

				if (e instanceof KeyEvent) {
					KeyEvent ke = (KeyEvent)e;
					// Keycode 8 is backspace, 127 is delete
					if (ke.getKeyCode() == 8 || ke.getKeyCode() == 127) {
						String userName = (String) users_Table.getModel().getValueAt(row, 0);

						// Bring up a confirmation dialogue
						int n = JOptionPane.showConfirmDialog(
							this,
							"Are you sure you want to delete '" + userName
								+ "'?\nThis action is irreversible!",
							"Confirm Delete User",
							JOptionPane.YES_NO_OPTION);

						if (n == 0) { // If the user presses yes
							try {
								System.out.println("Delete user: " + userName);
								requestSender.SendData(EndpointType.deleteUser, userName);
								refreshUsers();
							} catch (IOException | ClassNotFoundException ex) {
								System.out.println(ex.getMessage());
							}
						}


					}
				} else if (e instanceof MouseEvent) { 	// Open up editing window if user double clicks an entry
					MouseEvent mouseEvent = (MouseEvent) e;
					if (mouseEvent.getClickCount() != 2) {
						return false;
					}

					String userName = (String) users_Table.getModel().getValueAt(row, 0);

					try {
						Credentials credentials = (Credentials) requestSender.SendData(EndpointType.getUserDetails, userName).getData();
						new newUser(userName, new Permissions(credentials.getPermissions()));
					} catch (IOException | ClassNotFoundException ex) {
						ex.printStackTrace();
					}
				}
				return false;
			}
		};


		billboards_Table = new JTable() {
			@Override
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				String billboardName = (String) billboards_Table.getModel().getValueAt(row, 0);

				System.out.println("Clicked on row " + row);

				if (!userPerms.hasPermission(Perm.EDIT_ALL_BILLBOARDS)) {
					// check if they created this billboard
					try {
						Billboard b = (Billboard)requestSender.SendData(EndpointType.getBillboard, billboardName).getData();
						if (!b.author.equals(requestSender.getToken().getUser())) {
							// The user is trying to edit someone else's board! Stop them!
							return false;
						}
					} catch (IOException | ClassNotFoundException ioException) {
						ioException.printStackTrace();
					}
				}

				if (e instanceof KeyEvent) {
					KeyEvent ke = (KeyEvent)e;
					// Keycode 8 is backspace, 127 is delete
					if (ke.getKeyCode() == 8 || ke.getKeyCode() == 127) {
						// Delete thing

						// Bring up a confirmation dialogue
						int n = JOptionPane.showConfirmDialog(
							this,
							"Are you sure you want to delete '" + billboardName
								+ "'?\nThis action is irreversible!",
							"Confirm Delete User",
							JOptionPane.YES_NO_OPTION);

						if (n == 0) {
							try {
								requestSender.SendData(EndpointType.deleteBillboard, billboardName);
							} catch (IOException | ClassNotFoundException ioException) {
								ioException.printStackTrace();
							}
							refreshBillboards();
							refreshEvents();
						}
					}
				} else if (e instanceof MouseEvent) {
					MouseEvent mouseEvent = (MouseEvent) e;
					// Only allow double clicks
					if (mouseEvent.getClickCount() != 2) {
						return false;
					}


					new BillboardEditor(billboardName);

				}



				return false;
			}
		};

		schedule_Table= new JTable() {
			@Override
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				Event event = (Event) schedule_Table.getModel().getValueAt(row, 0);
				if (e instanceof KeyEvent) {
					KeyEvent ke = (KeyEvent)e;
					if (ke.getKeyCode() == 8 || ke.getKeyCode() == 127) {
						// Bring up a confirmation dialogue
						int n = JOptionPane.showConfirmDialog(
							this,
							"Are you sure you want to delete '" + event.billboardName
								+ "'?\nThis action is irreversible!",
							"Confirm Delete User",
							JOptionPane.YES_NO_OPTION);

						if (n == 0) {
							// Bring up a confirmation dialogue
							int option = 1;
							if (event.loopInterval > 0) {option = JOptionPane.showConfirmDialog(
								this,
								"This is a looping billboard. Do you want to delete the entire loop?\n" +
									"Press yes to delete the whole loop.\n" +
									"Press no to only delete the upcoming event (loop is preserver)",
								"Confirm Delete User",
								JOptionPane.YES_NO_OPTION);}


							// Don't proceed if the user presses escape
							if (option != -1) {
								System.out.println("Delete event");
								// Delete thing
								Object[] data = new Object[2];
								data[0] = event;
								data[1] = option == 1;
								System.out.println(event.toString());
								try {
									requestSender.SendData(EndpointType.deleteEvent, data);
								} catch (IOException | ClassNotFoundException ioException) {
									ioException.printStackTrace();
								}
								refreshEvents();
							}
						}
					}
				} else if (e instanceof MouseEvent) {
					MouseEvent mouseEvent = (MouseEvent) e;
					if (mouseEvent.getClickCount() != 2) {
						// Only allow double clicks
						return false;
					}

					new EventEditor(event);
				}

				return false;
			}
		};
	}
}
