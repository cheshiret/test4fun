package com.active.qa.automation.web.testrunner;

import com.active.qa.automation.web.testrunner.util.TestProperty;
import com.active.qa.automation.web.testrunner.util.Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

/**
 * Created by tchen on 1/18/2016.
 */
public class UserTestSuites {
  private static String xml_filepath;
  private static String xsd_filepath;
  private static Document document = null;

  public static UserTestSuites getInstance() {
    TestProperty.load();
    UserTestSuites user_TestSuites = new UserTestSuites();
    xml_filepath = Util.getProjectPath() + "/properties/" + TestProperty.get("TestSuites.file");
    xsd_filepath = Util.getProjectPath() + "/properties/" + TestProperty.get("TestSuites.XSD.file");
    document = getDocument(xml_filepath);

    return user_TestSuites;
  }

  private static Document getDocument(String filepath) {
//		if(!validateDocByXSD(document)){
//			System.out.println("\nTest Suite file validation failed\n");
//		}
    File file = new File(filepath);
    SAXReader reader = new SAXReader();
    Document document = null;
    try {
      document = reader.read(file);
    } catch (DocumentException e) {
      System.out.println("\nPlease check your test suite definition file at " + filepath + "\n");
//			e.printStackTrace();
    }
    return document;
  }

  private static Boolean updateDocument(Document doc) {
    if (!validateDocByXSD(doc)) {
      System.out.println("\nXML File Validation Failed!\n");
      return false;
    }
    XMLWriter writer;
    try {
      writer = new XMLWriter(new FileWriter(xml_filepath));
      writer.write(doc);
      writer.close();
    } catch (IOException e) {
      System.out.println("\nFailed to save test suite file.\n");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public List<String> getTestSuiteName() {
    List<String> testsuites = new ArrayList<String>();
    try {
      List list = document.selectNodes("//TestSuite");
      Iterator iter = list.iterator();

      while (iter.hasNext()) {
        Element element = (Element) iter.next();
        testsuites.add(element.attributeValue("name"));
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return testsuites;

  }

  public List<String> getTestCasesbyTestSuite(String testsuite) {
    List list = document.selectNodes("//TestSuite[@name='" + testsuite + "']/TestCase");
    if (list.size() == 0) {
      System.out.println("\nTest suite: " + testsuite + " has not been defined yet\n");
      return null;
    }
    Iterator iter = list.iterator();
    List<String> testcases = new ArrayList<String>();
    while (iter.hasNext()) {
      Element element = (Element) iter.next();
      testcases.add(element.getText());
    }
    return testcases;

  }

  public void parseTestSuitesCMD(String testSuite, String operator, String mode, String path) {
    String[] cases = null;
    cases = path.trim().split(",");
    List<String> caselist = new ArrayList<String>();
    if (mode.matches("-l")) {
      caselist = removeDuplicatedItem(Arrays.asList(cases));
    } else if (mode.matches("-d")) {
      //upload test case list file to raon-toolsvm: /home/deploy/TestDriver/temp/
      caselist = getTestCaseFromFile(path);
    }

    if (operator.matches("new")) {
      createTestSuite(testSuite, caselist);
    }

    if (operator.matches("add")) {
      addTestCasesForTestSuite(testSuite, caselist);
    }

    if (operator.matches("remove")) {
      removeTestCasesForTestSuite(testSuite, caselist);
    }
  }

  private void createTestSuite(String testSuite, List<String> testCases) {
    //Make sure that test suite has not been defined
    //Make sure that test cases are not duplicated
    if (getTestSuiteName().contains(testSuite)) {
      System.out.println("\nTest Suite: " + testSuite + " has been defined already\n");
      return;
    }
    System.out.println("\nCreating new Test Suite: " + testSuite + "... ...\n");

    Element element = document.getRootElement();
    Element elm = element.addElement("TestSuite").addAttribute("name", testSuite);
    for (int i = 0; i < testCases.size(); i++) {
      elm.addElement("TestCase").addText(testCases.get(i));
    }
    if (updateDocument(document)) {
      System.out.println("Done!\n");
    } else {
      System.out.println("Failed to create test suite: " + testSuite + "\n");
    }

  }

  private void addTestCasesForTestSuite(String testSuite, List<String> testCases) {
    //Make sure that test suite has been defined
    //Make sure that test cases are not duplicated and also has not been defined in test suite
    List<String> temp = getTestCasesbyTestSuite(testSuite);
    if (temp == null) {
      return;
    }

    List<String> temp1 = removeDuplicatedItem(testCases, temp);

    Node node = document.selectSingleNode("//TestSuite[@name='" + testSuite + "']");
    Element element = (Element) node;
    for (int i = 0; i < temp1.size(); i++) {
      element.addElement("TestCase").addText(temp1.get(i));
    }
    if (updateDocument(document)) {
      System.out.println("\nAdd Cases to Test Suite: " + testSuite + " successfully\n");
    } else {
      System.out.println("\nFailed to add Cases to Suite: " + testSuite + "\n");
    }
  }

  private void removeTestCasesForTestSuite(String testSuite, List<String> testCases) {
    //Make sure that test suite has been defined
    //Make sure that test cases are not duplicated and also has been defined in test suite
    List<String> def_cases = getTestCasesbyTestSuite(testSuite);
    if (def_cases == null) {
      return;
    }
    //test cases need to remove
    List<String> rem_cases = removeNotDefinedItem(testCases, def_cases);

    //Delete all test cases
    if (def_cases.equals(rem_cases)) {
      System.out.println("Warning: There will be none case in test suite: " + testSuite);
      System.out.println("\nIf you want to delete this test suite, please use command: --testsuites <suite name> remove\n");
      return;
    } else {
      List<String> list = document.selectNodes("//TestSuite[@name='" + testSuite + "']/TestCase");
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Element elem1 = (Element) iter.next();
        //record rem_case index
        if (rem_cases.contains(elem1.getText())) {
          elem1.detach();
        }
      }
    }

    if (updateDocument(document)) {
      System.out.println("\nRemove Cases from Suite: " + testSuite + " successfully\n");
    } else {
      System.out.println("\nFailed to remove Cases from Suite: " + testSuite + "\n");
    }
  }

  public void deleteTestSuite(String testSuite) {
    //Make sure we have defined cust test suites
    List<String> def_suites = getTestSuiteName();
    if (def_suites == null) {
      return;
    }
    //Make sure that test suite has been defined
    List<String> suitelist = new ArrayList<String>();
    suitelist.add(testSuite);
    if (!def_suites.contains(testSuite)) {
      System.out.println("\nTest Suite: " + testSuite + " has not been defined yet!\n");
      return;
    }

    suitelist = removeNotDefinedItem(suitelist, def_suites);
    Element root = document.getRootElement();
    Element elem = (Element) document.selectSingleNode("//TestSuite[@name='" + testSuite + "']");
    root.remove(elem);
    if (updateDocument(document)) {
      System.out.println("\nDelete test suite(s): " + testSuite + " successfully\n");
    } else {
      System.out.println("\nFailed to delete test suite(s): " + testSuite + "\n");
    }
  }

  /**
   * Remove user input duplicated test case
   */
  public List<String> removeDuplicatedItem(List<String> cases) {
    Set<String> set = new HashSet<String>(cases);
    List<String> list = new ArrayList<String>(set);
    return (list);
  }

  /**
   * list1: New test cases
   * list2: Already defined test cases
   * For adding test cases to Test Suite
   * Remove duplicated test case which have already defined
   *
   * @param list1
   * @param list2
   * @return
   */
  private List<String> removeDuplicatedItem(List<String> list1, List<String> list2) {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < list1.size(); i++) {
      if (!list2.contains(list1.get(i))) {
        list.add(list1.get(i));
      }
    }
    return list;

  }

  /**
   * list1: Remove test cases
   * list2: Already defined test cases
   * For removing test cases from Test Suite
   * Remove not defined test case
   *
   * @param list1
   * @param list2
   * @return
   */
  private List<String> removeNotDefinedItem(List<String> list1, List<String> list2) {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < list1.size(); i++) {
      if (list2.contains(list1.get(i))) {
        list.add(list1.get(i));
      }
    }
    return list;
  }

  public List<String> getTestCaseFromFile(String filepath) {
    String fileName = "";
    if (filepath.lastIndexOf('/') > 0) {
      fileName = filepath;
    } else {
      fileName = TestProperty.get("TestSuites.upload.local") + filepath;
    }

    Set<String> set = new HashSet<String>();
    FileReader reader;
    try {
      reader = new FileReader(fileName);
      BufferedReader buffer = new BufferedReader(reader);
      String line = "";
      while ((line = buffer.readLine()) != null) {
        if (line.replaceAll("\\s", "").length() > 0) {
          set.add(line);
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("\nPlease check your test suite definition file at " + fileName + "\n");
//			e.printStackTrace();
    } catch (IOException e) {
      System.out.println("\nPlease check your test suite definition file at " + fileName + "\n");
//			e.printStackTrace();
    }
    List<String> cases = new ArrayList<String>(set);
    return cases;
  }

  public static boolean validateDocByXSD(Document document) {
    try {
      XMLErrorHandler errorHandler = new XMLErrorHandler();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);
      SAXParser parser = factory.newSAXParser();
      parser.setProperty(
          "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
          "http://www.w3.org/2001/XMLSchema");
      parser.setProperty(
          "http://java.sun.com/xml/jaxp/properties/schemaSource",
          xsd_filepath);
      SAXValidator validator = new SAXValidator(parser.getXMLReader());
      validator.setErrorHandler(errorHandler);
      validator.validate(document);
      XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
      if (errorHandler.getErrors().hasContent()) {
        writer.write(errorHandler.getErrors());
        return false;
      } else {
//				System.out.println("Validate Pass");
        return true;
      }
    } catch (Exception ex) {
//			System.out.println("Validate Failed");
      System.out.println(ex.getMessage());
      return false;
    }
  }
}
