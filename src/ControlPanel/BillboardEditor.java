package ControlPanel;

import Server.Endpoints.EndpointType;
import Shared.Billboard;
import Shared.Display.IMGHandler;
import Shared.Display.XMLHandler;
import Viewer.BillboardDisplay;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;


/**
 * Form for creating a new billboard
 *
 * @author Connor McHugh - n10522662
 * @author Callum McNeilage - n10482652
 * @author Lucas Maldonado - n10534342
 */
public class BillboardEditor extends JFrame {
	public JPanel BillboardWindow;
	private JButton titleColor_Button;
	private JButton loadImageFromFile_Button;
	private JButton loadImageFromURL_Button;
	private JLabel selectedFile_Label;
	private JTextField title_TextField;
	private JButton infoColor_Button;
	private JButton backgroundColor_Button;
	private JButton exportXML_Button;
	private JButton importXML_Button;
	private JTextField titleColorPreview;
	private JTextField infoColorPreview;
	private JTextField backgroundColorPreview;
	private JButton okay_Button;
	private JTextField name_TextField;
	private JTextPane info_TextPane;
	private JButton updatePreviewButton;

	private String author;
	private boolean creatingNewBillboard;

	private boolean imageIsURL;
	private String imageBase64String;

	private JFrame previewFrame;
	private BillboardDisplay previewWindow;


