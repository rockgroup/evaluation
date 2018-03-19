package com.yocmoon.evaluation.utils;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CustomXMLUtil {
	public CustomXMLUtil() {

	}

	public String createXML(HashMap<String, String> xmlPropsMap) {
		System.out.println("createXML:input=" + xmlPropsMap);
		String xmlStr = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			document.setXmlVersion("1.0");

			// the first node
			Element root = document.createElement("EpointNewDataSetREQ");
			document.appendChild(root);

			// the second node
			Element nokia = document.createElement("Table");

			// the third node == detail info
			// Element priceNokia = document.createElement("BanJianGuid");
			// priceNokia.setTextContent(BanJianGuid);
			// nokia.appendChild(priceNokia);
			//
			// Element operatorNokia = document.createElement("Score");
			// operatorNokia.setTextContent(Score);
			// nokia.appendChild(operatorNokia);
			//
			// Element productNokia = document.createElement("Suggestion");
			// productNokia.setTextContent(Suggestion);
			// nokia.appendChild(productNokia);
			if (xmlPropsMap != null && !xmlPropsMap.isEmpty()) {
				Iterator<Entry<String, String>> it = xmlPropsMap.entrySet()
						.iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) it
							.next();
					String key = entry.getKey();
					String value = entry.getValue();
					if (!StringUtil.isNullOrEmpty(key)
							&& !StringUtil.isNullOrEmpty(value)) {
						Element temp = document.createElement(key);
						temp.setTextContent(value);
						nokia.appendChild(temp);
					}
				}
			}

			root.appendChild(nokia);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transFormer = transFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);

			// export string
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transFormer.transform(domSource, new StreamResult(bos));
			xmlStr = bos.toString();

			// -------
			// // save as file
			// File file = new File("TelePhone.xml");
			// if (!file.exists()) {
			// file.createNewFile();
			// }
			// FileOutputStream out = new FileOutputStream(file);
			// StreamResult xmlResult = new StreamResult(out);
			// transFormer.transform(domSource, xmlResult);
			// --------
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("createXML:output=" + xmlStr);

		return xmlStr;
	}

	public static HashMap<String, String> parserXML(String strXML) {
		System.out.println("parserXML:input=" + strXML);
		HashMap<String, String> propsMap = new HashMap<String, String>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader sr = new StringReader(strXML);
			InputSource is = new InputSource(sr);
			Document doc = builder.parse(is);
			Element rootElement = doc.getDocumentElement();
			NodeList properties = rootElement.getChildNodes();
			for (int i = 0; i < properties.getLength(); i++) {
				Node property = properties.item(i);
				String nodeName = property.getNodeName();
				if ("Table".equals(nodeName)) {
					NodeList subProps = property.getChildNodes();
					for (int j = 0; j < subProps.getLength(); j++) {
						Node subProp = subProps.item(j);
						String subNodeName = subProp.getNodeName();
						Node firstChild = subProp.getFirstChild();
						if (firstChild != null) {
							String subNodeValue = firstChild.getNodeValue();
							propsMap.put(subNodeName, subNodeValue);
						} else {
							propsMap.put(subNodeName, null);
						}
					}
				} else {
					Node firstChild = property.getFirstChild();
					if (firstChild != null) {
						String nodeValue = firstChild.getNodeValue();
						propsMap.put(nodeName, nodeValue);
					} else {
						propsMap.put(nodeName, null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("parserXML:output=" + propsMap.toString());
			return propsMap;
		}
	}
}
