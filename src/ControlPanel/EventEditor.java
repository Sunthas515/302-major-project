package ControlPanel;

import Server.Endpoints.EndpointType;
import Shared.Billboard;
import Shared.Schedule.Event;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventEditor extends JFrame {
	private JPanel main_Panel;
	private JButton cancel_Button;
	private JButton ok_Button;
	private JSpinner duration_Spinner;
	private JComboBox billboardSelector_ComboBox;
	private JCheckBox enableLooping_ChkBox;
	private JTextField startDate_TextField;
	private JTextField startTime_TextField;
	private JSpinner loopWeeks_Spinner;
	private JSpinner loopDays_Spinner;
	private JSpinner loopHours_spinner;
	private JSpinner loopMinutes_Spinner;

	private SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH);

	private boolean creatingNewEvent;
	private Event oldEvent;

	/**
	 * Allows the user to schedule a billboard. Has controls for setting a start date and time, duration, and looping
	 * behaviours. All user input is sanitized. The inputs are converted to unix time for convenience.
	 *
	 * When the user presses OK, a new Event is created, which is sent to server
	 *
	 * @author Callum McNeilage - n10482652
	 * @author Lucas Maldonado - n10534342
	 */
	public EventEditor(Event event) {
		// Set up frame
		setTitle("Event Editor");
		setContentPane(main_Panel);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setLocationRelativeTo(ControlPanel.get());
		setVisible(true);

		creatingNewEvent = event == null;

		LocalDateTime currentDateTime;
		if (creatingNewEvent) {
			// Pre-fill date & time boxes with current time
			currentDateTime = LocalDateTime.now();
			startTime_TextField.setText(timeFormatter.format(new Date()));
			startDate_TextField.setText(currentDateTime.format(dateFormatter));
		} else {
			// Load in the values from the provided event
			Date date = new Date(event.startTime);
			SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
			startDate_TextField.setText(sdf.format(date));
			startTime_TextField.setText(timeFormatter.format(date));

			billboardSelector_ComboBox.removeAllItems();
			billboardSelector_ComboBox.addItem(event.billboardName);
			billboardSelector_ComboBox.setEnabled(false);
			ok_Button.setEnabled(true);

			duration_Spinner.setValue(event.getDuration() / (60 * 1000));

			oldEvent = event;


		}



		// Read the list of available billboards and add them to the dropdown box
		ArrayList<Billboard> allBillboardNames;
		try {
			allBillboardNames = (ArrayList<Billboard>)ControlPanel.get().requestSender.SendData(EndpointType.listBillboards, null).getData();
			for (Billboard billboard : allBillboardNames) {
				billboardSelector_ComboBox.addItem(billboard.name);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		ok_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Event event = new Event();

				try { // Parse the start date and time
					// 86400000 is number of seconds in day (86400) * milliseconds in second (1000)
					// This converts epoch days to epoch milliseconds
					event.startTime += 86400000 * LocalDate.parse(startDate_TextField.getText(), dateFormatter).toEpochDay();
					// Add on the hours and minutes, and we're good to go
					event.startTime += timeFormatter.parse(startTime_TextField.getText()).getTime();
				} catch (DateTimeException | ParseException ex) {
					System.out.println(ex.getMessage());
					return;
				}

				// 60 converts minutes to seconds, 1000 converts seconds to milliseconds
				event.setDuration(60L * 1000L * Long.parseLong(duration_Spinner.getValue().toString()));

				// Get the specified billboard name from the drop down box
				event.billboardName = (String)billboardSelector_ComboBox.getSelectedItem();

				// Get the author from the currently logged in user
				event.author = ControlPanel.get().requestSender.getToken().getUser();

				System.out.println(event.toString());

				if (enableLooping_ChkBox.isSelected()) {
					event.loopInterval = (
						((int)loopMinutes_Spinner.getValue() * 1000 * 60)
							+ ((int)loopHours_spinner.getValue() * 1000 * 60 * 60)
						+ ((int)loopDays_Spinner.getValue() * 1000 * 60 * 1440)
						+ ((int)loopWeeks_Spinner.getValue() * 1000* 604800)
						);
				} else {
					event.loopInterval = 0;
				}

				try {

					if (!creatingNewEvent) {
						// Delete the existing event
						Object[] data = new Object[2];
						data[0] = oldEvent;
						data[1] = false;
						ControlPanel.get().requestSender.SendData(EndpointType.deleteEvent, data);
					}

					ControlPanel.get().requestSender.SendData(EndpointType.addEvents, event);
				} catch (IOException | ClassNotFoundException ex) {
					ex.printStackTrace();
					return;
				}

				ControlPanel.get().refreshEvents();
				dispose();
			}
		});
		cancel_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		billboardSelector_ComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setOkButtonEnabled();
			}
		});
	}

	/**
	 * Call to check if all of the inputs are good.
	 * If they are, enable the ok button
	 * If they are not, disable the ok button.
	 *
	 * @author Lucas Maldonado - n10534342
	 */
	private void setOkButtonEnabled() {
		boolean validBillboardSelected = billboardSelector_ComboBox.getSelectedIndex() != 0;

		ok_Button.setEnabled(validBillboardSelected);
	}
}
