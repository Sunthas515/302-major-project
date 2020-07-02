package Shared;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * The all mighty billboard class, the centrepiece of this assignment. This class represents the relevant info for a
 * billboard, including the information needed to display and identify them. Some additional metadata is also present,
 * for example, the author of the billboard (which is used in permissions)
 *
 * @author Lucas Maldonado
 */
public class Billboard implements Serializable {
	public String name;

	public String titleText;
	public String infoText;
	public Color titleTextColor;
	public Color infoTextColor;
	public Color backgroundColor;
	public String image;

	public String author;


	/***
	 * Default constructor
	 * @param name
	 * @param titleText
	 * @param infoText
	 * @param titleTextColor
	 * @param infoTextColor
	 * @param backgroundColor
	 * @param image
	 * @param author
	 */
	public Billboard(String name, String titleText, String infoText, Color titleTextColor, Color infoTextColor, Color backgroundColor, String image, String author) {
		this.name = name;
		this.titleText = titleText;
		this.infoText = infoText;
		this.titleTextColor = titleTextColor;
		this.infoTextColor = infoTextColor;
		this.backgroundColor = backgroundColor;
		this.image = image;
		this.author = author;
	}


	/**
	 * Constructor that imports the values from a given xml string.
	 * @param xmlData
	 */
	public Billboard(String xmlData) {
		importFromXML(xmlData);
	}

	/**
	 * Converts this billboard to an xml string
	 * @return
	 */
	public String toXML() {
		return null;
	}


	/**
	 * Init this billboard from a xml string
	 */
	public void importFromXML(String xmlData) {

	}

	public boolean equals(Object otherObject) {
		if (otherObject instanceof Billboard) {
			Billboard b = (Billboard) otherObject;
			return b.author.equals(author)
				&& b.image.equals(image)
				&& b.infoText.equals(infoText)
				&& b.titleText.equals(titleText)
				&& b.name.equals(name)
				&& b.backgroundColor == backgroundColor
				&& b.infoTextColor == infoTextColor
				&& b.titleTextColor == titleTextColor;
		} else {
			return false;
		}
	}
}
