package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface ISearch {
  /**
   * Search all TestObjects matching the given property
   *
   * @param propertyKey1 - the property key
   * @param value1       - the property value
   * @return
   */
  public IHtmlObject[] getHtmlObject(String propertyKey, Object value);

  /**
   * Search all TestObjects matching the given properties
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @return
   */
  public IHtmlObject[] getHtmlObject(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all TestObjects matching the given properties
   *
   * @param propertyKey1 - the property key
   * @param value1       - the property value
   * @param mapAble
   * @param top          - the top level GuiTestObject starting the search from
   * @return
   */
  public IHtmlObject[] getHtmlObject(String propertyKey, Object value, IHtmlObject top);

  /**
   * Search all TestObjects matching the given properties
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param mapAble
   * @param top          - the top level GuiTestObject starting the search from
   * @return
   */
  public IHtmlObject[] getHtmlObject(String propertyKey1, Object value1, String propertyKey2, Object value2, IHtmlObject top);

  /**
   * Search all TestObjects matching the given properties
   *
   * @param property - the array of property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getHtmlObject(Property[] property);

  /**
   * Search all TestObjects matching the given properties
   *
   * @param property - the array of property
   * @param mapAble
   * @param top      - the top level object starting the search from
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getHtmlObject(Property[] property, IHtmlObject top);

  /**
   * Search all TestObjects matching the given properties
   * Each element in the list is a property set for a TestObject. The method iterate searching from descendant of the previous object in the list
   *
   * @param list - the list of subitems
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getHtmlObject(List<Property[]> list);

  public IHtmlObject[] getHtmlObject(List<Property[]> list, IHtmlObject top);

  /**
   * Search all Html.TABLE TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTableTestObject(String propertyKey, Object value);

  /**
   * Search all Html.TABLE TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param mapAble
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTableTestObject(String propertyKey1, Object value1, String propertyKey2, Object value2);


  /**
   * Search all Html.SELECT TestObjects matching the given properties
   *
   * @param property
   * @param mapAble
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTableTestObject(Property[] property);

  /**
   * Search all Html.TABLE TestObjects matching the given properties
   *
   * @param property
   * @param mapAble
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTableTestObject(Property[] property, IHtmlObject top);

  /**
   * Search all Html.SELECT TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getDropdownList(String propertyKey, Object value);

  /**
   * Search all Html.SELECT TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param mapAble
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all Html.SELECT TestObjects matching the given properties
   *
   * @param property
   * @param mapAble
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getDropdownList(Property[] property);

  /**
   * Search all Html.INPUT.text TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getDropdownList(Property[] property, IHtmlObject top);

  /**
   * Search all Html.SELECT TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextField(String propertyKey, Object value);

  /**
   * Search all Html.INPUT.text TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextField(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all Html.INPUT.text TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextField(Property[] property);

  /**
   * Search all Html.INPUT.text TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextField(Property[] property, IHtmlObject top);

  /**
   * Search all html.INPUT.password TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getPasswordField(String propertyKey, Object value);

  /**
   * Search all html.INPUT.password TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getPasswordField(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all html.INPUT.password TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getPasswordField(Property[] property);

  /**
   * Search all html.INPUT.password TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getPasswordField(Property[] property, IHtmlObject top);

  /**
   * Search all Html.INPUT.file TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return
   */
  public IHtmlObject[] getFileField(String propertyKey, Object value);


  /**
   * Search all Html.INPUT.file TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return
   */
  public IHtmlObject[] getFileField(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all Html.INPUT.file TestObjects matching the given properties
   *
   * @param property
   * @return
   */
  public IHtmlObject[] getFileField(Property[] property);

  /**
   * Search all Html.INPUT.file TestObjects matching the given properties
   *
   * @param property
   * @param top
   * @return
   */
  public IHtmlObject[] getFileField(Property[] property, IHtmlObject top);

  /**
   * Search all text area TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextArea(String propertyKey, Object value);

  /**
   * Search all text area TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextArea(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Set text area content by index and property
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param forced
   * @param index
   */
  void setTextArea(String propertyKey, Object value, String text, boolean forced, int index);

  /**
   * Search all text area TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextArea(Property[] property);

  /**
   * Search all text area TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getTextArea(Property[] property, IHtmlObject top);

  /**
   * Search all IFrameText fields matching the given properties
   *
   * @param property
   * @param top
   * @return -  array of TestObjects found
   */
  public IHtmlObject[] getIFrameTextField(Property[] property, IHtmlObject top);


  /**
   * Search all Html.SELECT.checkbox TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getCheckBox(String propertyKey, Object value);

  /**
   * Search all Html.SELECT.checkbox TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all Html.SELECT.checkbox TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getCheckBox(Property[] property);

  /**
   * Search all Html.SELECT.checkbox TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getCheckBox(Property[] property, IHtmlObject top);

  public IHtmlObject[] getCheckBox(List<Property[]> property);

  /**
   * Search all Html.SELECT.radio TestObjects matching the given properties
   *
   * @param propertyKey
   * @param value
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getRadioButton(String propertyKey, Object value);

  /**
   * Search all Html.SELECT.radio TestObjects matching the given properties
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getRadioButton(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Search all Html.SELECT.radio TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getRadioButton(Property[] property);

  /**
   * Search all Html.SELECT.radio TestObjects matching the given properties
   *
   * @param property
   * @return - array of TestObjects found
   */
  public IHtmlObject[] getRadioButton(Property[] property, IHtmlObject top);

  /**
   * Get all TestObjects on the UI
   *
   * @return
   */
  public IHtmlObject[] getAllTestObjects();

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param propertyName
   * @param value
   * @return
   */
  public boolean checkHtmlObjectExists(String propertyName, Object value);

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param propertyName
   * @param value
   * @param top          - search object from
   * @return
   */
  public boolean checkHtmlObjectExists(String propertyName, Object value, IHtmlObject top);

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param propertyName1
   * @param value1
   * @param propertyName2
   * @param value2
   * @return
   */
  public boolean checkHtmlObjectExists(String propertyName1, Object value1, String propertyName2, Object value2);

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param propertyName1
   * @param value1
   * @param propertyName2
   * @param value2
   * @param top           - search object from
   * @return
   */
  public boolean checkHtmlObjectExists(String propertyName1, Object value1, String propertyName2, Object value2, IHtmlObject top);

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param property
   * @return - true if test object exist, false otherwise.
   */
  public boolean checkHtmlObjectExists(Property[] property);

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param property - array of property
   * @param top      - search object from
   * @return - true if test object exist, false otherwise.
   */
  public boolean checkHtmlObjectExists(Property[] property, IHtmlObject top);

  /**
   * This method finds all objects in HtmlDocument and checks if there is an object whose property value
   * is the same as the given value.
   *
   * @param list    - list of subitems property
   * @param mapAble
   * @return - true if test object exist, false otherwise.
   */
  public boolean checkHtmlObjectExists(List<Property[]> list);

  /**
   * Retrieve the text property of the TestObject
   *
   * @param propertyName
   * @param value
   * @return
   */
  public String getObjectText(String propertyName, Object value);

  /**
   * Retrieve the text property of the TestObject
   *
   * @param propertyName1
   * @param value1
   * @param propertyName2
   * @param value2
   * @return
   */
  public String getObjectText(String propertyName1, Object value1, String propertyName2, Object value2);

  public String getObjectText(Property... properties);

  public String getObjectText(List<Property[]> list);

  public String getObjectAttribute(String propertyName, Object value, String attrName);

  public String getObjectAttribute(String propertyName1, Object value1, String propertyName2, Object value2, String attrName);

  public String getObjectAttribute(String attrName, Property... properties);

  public String getObjectAttribute(List<Property[]> list, String attrName);


  boolean checkHtmlObjectDisplayed(String propertyKey, Object value);

  boolean checkHtmlObjectDisplayed(String propertyKey1, Object value1,
                                   String propertyKey2, Object value2);

  boolean checkHtmlObjectDisplayed(Property[] property);

  boolean checkHtmlObjectDisplayed(String propertyName, Object value,
                                   IHtmlObject top);

  boolean checkHtmlObjectDisplayed(String propertyName1, Object value1,
                                   String propertyName2, Object value2, IHtmlObject top);

  boolean checkHtmlObjectDisplayed(Property[] property, IHtmlObject top);

  boolean checkHtmlObjectDisplayed(List<Property[]> list);

  public boolean checkHtmlObjectEnabled(Property[] propertyArray,
                                        IHtmlObject iHtmlObject);

  public boolean checkHtmlObjectEnabled(Property[] property);

  public boolean checkHtmlObjectEnabled(String key, Object value);

  public boolean checkHtmlObjectEnabled(String key1, Object value1, String key2, Object value2);

  public List<String> getObjectsText(Property[] property);

  public String getTextAreaValue(Property[] property, IHtmlObject top);

  public String getTextAreaValue(Property[] propertyArray, int index, IHtmlObject top);

  public String getTextAreaValue(Property[] property);

  public String getTextAreaValue(String propertyKey, Object value);
}

