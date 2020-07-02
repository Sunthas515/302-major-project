package ControlPanel;

import Server.Endpoints.EndpointType;
import Shared.Credentials;
import Shared.Network.Response;
import Shared.Permissions.Perm;
import Shared.Permissions.Permissions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class newUser extends JFrame {
	public JPanel newUser;
	private JTextField username_TextField;
	private JTextField password_TextField;
	private JCheckBox chkCreate;
	private JCheckBox chkEdit;
	private JCheckBox chkSchedule;
	private JCheckBox chkEditUsers;
	private JButton okay_Button;
	private JLabel lblPassword;
	private JPanel permissions_Panel;

	boolean addUser; // True if this window is being used to create a new user

	/**
	 * Saves all user variables to database and closes window when OK is pressed
	 * @author Callum McNeilage - n10482652
	 * @param user See main()
	 * @param perms See main()
	 */
	public newUser(String user, Permissions perms) {
		setTitle("User Editor");
		setContentPane(newUser);
		setDefaultCloseOperation(HIDE_ON_CLOSE);


		username_TextField.setText(user);
		addUser = (user.equals(""));
		if (addUser) {
			lblPassword.setText("Password:");
		} else {
			// Disable the permission control panel if the user does not have EDIT_USERS perm
			if (!ControlPanel.get().userPerms.hasPermission(Perm.EDIT_USERS)) {
				chkCreate.setEnabled(false);
				chkEdit.setEnabled(false);
				chkSchedule.setEnabled(false);
				chkEditUsers.setEnabled(false);
				permissions_Panel.setVisible(false);
				pack();
			} else { // Set the checkboxes so they align with the user's perms
				if (perms.hasPermission(Perm.CREATE_BILLBOARDS)) {
					chkCreate.setSelected(true);
				}
				if (perms.hasPermission(Perm.EDIT_ALL_BILLBOARDS)) {
					chkEdit.setSelected(true);
				}
				if (perms.hasPermission(Perm.SCHEDULE_BILLBOARDS)) {
					chkSchedule.setSelected(true);
				}
				if (perms.hasPermission(Perm.EDIT_USERS)) {
					chkEditUsers.setSelected(true);
				}
				// Prevent users with EDIT_USERS from removing their own EDIT_USERS permission
				if (ControlPanel.get().requestSender.getToken().getUser().equals(username_TextField.getText())) {
					chkEditUsers.setEnabled(false);
				}
			}
		}

		username_TextField.setEditable(addUser);


		// Finish setting up window
		pack();
		setLocationRelativeTo(ControlPanel.get());
		setResizable(false);
		setVisible(true);


		/**
		 * Retrieves data from input fields and creates a new user in server
		 *
		 * @author Callum McNeilage
		 */
		okay_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = username_TextField.getText();
				String password = password_TextField.getText();
				if (password.equals("")) {
					password = null;
				}

				Permissions permissions = new Permissions();
				if (chkCreate.isSelected()) {
					permissions.addPermission(Perm.CREATE_BILLBOARDS);
				}
				if (chkEdit.isSelected()) {
					permissions.addPermission(Perm.EDIT_ALL_BILLBOARDS);
				}
				if (chkSchedule.isSelected()) {
					permissions.addPermission(Perm.SCHEDULE_BILLBOARDS);
				}
				if (chkEditUsers.isSelected()) {
					permissions.addPermission(Perm.EDIT_USERS);
				}

				Credentials credentials = new Credentials(username, password, permissions);

				EndpointType endpoint = null;
				if (addUser) {
					endpoint = EndpointType.addUser;
				} else {
					endpoint = EndpointType.updateUser;
				}

				try {
					Response response = ControlPanel.get().requestSender.SendData(endpoint, credentials);
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				}

				ControlPanel.get().refreshUsers();
				dispose();
			}
		});
	}
}
