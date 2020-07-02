package Shared.Display;

import Shared.Billboard;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Class for various tasks related to XML file handling
 *
 * @author Connor McHugh - n10522662
 */
public class XMLHandler {
	/**
	 * Method to read information for an XML file and return it
	 * @param isFile boolean that defines whether the XML data provided is a file or a string
	 * @param xmlData path to the XML file to work on
	 * @param tag name of the tag to get information from in the XML file
	 * @param attribute name of the attribute of a tag to get information from in the XML file: if "null" is passed as
	 *                  this parameter, it is ignored, and information is taken from tag instead
	 * @return the information that is found in the given XML file, according to the tag and attribute parameters
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static String xmlReader(boolean isFile, String xmlData, String tag, String attribute) throws ParserConfigurationException, IOException, SAXException {
		Document document;
		if (isFile) {
			File file = new File(xmlData);
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = db.parse(file);
		} else {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlData));
			document = db.parse(is);
		}

		if (document.getElementsByTagName("billboard").getLength() != 0) {
				if (document.getElementsByTagName(tag).getLength() != 0) {
					if (attribute == null) {
						return document.getElementsByTagName(tag).item(0).getTextContent();
					} else {
//						try {
							return document.getElementsByTagName(tag).item(0).getAttributes().getNamedItem(attribute).getNodeValue();
//						} catch (NullPointerException e) {
//							return null;
//						}
					}
				} else {
					System.out.println("Tag doesn't exist in the provided XML file!");
					return null;
				}
		} else {
			System.out.println("This is not a valid billboard XML file!");
			return null;
		}
	}

	/**
	 * Method to generate an XML file based on the parameters provided
	 * @param filePath path to save the XML file to
	 * @param b the billboard you want to save
	 * @throws ParserConfigurationException
	 */
	public static void xmlWriter(String filePath, Billboard b) throws ParserConfigurationException {
		String messageColorHex = colorConverter(b.titleTextColor);
		String informationColorHex = colorConverter(b.infoTextColor);
		String backgroundColorHex = colorConverter(b.backgroundColor);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();

		// Root 'billboard element'
		Element rootElem = document.createElement("billboard");
		document.appendChild(rootElem);

		// Background color attribute
		Attr backgroundAttr = document.createAttribute("background");
		backgroundAttr.setValue(backgroundColorHex);
		rootElem.setAttributeNode(backgroundAttr);

		// Message element
		if (!b.titleText.equals("")) {
			// Message text
			Element messageElem = document.createElement("message");
			rootElem.appendChild(messageElem);
			messageElem.appendChild(document.createTextNode(b.titleText));

			// Message color attribute
			Attr messageAttr = document.createAttribute("colour");
			messageAttr.setValue(messageColorHex);
			messageElem.setAttributeNode(messageAttr);
		}

		// Information element
		if (!b.infoText.equals("")) {
			// Information text
			Element informationElem = document.createElement("information");
			rootElem.appendChild(informationElem);
			informationElem.appendChild(document.createTextNode(b.infoText));

			// Information color attribute
			Attr informationAttr = document.createAttribute("colour");
			informationAttr.setValue(informationColorHex);
			informationElem.setAttributeNode(informationAttr);
		}

		// Picture element
		if (!b.image.equals("")) {
			Element pictureElem = document.createElement("picture");
			rootElem.appendChild(pictureElem);

			try {
				URL imageURL = new URL(b.image);
				Attr urlAttr = document.createAttribute("url");
				urlAttr.setValue(String.valueOf(imageURL));
				pictureElem.setAttributeNode(urlAttr);
			} catch (MalformedURLException e) {
				Attr dataAttr = document.createAttribute("data");
				dataAttr.setValue(b.image);
				pictureElem.setAttributeNode(dataAttr);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(filePath));
		try {
			assert transformer != null;
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Small method to convert an rgb color to hexadecimal
	 * @param inputColor color in rgb format that needs to be converted
	 * @return color encoded as hexadecimal
	 */
	public static String colorConverter(Color inputColor) {
		System.out.println(inputColor);
		return String.format("#%02X%02X%02X",
			inputColor.getRed(),
			inputColor.getGreen(),
			inputColor.getBlue());
	}
}
