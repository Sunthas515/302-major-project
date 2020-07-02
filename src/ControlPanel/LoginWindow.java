package ControlPanel;

import Shared.Credentials;
import Shared.Network.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LoginWindow {
	public JPanel loginWindow;
	private JLabel lblUsername;
	private JTextField enterUsernameTextField;
	private JLabel lblPassword;
	private JButton btnLogin;
	private JPasswordField enterPasswordPasswordField;
	private JLabel passIncorrect;

	// Handle button click
	public LoginWindow() {

		/**
		 * Graphical User Interface for login form
		 */

		/**
		 * Enables login button if both username and password fields have stuff in them.
		 * This one KeyAdapter is reused for both username and password events
		 * @author Callum McNeilage - n10482652
		 * @contributor Lucas Maldonado - n10534342
		 */
		KeyAdapter loginKeyAdapter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnLogin.setEnabled(
					enterPasswordPasswordField.getPassword().length > 0
						&& enterUsernameTextField.getText().length() > 0
				);
			}
		};

		// Use the key listener from above in both the username and password field
		enterUsernameTextField.addKeyListener(loginKeyAdapter);
		enterPasswordPasswordField.addKeyListener(loginKeyAdapter);

		/**
		 * Called when the user presses enter on the password field
		 * Will click the login button for the user
		 * @author Lucas Maldonado - n10534342
		 */
		enterPasswordPasswordField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnLogin.doClick();
			}
		});

		/**
		 * Called when user presses enter on username field
		 * Will click the login button for the user
		 * @author Callum McNeilage - n10482652
		 *
		 * Adapted from enterPasswordPasswordField.addActionListener
		 * @author Lucas Maldonado - n10534342
		 */
		enterUsernameTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnLogin.doClick();
			}
		});

		/**
		 * When pressed checks user credentials and authenticates
		 * @author Callum McNeilage - n10482652
		 * @contributor Connor McHugh - n10522662
		 * @contributor Lucas Maldonado - n10534342
		 */
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Attempting login...");

				String username = enterUsernameTextField.getText();
				char[] pwd = enterPasswordPasswordField.getPassword();

				// Turn the character array in pwd into a String using StringBuilder
				StringBuilder password = new StringBuilder();
				password.append(pwd);


				try {
					// Construct a credentials class with the login info, and send it off to the server
					Credentials credentials = new Credentials(username, password.toString(), null);
					Response response = ControlPanel.get().requestSender.login(credentials);

					// The request is a blocking operation, no need for delegates
					if (response.getStatus().equals("success")) {
						ControlPanel.get().loggedIn();
					} else {
						// If the password is incorrect, hide the placeholder label and show
						// the label informing the user that the password is incorrect.
						passIncorrect.setVisible(true);
					}
				} catch (ClassNotFoundException ex) {
					System.out.println("Error: Server sent an unexpected response.");
				} catch (IOException ex) {
					String serverIPAddress = ControlPanel.get().propsReader.getIPAddress();
					int serverPort = ControlPanel.get().propsReader.getPort();
					System.out.printf("Error: Unable to connect to server!\n" +
						"Make sure it's running and you're using the correct address & port.\n" +
						"Currently configured IP address: '%s:%d' (restart to refresh data)", serverIPAddress, serverPort);
				}
			}
		});
	}
}
