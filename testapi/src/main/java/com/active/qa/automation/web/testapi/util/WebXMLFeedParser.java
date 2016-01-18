package com.active.qa.automation.web.testapi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class is for web XML output. The content is from server directly by doing a post request
 * Created by tchen on 1/11/2016.
 */
public class WebXMLFeedParser {
  XmlParser xmlFeedParser;
  AutomationLogger logger = AutomationLogger.getInstance();

  public WebXMLFeedParser(String requestURL) {
    this.setXMLFeedParser(requestURL);
  }

  public void setXMLFeedParser(String requestURL) {
    logger.info("set XML Feed Parser by request server..." + requestURL);
    URL url = null;
    HttpURLConnection connection = null;
    try {
      url = new URL(requestURL.replaceAll(" ", "%20"));

      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");

      StringBuffer sb = new StringBuffer();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String inputLine = in.toString();
      while ((inputLine = in.readLine()) != null) {
        sb.append(inputLine);
      }
      xmlFeedParser = new XmlParser(sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null)
        connection.disconnect();
    }
  }

  /**
   * Get the given tag's attribute value in XML
   *
   * @param tagName
   * @param attr
   * @return
   */
  public String getXMLOutputParameterValue(String tagName, String attr) {
    String[] names = {tagName};
    return getXMLOutputParameterValue(names, attr);
  }

  /**
   * Get the given tags' attribute value in XML
   *
   * @param tagNames
   * @param attr
   * @return
   */
  public String getXMLOutputParameterValue(String[] tagNames, String attr) {
    List<Property[]> list = new ArrayList<Property[]>();
    for (String name : tagNames) {
      list.add(Property.toPropertyArray("tag", name));
    }

    org.w3c.dom.Node node = xmlFeedParser.getNodeByAttribute(list);

    if (node == null) {
      return null;
    } else {
      org.w3c.dom.Node option = node.getAttributes().getNamedItem(attr);
      if (option != null) {
        return option.getNodeValue();
      } else {
        return null;
      }
    }
  }

  /**
   * Get all given tags' attribute values
   *
   * @param tagName
   * @param attr
   * @return
   */
  public List<String> getXMLOutputParameterValues(String tagName, String... attrs) {
    List<String> values = null;
    org.w3c.dom.NodeList nodeList = xmlFeedParser.getNodesByAttribute(Property.toPropertyArray("tag", tagName));
    if (nodeList == null) {
      return null;
    } else {
      values = new ArrayList<String>();
      for (int i = 0; i < nodeList.getLength(); i++) {
        String value = "";
        for (String attr : attrs) {
          org.w3c.dom.Node option = nodeList.item(i).getAttributes().getNamedItem(attr);
          if (option != null) {
            value += option.getNodeValue() + ",";
          }
        }
        values.add(value.substring(0, value.length() - 1));
      }
    }
    return values;
  }

  /**
   * Get all tags' attributes values
   *
   * @param tagName
   * @param attr1
   * @param attr2
   * @return
   */
  public Map<String, String> getXMLOutputParameterValues(String tagName, String attr1, String attr2) {
    Map<String, String> values = null;
    org.w3c.dom.NodeList nodeList = xmlFeedParser.getNodesByAttribute(Property.toPropertyArray("tag", tagName));
    if (nodeList == null) {
      return null;
    } else {
      values = new HashMap<String, String>();
      String value1 = null;
      String value2 = null;
      for (int i = 0; i < nodeList.getLength(); i++) {
        org.w3c.dom.Node option = nodeList.item(i).getAttributes().getNamedItem(attr1);
        if (option != null) {
          value1 = option.getNodeValue();
        }
        option = nodeList.item(i).getAttributes().getNamedItem(attr2);
        if (option != null) {
          value2 = option.getNodeValue();
        }
        values.put(value1, value2);
      }
    }
    return values;
  }

  /**
   * Check if the tag exists in XML
   *
   * @param tagName
   * @return
   */
  public boolean isXMLOutputParameterExist(String tagName) {
    org.w3c.dom.NodeList nodeList = xmlFeedParser.getNodesByAttribute(Property.toPropertyArray("tag", tagName));
    return nodeList == null ? false : (nodeList.getLength() > 0);
  }

  /**
   * Check if the tag has attributes
   *
   * @param tagName
   * @return
   */
  public boolean isXMLOutputParameterValueExist(String tagName) {
    org.w3c.dom.NodeList nodeList = xmlFeedParser.getNodesByAttribute(Property.toPropertyArray("tag", tagName));
    if (nodeList == null) {
      return false;
    } else {
      boolean result = true;
      for (int i = 0; i < nodeList.getLength(); i++) {
        result &= (nodeList.item(i).getAttributes().getLength() != 0);
      }
      return result;
    }
  }
}

