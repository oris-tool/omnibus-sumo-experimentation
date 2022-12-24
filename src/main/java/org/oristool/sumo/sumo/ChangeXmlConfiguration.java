package org.oristool.sumo.sumo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class ChangeXmlConfiguration {

	public static void changeXmlConfiguration(String FilePath, String NodePath, String Attribute, String AttributeValue)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException,
			TransformerException {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder b = f.newDocumentBuilder();
		Document doc = b.parse(new File(FilePath));

		XPath xPath = XPathFactory.newInstance().newXPath();
		Element startDateNode = (Element) xPath.evaluate(NodePath, doc, XPathConstants.NODE);
		startDateNode.setAttribute(Attribute, AttributeValue);

		saveXmlFile(FilePath, doc);
	}

	public static void saveXmlFile(String path, Document doc) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);
	}

}