	/**
	 * Billboard Constructor
	 * @param billboardName optional. If supplied, will pre-fill the values with this billboard's properties
	 * @author Connor McHugh - n10522662
	 */
	public BillboardEditor(String billboardName) {
		creatingNewBillboard = billboardName == null;
		if (creatingNewBillboard) {
			// If we're creating a new billboard
			author = ControlPanel.get().requestSender.getToken().getUser();
		} else {
			// If we're editing an existing billboard, load in the values
			System.out.println("BillboardEditor: loading values from billboard: " + billboardName);

			try {
				Billboard billboard = (Billboard)ControlPanel.get().requestSender.SendData(EndpointType.getBillboard, billboardName).getData();
				importBillboard(billboard);
				name_TextField.setEnabled(false);
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Failed to import billboard!");
				System.out.println(e.getMessage());
				return;
			}
		}

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setTitle("Billboard Editor");
		setContentPane(BillboardWindow);
		pack();
		setLocationRelativeTo(ControlPanel.get());
		setVisible(true);
		setResizable(false);


		// Create the preview window
		previewWindow = new BillboardDisplay();
		previewFrame = new JFrame("Preview Changes");
		previewFrame.setContentPane(previewWindow.mainPanel);
		previewFrame.setSize(800, 600);
		//previewFrame.setVisible(true);
		updatePreviewButton.setVisible(false);

		updatePreviewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				previewWindow.setBillboard(exportBillboard());
			}
		});



		titleColor_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'Message Color' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Color initialColor = Color.black;
				Color messageColor = JColorChooser.showDialog(null, "Choose Message Color", initialColor); // Brings up the color chooser dialogue
				titleColorPreview.setBackground(messageColor); // Sets the inactive button that acts as as 'preview' to the color selected
			}
		});

		loadImageFromFile_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'Local File' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				String localImagePath = null;

				JFileChooser imageSelect = new JFileChooser();
				imageSelect.setAcceptAllFileFilterUsed(false);
				imageSelect.setDialogTitle("Select an image");

				FileNameExtensionFilter restrict = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "bmp", "png");
				imageSelect.addChoosableFileFilter(restrict);

				int selected = imageSelect.showOpenDialog(null);


				if (selected == JFileChooser.APPROVE_OPTION) {
					localImagePath = imageSelect.getSelectedFile().getAbsolutePath();
					System.out.println("LOCAL IMAGE PATH:::" + localImagePath);
					selectedFile_Label.setText(localImagePath);
				}


				BufferedImage image = null;

				try
				{
					assert localImagePath != null;
					image = ImageIO.read(new File(localImagePath));
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				String fileExtension = "";

				int i = localImagePath.lastIndexOf('.');
				int p = Math.max(localImagePath.lastIndexOf('/'), localImagePath.lastIndexOf('\\'));

				if (i > p) {
					fileExtension = localImagePath.substring(i+1);
				}

				imageBase64String = IMGHandler.imageEncoder(image, fileExtension);
				System.out.println(imageBase64String);
				imageIsURL = false;
			}
		});

		loadImageFromURL_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'URL' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				// Open dialogue box for the user to enter a URL
				String imageURL = (String)JOptionPane.showInputDialog(
					frame,
					"Enter the URL of the image that you would like to use",
					"Enter Image URL",
					JOptionPane.PLAIN_MESSAGE
				);

				// As long as the user has entered a URL, update the preview label
				if ((imageURL != null) && (imageURL.length() > 0)) {
					selectedFile_Label.setText(imageURL);
				}
				imageIsURL = true;
			}
		});

		infoColor_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'Information Text Color' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Color initialColor = Color.black;
				Color informationColor = JColorChooser.showDialog(null, "Choose Information Text Color", initialColor); // Brings up the color chooser dialogue
				infoColorPreview.setBackground(informationColor); // Sets the inactive button that acts as as 'preview' to the color selected
			}
		});

		backgroundColor_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'Background Color' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Color initialColor = Color.white;
				Color backgroundColor = JColorChooser.showDialog(null, "Choose Background Color", initialColor); // Brings up the color chooser dialogue
				backgroundColorPreview.setBackground(backgroundColor); // Sets the inactive button that acts as as 'preview' to the color selected
			}
		});

		importXML_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'Import XML' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open file chooser dialogue to select XML file to load
				JFileChooser xmlSelect = new JFileChooser();
				xmlSelect.setAcceptAllFileFilterUsed(false);
				xmlSelect.setDialogTitle("Select an XML file");

				FileNameExtensionFilter restrict = new FileNameExtensionFilter("XML files", "xml");
				xmlSelect.addChoosableFileFilter(restrict);

				String xmlImportPath = null;
				int selected = xmlSelect.showOpenDialog(null);

				if (selected == JFileChooser.APPROVE_OPTION) {
					xmlImportPath = xmlSelect.getSelectedFile().getAbsolutePath();
					// Clear any current values
					title_TextField.setText(null);
					titleColorPreview.setBackground(Color.black);
					info_TextPane.setText(null);
					infoColorPreview.setBackground(Color.black);
					backgroundColorPreview.setBackground(Color.white);
				}

				importFromXML(true, xmlImportPath);
			}
		});

		exportXML_Button.addActionListener(new ActionListener() {
			/**
			 * Handle the 'Export XML' button being pressed
			 *
			 * @author Connor McHugh - n10522662
			 * @param e the event to be processed
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open file chooser dialogue to select XML file to load
				JFileChooser xmlSave = new JFileChooser();
				xmlSave.setAcceptAllFileFilterUsed(false);
				xmlSave.setDialogTitle("Save To");

				FileNameExtensionFilter restrict = new FileNameExtensionFilter("XML files", "xml");
				xmlSave.addChoosableFileFilter(restrict);

				String xmlExportPath = null;
				int selected = xmlSave.showSaveDialog(null);

				if (selected == JFileChooser.APPROVE_OPTION) {
					xmlExportPath = xmlSave.getSelectedFile().getAbsolutePath();

					String messageText = title_TextField.getText();
					Color messageColor = titleColorPreview.getBackground();
					String informationText = info_TextPane.getText();
					Color informationColor = infoColorPreview.getBackground();
					Color backgroundColor = backgroundColorPreview.getBackground();
					String image = selectedFile_Label.getText();

					try {
						XMLHandler.xmlWriter(xmlExportPath, exportBillboard());
					} catch (ParserConfigurationException parserConfigurationException) {
						parserConfigurationException.printStackTrace();
					}
				}
			}
		});

		okay_Button.addActionListener(new ActionListener() {
			/**
			 * Saves data and closes window
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				EndpointType type = creatingNewBillboard ? EndpointType.addBillboard : EndpointType.updateBillboard;
				try {
					ControlPanel.get().requestSender.SendData(type, exportBillboard());
					ControlPanel.get().refreshBillboards();
					dispose();
					previewFrame.dispose();
				} catch (IOException | ClassNotFoundException ex) {
					System.out.println(ex.getMessage());
				}

			}
		});
	}


	/**
	 * Sets the UI element's values to that of a given billboard.
	 * Used when loading in an existing billboard to edit
	 * @param b the billboard to load in
	 */
	private void importBillboard(Billboard b) {
		name_TextField.setText(b.name);
		title_TextField.setText(b.titleText);
		info_TextPane.setText(b.infoText);
		titleColorPreview.setBackground(b.titleTextColor);
		infoColorPreview.setBackground(b.infoTextColor);
		backgroundColorPreview.setBackground(b.backgroundColor);
		selectedFile_Label.setText(b.image);
		author = b.author;
	}


	/**
	 * Reads all of the UI properties that the user set, and turns it into a billboard for easy access.
	 * @returns a new billboard object with the data filled in
	 */
	private Billboard exportBillboard() {
//		System.out.println(imageIsURL);
		if (imageIsURL) {
			System.out.println(selectedFile_Label.getText());
			return new Billboard(
				name_TextField.getText(),
				title_TextField.getText(),
				info_TextPane.getText(),
				titleColorPreview.getBackground(),
				infoColorPreview.getBackground(),
				backgroundColorPreview.getBackground(),
				selectedFile_Label.getText(),
				author
			);
		} else {
			return new Billboard(
				name_TextField.getText(),
				title_TextField.getText(),
				info_TextPane.getText(),
				titleColorPreview.getBackground(),
				infoColorPreview.getBackground(),
				backgroundColorPreview.getBackground(),
				imageBase64String,
				author
			);
		}

	}


	/**
	 * Populates billboard editor window with data from server
	 */
	private void importFromXML(boolean isFile, String xmlData) {
		try {
			// Set message text and color
			Color messageColor;
			String messageText = XMLHandler.xmlReader(isFile, xmlData, "message", null);
			if (messageText != null) {
				title_TextField.setText(messageText);
				try {
					messageColor = Color.decode(Objects.requireNonNull(XMLHandler.xmlReader(isFile, xmlData, "message", "colour")));
				} catch (NullPointerException r) {
					messageColor = Color.black;
				}
				titleColorPreview.setBackground(messageColor);
			}

			// Set information text and color
			Color informationColor;
			String informationText = XMLHandler.xmlReader(isFile, xmlData, "information", null);
			if (informationText != null) {
				info_TextPane.setText(informationText);
				try {
					informationColor = Color.decode(Objects.requireNonNull(XMLHandler.xmlReader(isFile, xmlData, "information", "colour")));
				} catch (NullPointerException r) {
					informationColor = Color.black;
				}
				infoColorPreview.setBackground(informationColor);

			}

			// Set background color
			Color backgroundColor;
			try {
				backgroundColor = Color.decode(Objects.requireNonNull(XMLHandler.xmlReader(isFile, xmlData, "billboard", "background")));
			} catch (NullPointerException r) {
				backgroundColor = Color.white;
			}
			backgroundColorPreview.setBackground(backgroundColor);

			// Set image
			String image = null;
			try {
				image = XMLHandler.xmlReader(isFile, xmlData, "picture", "data");
				selectedFile_Label.setText(image);
				imageIsURL = false;
				imageBase64String = image;
			} catch (NullPointerException ignored) {
			}
			try {
				image = XMLHandler.xmlReader(isFile, xmlData, "picture", "url");
				imageIsURL = true;
				selectedFile_Label.setText(image);
			} catch (NullPointerException ignored) {
			}

			System.out.println("IMAGE TEST:::" + image);

//			if (image != null) {
//				System.out.println(image);
//				selectedFile_Label.setText(image);
//			}
		} catch (ParserConfigurationException | IOException | SAXException | NullPointerException ex) {
			ex.printStackTrace();
		}
	}
}
