package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.html.IText;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface ISetText {
  /**
   * Set the password value for the TextField with the given property
   *
   * @param propertyKey
   * @param value
   * @param text
   */
  public void setPasswordField(Property[] property, String text);

  /**
   * Set the password value for the TextField with the given property
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param forced      - if true and object not found, throw exceptions
   */
  public void setPasswordField(Property[] property, String text, boolean forced);

  public void setPasswordField(String propertyKey, Object value, String text, boolean forced);

  public void setPasswordField(String propertyKey, Object value, String text);

  /**
   * Set the file value for the TextField with the given property
   *
   * @param propertyKey
   * @param value
   * @param text
   */
  public void setFileField(String propertyKey, Object value, String text);

  /**
   * Set the file value for the TextField with the given property
   *
   * @param propertyKey
   * @param value
   * @param text
   */
  public void setFileField(String propertyKey, Object value, String text, boolean forced);


  /**
   * Set the file value for the TextField with the given property
   *
   * @param propertyKey1
   * @param value1
   * @param text
   * @param top
   */
  public void setTextField(String propertyKey1, Object value1, String text, IHtmlObject top);

  public void setTextField(String propertyKey1, Object value1, String text, IText.Event... event);

  /**
   * Set the text value for TextArea matching the given property
   *
   * @param propertyKey
   * @param value
   * @param text
   */
  public void setTextArea(String propertyKey, Object value, String text);

  /**
   * set the text value for TextArea matching the given property
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param forced
   */
  public void setTextArea(String propertyKey, Object value, String text, boolean forced);

  /**
   * Set the text value for TextArea matching the given property
   *
   * @param properties
   * @param text
   */
  public void setTextArea(Property[] properties, String text);

  /**
   * set the text value for TextArea matching the given property
   *
   * @param properties
   * @param text
   * @param forced
   */
  public void setTextArea(Property[] properties, String text, boolean forced);

  public void setTextArea(String propertyKey, Object value, String text,
                          boolean forced, int index, IHtmlObject top);

  public void setTextArea(Property[] properties, String text, boolean forced, int index);

  public void setTextArea(Property[] properties, String text, boolean forced, int index, IHtmlObject top);

  public void setTextArea(String propertyKey, Object value, String text, boolean forced, int index);

  /**
   * Get the text value of the TextField matching the given property
   *
   * @param propertyKey
   * @param value
   * @return
   */
  public String getTextFieldValue(String propertyKey, Object value);

  /**
   * Get the text value of the TextField object matching the given property and object index
   *
   * @param propertyKey
   * @param value
   * @param objectIndex
   * @return
   */
  public String getTextFieldValue(String propertyKey, Object value, int objectIndex);

  /**
   * Get the text value of the TextField matching the given property
   *
   * @param propertyKey
   * @param value
   * @param top         - search the object from
   * @return
   */
  public String getTextFieldValue(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Get the text value of TextField matching the given property and object index
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param objectIndex
   * @return
   */
  public String getTextFieldValue(String propertyKey1, Object value1, String propertyKey2, Object value2, int objectIndex);

  /**
   * Get the text value of the TextField matching the given property
   *
   * @param property - the property array
   * @return
   */
  public String getTextFieldValue(Property[] property);

  /**
   * Get the text value of the TextField matching the given property and object index
   *
   * @param property
   * @param objectIndex
   * @return
   */
  public String getTextFieldValue(Property[] property, int objectIndex);

  /**
   * Get the text value of the TextField matching the given property
   *
   * @param property - the property array
   * @param top      - search the object from
   * @return
   */
  public String getTextFieldValue(Property[] property, IHtmlObject top);

  /**
   * Set the text value for the 1st TextField with the given property.
   *
   * @param propertyKey
   * @param value
   * @param text
   */
  public void setTextField(String propertyKey, Object value, String text);

  /**
   * Set the text value for the 1st TextField with the given property.
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param forced
   */
  public void setTextField(String propertyKey, Object value, String text, boolean forced);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param index
   */
  public void setTextField(String propertyKey, Object value, String text, int index);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param property
   * @param text
   * @param index
   */
  public void setTextField(Property[] property, String text, int index);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param forced
   * @param index
   */
  public void setTextField(String propertyKey, Object value, String text, boolean forced, int index);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param propertyKey
   * @param value
   * @param text
   * @param forced
   * @param index
   * @param top         - the top level GuiTestObject searching from
   */
  public void setTextField(String propertyKey, Object value, String text, boolean forced, int index, IHtmlObject top);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param text
   * @param forced
   * @param index
   */
  public void setTextField(String propertyKey1, Object value1, String propertyKey2, Object value2, String text, boolean forced, int index);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param text
   * @param forced
   * @param index
   * @param top          - the top level GuiTestObject searching from
   */
  public void setTextField(String propertyKey1, Object value1, String propertyKey2, Object value2, String text, boolean forced, int index, IHtmlObject top);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param property
   * @param text
   * @param forced
   * @param index
   */
  public void setTextField(Property[] property, String text, boolean forced, int index);

  public void setTextField(Property[] property, String text);

  /**
   * Set the text value for TextField with the given property.
   *
   * @param property
   * @param text
   * @param forced
   * @param index
   * @param top      - the top level GuiTestObject searching from
   */
  public void setTextField(Property[] property, String text, boolean forced, int index, IHtmlObject top);

  public void setIFrameTextField(Property[] property, String text, boolean forced, int index, IHtmlObject top);

  public void setTextField(List<Property[]> list, String text, boolean forced, int index);

  public void setTextField(List<Property[]> list, String text, boolean forced);

  public void setTextField(List<Property[]> list, String text);

  public void setCalendarField(String propertyKey, Object value, String text);

  public void setCalendarField(String propertyKey, Object value, String text, boolean forced);

  public void setCalendarField(String propertyKey, Object value, String text, int index);

  public void setCalendarField(String propertyKey, Object value, String text, boolean forced, int index);

  public void setCalendarField(String propertyKey, Object value, String text, boolean forced, int index, IHtmlObject top);

  public void setCalendarField(Property[] property, String text);

  public void setCalendarField(Property[] property, String text, int index);

  public void setCalendarField(Property[] property, String text, int index, IHtmlObject top);

  public void setCalendarField(Property[] property, String text, boolean forced);

  public void setCalendarField(Property[] property, String text, boolean forced, int index);

  public void setCalendarField(Property[] property, String text, boolean forced, int index, IHtmlObject top);

  public void setCalendarField(Property[] property, String text, boolean forced, boolean forcedToSetReadOnly, int index, IHtmlObject top);

  /**
   * Get the text value of the TextField matching the given property
   *
   * @param property
   * @param top
   * @param objectIndex
   * @return
   */
  String getTextFieldValue(Property[] property, int objectIndex, IHtmlObject top);


  /*
   * setTextField methods below are event related
   */
  public void setTextField(Property[] property, String text, boolean forced, int index, IText.Event... event);

  public void setTextField(String propertyKey1, Object value1, String text, IHtmlObject top, IText.Event... event);

  public void setTextField(String propertyKey1, Object value1, String propertyKey2, Object value2, String text, boolean forced, int index, IHtmlObject top, IText.Event... event);

  public void setTextField(String propertyKey1, Object value1, String propertyKey2, Object value2, String text, boolean forced, int index, IText.Event... event);

  public void setTextField(String propertyKey, Object value, String text, boolean forced, int index, IHtmlObject top, IText.Event... event);

  public void setTextField(String propertyKey, Object value, String text, boolean forced, int index, IText.Event... event);

  public void setTextField(String propertyKey, Object value, String text, int index, IText.Event... event);

  public void setTextField(Property[] property, String text, boolean forced, int index, IHtmlObject top, IText.Event... event);

  public void setTextField(Property[] property, String text, boolean forced, boolean forcedToSetReadOnly, int index, IHtmlObject top, IText.Event... event);

}

