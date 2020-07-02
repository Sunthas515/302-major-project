package Shared.Display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Class for Base64 encoding and decoding image files
 *
 * @author Connor McHugh - n10522662
 */
public class IMGHandler {
	/**
	 * Method to encode an image into a Base64 string
	 * @param image the image to encode
	 * @param type the file extension of the image to encode
	 * @return Base64 string of the image
	 */
	public static String imageEncoder(BufferedImage image, String type) {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();

			imageString = Base64.getEncoder().encodeToString(imageBytes);
//			System.out.println(imageString);
			bos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}

	/**
	 * Method to decode an images encoded as a Base64 string
	 *
	 * @param imageString the Base64 string to decode
	 * @return the decoded image
	 */
	public static BufferedImage imageDecoder(String imageString) {
		BufferedImage image = null;
		byte[] imageByte;

		try {
			imageByte = Base64.getDecoder().decode(imageString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);

			bis.close();
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
		return image;
	}
}
