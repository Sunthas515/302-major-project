package ControlPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Schedule {
	private JPanel Schedule;
	private JTable table1;
	private JButton btnDelete;
	private JButton btnOK;
	private JButton btnSchedule;
	private static JFrame scheduleFrame;

	public JFrame getScheduleFrame() {
		return scheduleFrame;
	}

	public static DefaultTableModel tableModel;



	public Schedule() {
		/**
		 * Endpoint listener for OK button to close window
		 *
		 * @author Callum McNeilage - n10482652
		 */
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getScheduleFrame().dispose();
			}
		});
		/**
		 * Endpoint Listener for Schedule button
		 *
		 * @author Callum McNeilage - n10482652
		 */
		btnSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EventEditor(null);
			}
		});


	}

	/**
	 * Loads the schedule window
	 *
	 * @author Callum McNeilage - n10482652
	 * @param args
	 */
	public static void main(String[] args) {
		// Create and setup Schedule window
		scheduleFrame = new JFrame("BillboardEditor Schedule");
		scheduleFrame.setContentPane(new Schedule().Schedule);
		scheduleFrame.setDefaultCloseOperation(scheduleFrame.HIDE_ON_CLOSE);
		scheduleFrame.pack();
		scheduleFrame.setVisible(true);
	}
}
