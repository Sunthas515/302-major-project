package ControlPanel;

import Server.Endpoints.EndpointType;
import Shared.Credentials;
import Shared.Network.Response;
import Shared.Permissions.Permissions;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class User {
	public JPanel Users;
	private JButton btnNewUser;
	private JList lstNames;
	private JButton btnEdit;
	private JButton btnDeleteUser;

	public User() {
		/**
		 * Opens a form to create new users
		 *
		 * @author Callum McNeilage - n10482652
		 */
		btnNewUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new newUser("", new Permissions());
			}
		});

		/**
		 * Opens form to edit existing users
		 *
		 * @author Callum McNeilage - n10482652
		 */
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = (String) lstNames.getSelectedValue();
				System.out.println(selected);

				String username = ControlPanel.get().requestSender.getToken().getUser();
				//Query server to get user data
				Permissions perms;
				try {
					//Query server
					Response response = ControlPanel.get().requestSender.SendData(EndpointType.getUserDetails, selected);
					System.out.println(response);
					Credentials user = (Credentials) response.getData();
					System.out.println(user);
					perms = new Permissions(user.getPermissions());
					System.out.println(perms);

					new newUser(selected, perms);
				}
				catch (NullPointerException err) {
					System.out.println("No user data");
				} catch (IOException | ClassNotFoundException ex) {
					System.out.println("Error in response");
				}


			}
		});

		/**
		 * Deletes user from server
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		btnDeleteUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = (String) lstNames.getSelectedValue();
				System.out.println(selected);

				//Query server to delete user
				Permissions perms;
				try {
					//Query server
					Response response = ControlPanel.get().requestSender.SendData(EndpointType.deleteUser, selected);
					System.out.println(response);
					System.out.println(response.getData().toString().equals("true"));
					populate();
				}
				catch (NullPointerException err) {
					System.out.println("No user data");
				} catch (IOException | ClassNotFoundException ex) {
					System.out.println(ex.getStackTrace());
					System.out.println("Error in response");
				}
			}
		});

		/**
		 * Enables buttons for user functions when a user is selected
		 *
		 * @author Lucas Maldonado - n10534342
		 */
		lstNames.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()){
					JList source = (JList) e.getSource();
					String selected = source.getSelectedValue().toString();
					btnEdit.setEnabled(true);

					String currentUser = ControlPanel.get().requestSender.getToken().getUser();

					btnDeleteUser.setEnabled(!selected.equals(currentUser));
				}
			}
		});

		btnEdit.setEnabled(false);
		btnDeleteUser.setEnabled(false);
		populate();
	}

	/**
	 * Queries server for users
	 *
	 * @author Callum McNeilage - n10482652
	 */
	public void populate() {
		// Query users table
		ArrayList<String> users = null;
		try {
			Response request = ControlPanel.get().requestSender.SendData(EndpointType.listUsers, null);
			// Check if the server replied with an error
			if (request.getStatus().equals("error")) {
				if (request.getData().equals("Invalid token")) {
					System.out.println("Your session token is invalid (probably expired), try logging in again");
				}
				else {
					System.out.println("Server replied with an error: " + request.getData().toString());
				}
				return;
			}

			if (request.getData() instanceof ArrayList) {
				users = (ArrayList<String>) request.getData();
				System.out.println(users);
			} else if(request.getData().toString().equals("")){
				System.out.println("No users to show");
			}
			else {
				System.out.printf("Server replied with unexpected data: '%s'\n", request.getData().toString());
				return;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("There was an issue when retrieving users");
		}

		//User data to be displayed
		String[] ListData = new String[users.size()];
		for (int i = 0; i < users.size(); i++) {
			ListData[i] = users.get(i);
		}

		//Add users to list
		lstNames.setListData(ListData);
	}


	/**
	 * Loads User list window
	 *
	 * @author Callum McNeilage - n10482652
	 * @param args
	 */
	public static void main(String[] args) {
		// Won't compile without the exceptions unhandled
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
			// handle exception
		}

		// Create and setup Users window
		JFrame usersFrame = new JFrame("Users");
		usersFrame.setContentPane(new User().Users);
		usersFrame.setDefaultCloseOperation(usersFrame.HIDE_ON_CLOSE);
		usersFrame.pack();
		usersFrame.setLocationRelativeTo(ControlPanel.get());
		usersFrame.setVisible(true);
	}
}
