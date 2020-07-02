package Shared.Display;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;


/**
 * JUnit5 tests for the XMLHandler class
 * @author Connor McHugh - n10522662
 */
class TestXMLHandler {
	private static final String xmlFileName = "test.xml";
	private static final String invalidFileName = "test.txt";
	private static File xmlTestFile;
	private static File invalidTestFile;
	private static final String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><billboard><message>Default-coloured message</message><picture url=\"https://cloudstor.aarnet.edu.au/plus/s/X79GyWIbLEWG4Us/download\" /><information colour=\"#60B9FF\">Custom-coloured information text</information></billboard>";


	@BeforeAll
	static void setUp() throws IOException {
    	invalidTestFile = new File(invalidFileName);
    	FileWriter invalidW = new FileWriter(invalidFileName);
    	invalidW.write("some invalid text");
    	invalidW.close();
    	xmlTestFile = new File(xmlFileName);
		FileWriter xmlW = new FileWriter(xmlFileName);
		xmlW.write(xmlString);
		xmlW.close();
    }

    @AfterAll
	static void tearDown() {
    	invalidTestFile.delete();
		xmlTestFile.delete();
    }

    // Tests for the xmlReader method //
    @Test
	/*
	 * Confirm that the method throws an exception when the file provided doesn't exist.
	 */
    public void readerNonExistentFile() {
		assertThrows(FileNotFoundException.class, () -> {
			XMLHandler.xmlReader(true,"invalid file name", "message", null);
		});
    }

	@Test
	/*
	 * Confirm that the method throws an exception when isFile is true, but the file provided is invalid.
	 */
	public void readerInvalidFile() {
		assertThrows(SAXParseException.class, () -> {
			XMLHandler.xmlReader(true, invalidFileName, "message", null);
		});
	}

    @Test
	/*
	 * Confirm that when an invalid tag is passed when isFile is true, the method returns null.
	 */
    void readerInvalidTag() throws Exception {
    	String tester = XMLHandler.xmlReader(true, xmlFileName, "invalid tag", null);
		assertNull(tester);
	}

	@Test
	/*
	 * Confirm that when an invalid attribute is passed when isFile is true, the method returns null.
	 */
	void readerInvalidAttribute() throws Exception {
		String tester = XMLHandler.xmlReader(true, xmlFileName, "invalid tag", null);
		assertNull(tester);
	}

	@Test
	/*
	 * Confirm that the method works as expected from a file when only a tag is provided
	 */
	void readFileTag() throws IOException, SAXException, ParserConfigurationException {
		String tester = XMLHandler.xmlReader(true, xmlFileName, "message", null);
		assertEquals("Default-coloured message", tester);
	}

	@Test
	/*
	 * Confirm that the method works as expected from a file when both a tag and an attribute are provided
	 */
	void readFileTagAttribute() throws IOException, SAXException, ParserConfigurationException {
		String tester = XMLHandler.xmlReader(true, xmlFileName, "information", "colour");
		assertEquals("#60B9FF", tester);
	}


	@Test
	/*
	 * Confirm that the program throws an exception when it is provided with an invalid XML string
	 */
	public void readerInvalidString() {
		assertThrows(SAXParseException.class, () -> {
			XMLHandler.xmlReader(false,"invalid xml string", "message", null);
		});
	}

	@Test
	/*
	 * Confirm that the program throws an exception when it is provided with a null XML string
	 */
	public void readerNullString() {
		assertThrows(NullPointerException.class, () -> {
			XMLHandler.xmlReader(false,null, "message", null);
		});
	}

	@Test
	/*
	 * Confirm that when an invalid tag is passed when isFile is false, the method returns null.
	 */
	void readerStringInvalidTag() throws Exception {
		String tester = XMLHandler.xmlReader(false, xmlString, "invalid tag", null);
		assertNull(tester);
	}

	@Test
	/*
	 * Confirm that when an invalid attribute is passed when isFile is false, the method returns null.
	 */
	void readerStringInvalidAttribute() throws Exception {
		String tester = XMLHandler.xmlReader(false, xmlString, "invalid tag", null);
		assertNull(tester);
	}

	@Test
	/*
	 * Confirm that the method works as expected from a string when only a tag is provided
	 */
	void readStringTag() throws IOException, SAXException, ParserConfigurationException {
		String tester = XMLHandler.xmlReader(false, xmlString, "message", null);
		assertEquals("Default-coloured message", tester);
	}

	@Test
	/*
	 * Confirm that the method works as expected from a string when both a tag and an attribute are provided
	 */
	void readStringTagAttribute() throws IOException, SAXException, ParserConfigurationException {
		String tester = XMLHandler.xmlReader(false, xmlString, "information", "colour");
		assertEquals("#60B9FF", tester);
	}


	// Tests for the xmlWriter method //


	// Tests for the colorConverter method //
	@Test
	/*
	 *	Confirm that the colorConverter works properly when provided with a valid rgb color
	 */
	void validColorConverter() {
		String tester = XMLHandler.colorConverter(new Color(255, 196, 87));
		assertEquals("#FFC457", tester);
	}

	@Test
	/*
	 *	Confirm that the colorConverter throws an exception when in invalid color is provided
	 */
	void invalidColorConverter() {
		assertThrows(IllegalArgumentException.class, () -> {
			XMLHandler.colorConverter(new Color(256, 196, 87));
		});
	}

	@Test
		/*
		 *	Confirm that the colorConverter throws an exception when null is provided
		 */
	void nullColorConverter() {
		assertThrows(NullPointerException.class, () -> {
			XMLHandler.colorConverter(null);
		});
	}
}
