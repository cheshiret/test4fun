package com.active.qa.automation.web.testapi.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import com.active.qa.automation.web.testapi.ActionFailedException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Created by tchen on 1/11/2016.
 */
public class XmlParser {
    // Global value so it can be ref'd by the tree-adapter
    Document document;

    // Get the root element (without specifying its name)

    public static void main(String argv[]) {

    }

    public XmlParser(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

        } catch (Exception e) {
            throw new ActionFailedException(e);

        }

    }

    public NodeList getNodesByAttribute(List<Property[]> list) {
        String xpath=constructXPath(list);

        return getNodesByXpath(xpath);
    }

    public Node getNodeByAttribute(List<Property[]> list) {
        String xpath=constructXPath(list);

        return getNodeByXpath(xpath);
    }

    public NodeList getNodesByAttribute(Property... prop) {
        String xpath=constructXPath(prop);

        return getNodesByXpath(xpath);
    }

    public Node getNodeByAttribute(Property... prop) {
        String xpath=constructXPath(prop);

        return getNodeByXpath(xpath);
    }

    public String constructXPath(List<Property[]> list) {
        StringBuffer xpath=new StringBuffer();
        for(Property[] p:list) {
            xpath.append(constructXPath(p));
        }

        return xpath.toString();
    }

    public String constructXPath(Property...props) {
        String tag="*";
        List<String> attrs=new ArrayList<String>();
        for(Property p: props) {
            if(p.getPropertyName().equalsIgnoreCase("tag")) {
                tag=(String)p.getPropertyValue();
            } else {
                attrs.add("@"+p.getPropertyName()+"='"+p.getPropertyValue()+"'");
            }
        }

        StringBuffer xpath=new StringBuffer();
        xpath.append("//");
        xpath.append(tag);
        if(attrs.size()>0) {
            xpath.append("[");
            for(int i=0;i<attrs.size();i++) {
                if(i==0) {
                    xpath.append(attrs.get(i));
                } else {
                    xpath.append(" and ");
                    xpath.append(attrs.get(i));

                }
            }
            xpath.append("]");

        }

        return xpath.toString();
    }

    public NodeList getNodesByXpath(String xpath) {
        try {
            return XPathAPI.selectNodeList(document, xpath);
        } catch (TransformerException e) {
            throw new ActionFailedException(e);
        }
    }

    public Node getNodeByXpath(String xpath) {
        NodeList list=getNodesByXpath(xpath);
        if(list!=null && list.getLength()>0)
            return list.item(0);
        else
            return null;
    }

    public String getNodeAttribute(Node node, String attr){
        return node.getAttributes().getNamedItem(attr).getTextContent();
    }

    public String getNodeContent(Node node){
        return node.getFirstChild().getTextContent();
    }

    public String getNodeContentByAttribute(Property... prop){
        Node node = this.getNodeByAttribute(prop);
        if(node == null) {
//			throw new ActionFailedException("Could not get any node by attribute.");
            System.out.println("Could not get any node by attribute "+prop.toString());
            return null;
        }
        return this.getNodeContent(node);
    }

//	/**
//	 * Get Node value search by nodeName in the given XML file
//	 * @param xmlFile
//	 * @param parentNodeName
//	 * @param row
//	 * @param childNodeName
//	 * @return
//	 */
//	public String getCellData(String xmlFile, String parentNodeName, int row,
//			String childNodeName) {
//
////		String xpath = "//DetailPanel[1]";
//		// Get the root element (using its name)
////		xpath = "/Report";
//
//		// Get all elements directly under the root
////		xpath = "/Report/*";
//
//		// Get all e elements directly under the root
////		xpath = "/Report/Page/ReportFooterPanel";
//
//		// Get all e elements in the document
////		xpath = "//DetailPanel[1]";
//
//		// Get all non-e elements in the document
//		//    xpath = "//*[name() != 'parkNo']";              // 1 2 3 5 7 8 9
//
//		// Get all e elements directly under an elem1 element
//		//    xpath = "//DetailPanel/parkNo";                       // 10 11
//
//		// Get all e elements anywhere under an elem1 element
//		//    xpath = "//Report//parkNo";                      // 4 6 10 11
//
//		// Get all elements with at least one child element
//		//    xpath = "//*[*]";                          // 1 2 3 5 8
//
//		// Get all elements without a child element
//		//    xpath = "//*[not(*)]";                     // 4 6 7 9 10 11 12
//
//		// Get all elements with at least one child e element
//		//	    xpath = "//*[e]";                          // 1 3 5 8
//
//		// Get all elements with more than one child e elements
//		//	    xpath = "//*[count(e)>1]";                 // 8
//
//		// Get all non-e elements without an e child element
//		//	    xpath = "//*[not(e) and name() != 'e']";   // 2 7 9
//
//		// Get all level-4 e elements (the root being at level 1)
//		//	    xpath = "/*/*/*/e";                        // 4
//
//		// Get all elements with more than one child e elements
//		//	    xpath = "//*[count(e)>1]";                 // 8
//
//		//		XPath 1.0 does not support regular expressions to match element names. However, it is possible to perform some very simple matches on element names.
//		// Get all elements whose name starts with el
//		//    xpath = "//*[starts-with(name(), 'el')]";  // 2 3 5 7 8 9
//
//		// Get all elements whose name contains with lem1
//		//	    xpath = "//*[contains(name(), 'lem1')]";   // 2 8
//
//		//Sets of elements can also be combined using the union operator |
//		// Get all e elements directly under either the root or an elem2 element
//		//    xpath = "/*/e | //elem2/e";                // 4 12
//
//		//    xpath = "//*[.='AIKEN']";
//
//		//"C:/0_0/ParkDepositReport.XML"
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		//factory.setValidating(true);
//		//factory.setNamespaceAware(true);
//		try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			document = builder.parse(xmlFile);
//
//			//			String xPath = "//" + parentNodeName + "[" + row + "]";
//			//			NodeList nodelist = XPathAPI.selectNodeList(document, xPath);
//
//			NodeList nodelist = document
//					.getElementsByTagName("//weather/report/locality");
//
//			//	System.err.println(nodelist.getLength());
//
//			for (int i = 0; i < nodelist.getLength(); i++) {
//				Node child = nodelist.item(i);
//
//				NodeList nodes = child.getChildNodes();
//
//				//		System.err.println(nodes.getLength());
//
//				for (int j = 0; j < nodes.getLength(); j++) {
//					Node childElement = nodes.item(j);
//
//					if (!(childElement instanceof Text || childElement instanceof Comment)) {
//						//		System.err.println(childElement.getNodeName() + " - " + childElement.getNodeType() + " - " + childElement.getFirstChild().getNodeValue());
//
//						if (childElement.getNodeName().equalsIgnoreCase(
//								childNodeName)) {
//							return childElement.getFirstChild().getNodeValue();
//
//						}
//					}
//				}
//			}
//
//		} catch (SAXException sxe) {
//			// Error generated during parsing)
//			Exception x = sxe;
//			if (sxe.getException() != null)
//				x = sxe.getException();
//			x.printStackTrace();
//
//		} catch (ParserConfigurationException pce) {
//			// Parser with specified options can't be built
//			pce.printStackTrace();
//
//		} catch (IOException ioe) {
//			// I/O error
//			ioe.printStackTrace();
//		}
//		return "";
//
//	}
}

