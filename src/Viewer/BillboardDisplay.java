package Viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import Server.Endpoints.EndpointType;
import Shared.Billboard;
import Shared.ClientPropsReader;
import Shared.Display.IMGHandler;
import Shared.Network.RequestSender;
import Shared.Network.Response;

/**
 * The billboard display lets you display a billboard in a window.
 * Give it a billboard with setBillboard() and it will display it
 *
 * @author Lucas Maldonado N10534342
 * @author Connor McHugh n10522662
 * @author Callum McNeilage n10482652
 */
public class BillboardDisplay extends JFrame {
	public JPanel mainPanel;
	private JTextPane message;
	private JTextPane information;
	private JTextPane image;

	public Billboard billboardToView;
	public String lastTitleText;
	public String lastInfoText;
	public String lastImage;


	// The help text to display to tell the user what they need to do
	public String noBillboardInfoText = "Please add billboards to the schedule in the Billboard Controller";




	/** Constructor */
	public BillboardDisplay() {

		information.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, -1));
		message.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, -1));

		StyledDocument doc = message.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		doc = information.getStyledDocument();
		center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);


		message.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1)
					System.exit(0);
			}
		});
		image.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1)
					System.exit(0);
			}
		});
		information.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1)
					System.exit(0);
			}
		});

	}

	private Font textFormatter(JTextPane textArea, String textAreaText) {
		StyledDocument sd = textArea.getStyledDocument();
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);
		sd.setParagraphAttributes(0, sd.getLength()-1, sas, false);

		Font labelFont = textArea.getFont();

		int stringWidth = textArea.getFontMetrics(labelFont).stringWidth(textAreaText);
		int componentWidth = textArea.getWidth();

		double widthRatio = (double)componentWidth / (double)stringWidth;

		int newFontSize = (int)(labelFont.getSize() * widthRatio);
		int componentHeight = textArea.getHeight();

		int fontSizeToUse = Math.min(newFontSize - 5, componentHeight - 5);
		return new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse);
	}



	/**
	 * Call this to give the billboard viewer a new billboard to display.
	 * Set billboard to null to clear it.
	 * @param billboard
	 */
	public void setBillboard(Billboard billboard) {
		if (billboard != null) {

//			 clearViewer(); // shouldn't need this

			// Check which components need to be enabled
			message.setVisible(!billboard.titleText.equals(""));
			information.setVisible(!billboard.infoText.equals(""));
			if (billboard.image != null) {
				image.setVisible(!billboard.image.equals(""));
			} else {image.setVisible(false);}


			// Set the variables

			message.setText(billboard.titleText);
			message.setFont(textFormatter(message, billboard.titleText));
			message.setForeground(billboard.titleTextColor);

			information.setText(billboard.infoText);
			information.setForeground(billboard.infoTextColor);

			// Set background color
			mainPanel.setBackground(billboard.backgroundColor);
			message.setBackground(billboard.backgroundColor);
			information.setBackground(billboard.backgroundColor);
			image.setBackground(billboard.backgroundColor);

			try {

				// Set image
				BufferedImage billboardImage = null;
				try {
					billboardImage = IMGHandler.imageDecoder(billboard.image);
				} catch (NullPointerException | IllegalArgumentException e) {
//					e.printStackTrace();
				}

				try {
					billboardImage = ImageIO.read(new URL(billboard.image));
				} catch (NullPointerException | IOException ignored) { }

				try {
					assert billboardImage != null;

					int imageHeight = billboardImage.getHeight();
					int imageWidth = billboardImage.getWidth();

					System.out.println("Height: " + imageHeight + " | Width: " + imageWidth);


					// ImageIcon displayedBillboardImage = new ImageIcon(new ImageIcon(billboardImage).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
					ImageIcon displayedBillboardImage = imageScaler(billboardImage, imageWidth, imageHeight, 200);

					image.insertIcon(displayedBillboardImage);
					StyledDocument sd = image.getStyledDocument();
					SimpleAttributeSet sas = new SimpleAttributeSet();
					StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);
					sd.setParagraphAttributes(0, sd.getLength()-1, sas, false);
				}
				catch (NullPointerException ignored) {}


			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		} else {
			displayNoBillboard();
		}

		information.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, -1));
		message.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, -1));
	}


	public byte[] imageToByte(String imgString)
	{
		return imgString.getBytes();
	}

	public BufferedImage convertToImage(String imgString) throws IOException {
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageToByte(imgString)));
		return img;
	}

	void setImage(String imgString) throws IOException {
		BufferedImage img = convertToImage(imgString);

		ImageIcon icon = new ImageIcon(img);
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(200, 300);
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Display the default billboard when there is no board to display
 	 */
	private void displayNoBillboard() {
		setBillboard(new Billboard(
			"No Billboard",
			"No Billboards Scheduled",
			noBillboardInfoText,
			Color.red,
			Color.red,
			Color.darkGray,
			"",
			"No Author"
		));
	}


	private ImageIcon imageScaler(Image billboardImage, int imageWidth, int imageHeight, int desiredHeight) {
		double scaleFactor = (double) (imageHeight / desiredHeight);
		int newWidth = (int) Math.round(imageWidth / scaleFactor);

		return new ImageIcon(new ImageIcon(billboardImage).getImage().getScaledInstance(newWidth, desiredHeight, Image.SCALE_DEFAULT));
	}
}
