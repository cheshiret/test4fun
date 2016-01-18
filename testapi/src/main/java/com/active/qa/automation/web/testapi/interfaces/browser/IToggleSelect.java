package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IToggleSelect {
  /**
   * Check if the Checkbox with the given property selected or not
   *
   * @param propertyKey
   * @param value
   * @return
   */
  public boolean isCheckBoxSelected(String propertyKey, Object value);

  /**
   * Check if the Checkbox with the given property selected or not
   *
   * @param propertyKey
   * @param value
   * @param top         - search the object from
   * @return
   */
  public boolean isCheckBoxSelected(String propertyKey, Object value, IHtmlObject top);

  public boolean isCheckBoxSelected(String propertyKey, Object value, int index);

  /**
   * Check if the Checkbox with the given property selected or not
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param top          - search the object from
   * @return
   */
  public boolean isCheckBoxSelected(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Check if the Checkbox with the given property selected or not
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @return
   */
  public boolean isCheckBoxSelected(String propertyKey1, Object value1, String propertyKey2, Object value2, IHtmlObject top);

  /**
   * Check if the Checkbox with the given property selected or not
   *
   * @param property
   * @return
   */
  public boolean isCheckBoxSelected(Property[] property);

  /**
   * Check if the Checkbox with the given property selected or not
   *
   * @param property
   * @return
   */
  public boolean isCheckBoxSelected(Property[] property, IHtmlObject top);

  /**
   * Tick off the check box matching the given property
   *
   * @param propertyKey
   * @param value
   */
  public void unSelectCheckBox(String propertyKey, Object value);

  /**
   * Tick off the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   */
  public void unSelectCheckBox(String propertyKey, Object value, int index);

  /**
   * Tick off the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   * @param top         - the top level TestObject start searching from
   */
  public void unSelectCheckBox(String propertyKey, Object value, int index, IHtmlObject top);

  /**
   * Tick off the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey1
   * @param value1
   */
  public void unSelectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Tick off the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey1
   * @param value1
   * @param index
   */
  public void unSelectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, int index);

  /**
   * Tick off the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey1
   * @param value1
   * @param index
   * @param top          - the top level GuiTestObject starting search from.
   */
  public void unSelectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, IHtmlObject top);

  /**
   * Tick off the check box matching the given property
   *
   * @param property
   * @param index
   */
  public void unSelectCheckBox(Property[] property, int index);

  /**
   * Tick off the check box matching the given property
   *
   * @param property
   * @param index
   * @param top      - the top level TestObject starting the search from
   */
  public void unSelectCheckBox(Property[] property, int index, IHtmlObject top);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey
   * @param value
   */
  public void selectCheckBox(String propertyKey, Object value);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param forced
   */
  public void selectCheckBox(String propertyKey, Object value, boolean forced);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   */
  public void selectCheckBox(String propertyKey, Object value, int index);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   * @param forced
   */
  public void selectCheckBox(String propertyKey, Object value, int index, boolean forced);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   * @param top         - the top level GuiTestObject starting the search from
   */
  public void selectCheckBox(String propertyKey, Object value, int index, IHtmlObject top);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   * @param forced
   * @param top         - the top level GuiTestObject starting the search from
   */
  public void selectCheckBox(String propertyKey, Object value, int index, boolean forced, IHtmlObject top);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   */
  public void selectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param forced
   */
  public void selectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, boolean forced);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param index
   */
  public void selectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, int index);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param index
   * @param forced
   */
  public void selectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, boolean forced);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param index
   * @param top          - the top level GuiTestObject starting the search from
   */
  public void selectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, IHtmlObject top);

  /**
   * Tick on the check box matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param index
   * @param forced
   * @param top          - the top level GuiTestObject starting the search from
   */
  public void selectCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, boolean forced, IHtmlObject top);

  /**
   * Tick on the check box matching the given property
   *
   * @param property
   */
  public void selectCheckBox(Property[] property);

  /**
   * Tick on the check box matching the given property
   *
   * @param property
   * @param forced
   */
  public void selectCheckBox(Property[] property, boolean forced);


  /**
   * Tick on the check box matching the given property
   *
   * @param property
   * @param index
   */
  public void selectCheckBox(Property[] property, int index);

  /**
   * Tick on the check box matching the given property
   *
   * @param property
   * @param index
   * @param forced
   */
  public void selectCheckBox(Property[] property, int index, boolean forced);

  /**
   * Tick on the check box matching the given property
   *
   * @param property
   * @param index
   * @param top      - the top level GuiTestObject starting the search from
   */
  public void selectCheckBox(Property[] property, int index, IHtmlObject top);

  /**
   * Tick on the check box matching the given property
   *
   * @param property
   * @param index
   * @param forced
   * @param top      - the top level GuiTestObject starting the search from
   */
  public void selectCheckBox(Property[] property, int index, boolean forced, IHtmlObject top);

  public void selectCheckBox(List<Property[]> property);

  public void selectCheckBox(List<Property[]> property, int index);

  public void selectCheckBox(List<Property[]> property, int index, boolean forced);

  public void unSelectCheckBox(List<Property[]> property);

  public void unSelectCheckBox(List<Property[]> property, int index);

  public void unSelectCheckBox(List<Property[]> property, int index, boolean forced);

  /**
   * Tick on the check box matching the given property
   *
   * @param property
   */
  public void unSelectCheckBox(Property[] property);

  /**
   * Select first radio button matching the given property
   *
   * @param propertyKey
   * @param value
   */
  public void selectRadioButton(String propertyKey, Object value);

  /**
   * Select <param> index radio button matching the given property
   *
   * @param propertyKey
   * @param value
   * @param index
   */
  public void selectRadioButton(String propertyKey, Object value, int index);

  /**
   * Select first radio button matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   */
  public void selectRadioButton(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Select <param> index radio button matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param index
   */
  public void selectRadioButton(String propertyKey1, Object value1, String propertyKey2, Object value2, int index);

  /**
   * Select <param> index radio button matching the given property
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param index
   * @param forced       - if true and Object not found, throw exception
   */
  public void selectRadioButton(String propertyKey1, Object value1, String propertyKey2, Object value2, boolean forced, int index);

  /**
   * Select <param> index radio button matching the given property
   *
   * @param property
   * @param index
   */
  public void selectRadioButton(Property[] property, int index);

  /**
   * Select <param> index radio button matching the given property
   *
   * @param property
   * @param index
   * @param forced   - if true and Object not found, throw exception
   */
  public void selectRadioButton(Property[] property, boolean forced, int index);

  /**
   * Select <param> index radio button matching the given property
   *
   * @param property
   * @param index
   * @param forced   - if true and Object not found, throw exception
   * @param top      - the TestObject to start searching from
   */
  public void selectRadioButton(Property[] property, boolean forced, int index, IHtmlObject top);

  boolean isRadioButtonSelected(String propertyKey1, Object value1, String propertyKey2, Object value2);

  boolean isRadioButtonSelected(String propertyKey1, Object value1, String propertyKey2, Object value2, IHtmlObject top);

  boolean isRadioButtonSelected(Property[] property);

  boolean isRadioButtonSelected(Property[] property, IHtmlObject top);

  boolean isRadioButtonSelected(String propertyKey, Object value, IHtmlObject top);

  boolean isRadioButtonSelected(String propertyKey, Object value);

  boolean isRadioButtonSelected(String propertyKey, Object value, int index);

}

