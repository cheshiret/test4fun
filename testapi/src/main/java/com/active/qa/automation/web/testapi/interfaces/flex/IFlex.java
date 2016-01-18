package com.active.qa.automation.web.testapi.interfaces.flex;

import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IFlex {

  boolean exists();

  void waitExists();

  void waitExists(int timeout);

  boolean checkFlexObjectExists(Property[] properties);

  IFlexObject[] getFlexObject(Property[] properties);

  IFlexObject[] getFlexTextArea(Property[] properties);

  IFlexObject[] getFlexTable(Property[] properties);

  IFlexObject[] getFlexComboBox(Property[] properties);

  IFlexObject[] getFlexDateField(Property[] properties);

//	IFlexObject[] getFlex(Property[]properties);

  void setTextArea(Property[] properties, String text);

  void setTextArea(Property[] properties, int index, String text);

  void setTextArea(Property[] properties, int index, boolean forced, String text);

  void clickFlexObject(Property[] properties);

  void clickFlexObject(Property[] properties, int index);

  void clickFlexObject(Property[] properties, int index, boolean forced);

  void doubleclickFlexObject(Property[] properties);

  void doubleclickFlexObject(Property[] properties, int index);

  void doubleclickFlexObject(Property[] properties, int index, boolean forced);

  void selectComboBox(Property[] properties, String option);

  void selectComboBox(Property[] properties, int index, String option);

  void selectComboBox(Property[] properties, int index, boolean forced, String option);

  void selectComboBoxByIndex(Property[] properties, int optionIndex);

  void selectComboBoxByIndex(Property[] properties, int objIndex, int optionIndex);

  void selectComboBoxByIndex(Property[] properties, int objIndex, boolean forced, int optionIndex);

  void setComboBox(Property[] properties, String text);

  void setComboBox(Property[] properties, int index, String text);

  void setComboBox(Property[] properties, int index, boolean forced, String text);

  String getComboBoxValue(Property[] properties);

  String getComboBoxValue(Property[] properties, int index);

  String getComboBoxValue(Property[] properties, int index, boolean forced);

  List<String> getComboxBoxValues(Property[] properties);

  List<String> getComboxBoxValues(Property[] properties, int index);

  List<String> getComboxBoxValues(Property[] properties, int index, boolean forced);

  void selectCalendar(Property[] properties, String date);

  void selectCalendar(Property[] properties, int index, String date);

  void selectCalendar(Property[] properties, int index, boolean forced, String date);
}
