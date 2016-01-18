package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IDropdownSelect {
  /**
   * Retrieve the current selection value of the dropdown list matching the given property
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param Top         - parent object
   * @return - the current selection, null if dropdown list doesn't exist
   */
  public String getDropdownListValue(Property[] property, int objectIndex, IHtmlObject top);

  /**
   * Retrieve the current selection value of the dropdown list matching the given property
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @return - the current selection, null if dropdown list doesn't exist
   */
  public String getDropdownListValue(String propertyKey, Object value, int objectIndex);

  /**
   * Retrieve the current selection value of the dropdown list matching the given property
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @return
   */
  public String getDropdownListValue(String propertyKey, Object value);

  /**
   * Retrieve the current selection value of the dropdown list matching the given property
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @return - the current selection, null if dropdown list doesn't exist
   */
  public String getDropdownListValue(String propertyKey1, Object value1, String propertyKey2, Object value2, int objectIndex);

  /**
   * Retrieve the current selection value of the dropdown list matching the given property
   *
   * @param property - the array of property, exception ".class"
   * @return - the current selection, null if dropdown list doesn't exist
   */
  public String getDropdownListValue(Property[] property, int objectIndex);

  /**
   * Retrieve the elements of the dropdown list with the given property
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @return - element list
   */
  public List<String> getDropdownElements(String propertyKey, Object value);

  /**
   * Retrieve the elements of the dropdown list with the given properties
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @return - element list
   */
  public List<String> getDropdownElements(String propertyKey1, Object value1, String propertyKey2, Object value2);

  /**
   * Retrieve the elements of the dropdown list with the given properties
   *
   * @param property - the array of property, except property ".class"
   * @return - element list
   */
  public List<String> getDropdownElements(Property[] property);

  /**
   * Retrieve the elements of the dropdown list with the given properties
   *
   * @param property - the array of property, except property ".class"
   * @param top      - search the object from
   * @return - element list
   */
  public List<String> getDropdownElements(Property[] property, IHtmlObject top);

  /**
   * Select the <param> item in the dropdown list with the given property.
   * when dropdown list doesn't exist, do nothing.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param item        - the option name in the dropdown list. do nothing if item is empty.
   */
  public void selectDropdownList(String propertyKey, Object value, String item);

  /**
   * Select the given text in the dropdown list with the given property.
   *
   * @param propertyKey -the property key
   * @param value       -the property value
   * @param text
   * @param index
   */
  public void selectDropdownList(String propertyKey, Object value, String text, int index);

  /**
   * Select the given text in the dropdown list with the given property.
   *
   * @param property
   * @param text
   * @param index
   */
  public void selectDropdownList(Property[] property, String text, int index);

  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param item        - the option name in the dropdown list. do nothing if item is empty.
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   */
  public void selectDropdownList(String propertyKey, Object value, String item, boolean forced);

  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param item        - the option name in the dropdown list. do nothing if item is empty.
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top         - the top level GuiTestObject searching from
   */
  public void selectDropdownList(String propertyKey, Object value, String item, boolean forced, IHtmlObject top);

  /**
   * Select the <param> item in the dropdown list with the given property.
   * when dropdown list doesn't exist, do nothing.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param item         - the option name in the dropdown list. do nothing if item is empty.
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, String item);

  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param item         - the option name in the dropdown list. do nothing if item is empty.
   * @param forced       - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, String item, boolean forced);

  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param item         - the option name in the dropdown list. do nothing if item is empty.
   * @param forced       - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top          - the top level GuiTestObject search from
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, String item, boolean forced, IHtmlObject top);

  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param property - array of properties except property ".class"
   * @param item     - the option name in the dropdown list. do nothing if item is empty.
   * @param forced   - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   */
  public void selectDropdownList(Property[] property, String item, boolean forced);

  /**
   * Select the <index> item in the dropdown list with the given property.
   *
   * @param property
   * @param index
   */
  public void selectDropdownList(Property[] property, int index);

  /**
   * Select the item in the dropdown list with the given property.
   *
   * @param property
   * @param item
   */
  public void selectDropdownList(Property[] property, String item);


  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param property - array of properties except property ".class"
   * @param item     - the option name in the dropdown list. do nothing if item is empty.
   * @param forced   - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top      - the top level GuiTestObject search from
   */
  public void selectDropdownList(Property[] property, String item, boolean forced, IHtmlObject top);

  /**
   * Select the <param> item in the dropdown list with the given property.
   *
   * @param property    - array of properties except property ".class"
   * @param item        - the option name in the dropdown list. do nothing if item is empty.
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param objectIndex - the index of the test object in the Dropdown list test object array
   * @param top         - the top level GuiTestObject search from
   */
  public void selectDropdownList(Property[] property, String item, boolean forced, int objectIndex, IHtmlObject top);

  /**
   * Select the drop down list with the corresponding item which has the same index in IHtmlObject array and item array
   *
   * @param propertyKey
   * @param value
   * @param item
   * @param forced
   */
  public void selectDropdownList(String propertyKey, Object value, String[] item, boolean forced);

  /**
   * Select the drop down list with the corresponding item which has the same index in IHtmlObject array and item array
   *
   * @param propertyKey1
   * @param value1
   * @param propertyKey2
   * @param value2
   * @param item
   * @param forced
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, String[] item, boolean forced);

  /**
   * Select the drop down list with the corresponding item which has the same index in IHtmlObject array and item array
   *
   * @param property - object properies array
   * @param item     - array of items
   * @param forced   - throw exception if drop down list not found
   */
  public void selectDropdownList(Property[] property, String[] item, boolean forced);

  /**
   * Select the drop down list with the corresponding item which has the same index in IHtmlObject array and item array
   *
   * @param property - object properies array
   * @param item     - array of items
   * @param forced   - throw exception if drop down list not found
   * @param top      - the top level GuiTestObject search from
   */
  public void selectDropdownList(Property[] property, String[] item, boolean forced, IHtmlObject top);

  /**
   * Select multiple items from the first dropdown list found matching the given property if multipleSelection is true.
   * Otherwise, select corresponding single item which has the same index in IHtmlObject array and item array
   *
   * @param property
   * @param item
   * @param multipleSelection
   * @param forced
   * @param top
   */
  public void selectDropdownList(Property[] property, String[] item, boolean multipleSelection, boolean forced, IHtmlObject top);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   * when dropdown list doesn't exist, do nothing.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param index       - the index of option in the dropdown list
   */
  public void selectDropdownList(String propertyKey, Object value, int index);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   * when dropdown list doesn't exist, do nothing.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param index       - the index of option in the dropdown list
   * @param objectIndex - the index of text object if more than one drop down lists available
   */
  public void selectDropdownList(String propertyKey, Object value, int index, int objectIndex);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param index       - the index of option in the dropdown list
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   */
  public void selectDropdownList(String propertyKey, Object value, int index, boolean forced);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param index       - the index of option in the dropdown list
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top         - the top level GuiTestObject starting the search from
   */
  public void selectDropdownList(String propertyKey, Object value, int index, boolean forced, IHtmlObject top);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param propertyKey - the property key
   * @param value       - the property value
   * @param index       - the index of option in the dropdown list
   * @param objectIndex - the index of test object if more than one drop down list available
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top         - the top level GuiTestObject starting the search from
   */
  public void selectDropdownList(String propertyKey, Object value, int index, int objectIndex, boolean forced, IHtmlObject top);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   * when dropdown list doesn't exist, do nothing.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param index        - the index of option in the dropdown list
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, int index);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param index        - the index of option in the dropdown list
   * @param forced       - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, boolean forced);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param index        - the index of option in the dropdown list
   * @param forced       - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top          - the top level GuiTestObject starting the search from
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, boolean forced, IHtmlObject top);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param propertyKey1 - the 1st property key
   * @param value1       - the 1st property value
   * @param propertyKey2 - the 2nd property key
   * @param value2       - the 2nd property value
   * @param index        - the index of option in the dropdown list
   * @param objectIndex  - the index of test object if more than one drop down lists found
   * @param forced       - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top          - the top level GuiTestObject starting the search from
   */
  public void selectDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2, int index, int objectIndex, boolean forced, IHtmlObject top);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param property - array of properties except property ".class"
   * @param index    - the index of option in the dropdown list
   * @param forced   - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   */
  public void selectDropdownList(Property[] property, int index, boolean forced);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param property - array of properties except property ".class"
   * @param index    - the index of option in the dropdown list
   * @param forced   - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top      - the top level GuiTestObject starting the search from
   */
  public void selectDropdownList(Property[] property, int index, boolean forced, IHtmlObject top);

  /**
   * Select the <param> index item in the dropdown list with the given property.
   *
   * @param property    - array of properties except property ".class"
   * @param index       - the index of option in the dropdown list
   * @param objectIndex - the index of test object in the test object array
   * @param forced      - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
   * @param top         - the top level GuiTestObject starting the search from
   */
  public void selectDropdownList(Property[] property, int index, int objectIndex, boolean forced, IHtmlObject top);

  /**
   * Check if the dropdown list contains the given option
   *
   * @param propertyKey
   * @param value
   * @param option
   * @return
   */
  public boolean dropdownListContains(String propertyKey, Object value, Object option);

  /**
   * Check if the dropdown list contains the given option
   *
   * @param property
   * @param option
   * @return
   */
  public boolean dropdownListContains(Property[] property, Object option);

  /**
   * Check if the dropdown list contains the given option
   *
   * @param property
   * @param option
   * @param top
   * @return
   */
  public boolean dropdownListContains(Property[] property, Object option, IHtmlObject top);

  /**
   * Check if the dropdown list contains the given option
   *
   * @param property    - the property of the dropdown list
   * @param objectIndex
   * @param option      - the option either String or RegularExpression
   * @param top         - search the dropdown list from
   * @return
   */
  public boolean dropdownListContains(Property[] property, int objectIndex, Object option, IHtmlObject top);

  void dropdownOptionWaitExists(String propertyKey, Object value, Object option);

  void dropdownOptionWaitExists(Property[] property, Object option);

  void dropdownOptionWaitExists(Property[] property, int objectIndex, int timeout, Object option, IHtmlObject top);

  void dropdownOptionWaitExists(Property[] property, int objectIndex, int timeout, int optionTreshold, IHtmlObject top);

  boolean verifySelect(Property[] property, int objectIndex, String option, IHtmlObject top);

}

