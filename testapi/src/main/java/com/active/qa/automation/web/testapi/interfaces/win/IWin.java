package com.active.qa.automation.web.testapi.interfaces.win;

import com.active.qa.automation.web.testapi.pages.Page;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IWin {

  boolean exists();

  void waitExists();

  void waitExists(int timeout);

  boolean checkWinObjectExists(Property[] properties);

  boolean checkWinObjectExists(List<Property[]> properties);

  boolean checkWinObjectDisplayed(Property[] properties);

  boolean checkWinObjectDisplayed(List<Property[]> properties);

  IWinObject[] getWinObject(Property[] properties);

  IWinObject[] getWinObject(List<Property[]> properties);

  IWinObject[] getTextBox(Property[] properties);

  IWinObject[] getTextBox(List<Property[]> properties);

  IWinObject[] getDateTimePicker(Property[] properties);

  IWinObject[] getDateTimePicker(List<Property[]> properties);

  IWinObject[] getDataGrid(Property[] properties);

  IWinObject[] getDataGrid(List<Property[]> properties);

  IWinObject[] getComboBox(Property[] properties);

  IWinObject[] getComboBox(List<Property[]> properties);

  void setTextBox(Property[] properties, String text);

  void setTextBox(Property[] properties, int index, String text);

  void setTextBox(Property[] properties, int index, boolean forced, String text);

  void setTextBox(List<Property[]> properties, String text);

  void setTextBox(List<Property[]> properties, int index, String text);

  void setTextBox(List<Property[]> properties, int index, boolean forced, String text);

  String getTextBoxValue(Property[] properties);

  String getTextBoxValue(Property[] properties, int index);

  String getTextBoxValue(Property[] properties, int index, boolean forced);

  String getTextBoxValue(List<Property[]> properties);

  String getTextBoxValue(List<Property[]> properties, int index);

  String getTextBoxValue(List<Property[]> properties, int index, boolean forced);

  void clickWinObject(Property[] properties);

  void clickWinObject(Property[] properties, int index);

  void clickWinObject(Property[] properties, int index, boolean forced);

  void clickWinObject(List<Property[]> properties);

  void clickWinObject(List<Property[]> properties, int index);

  void clickWinObject(List<Property[]> properties, int index, boolean forced);

  void doubleclickWinObject(Property[] properties);

  void doubleclickWinObject(Property[] properties, int index);

  void doubleclickWinObject(Property[] properties, int index, boolean forced);

  void doubleclickWinObject(List<Property[]> properties);

  void doubleclickWinObject(List<Property[]> properties, int index);

  void doubleclickWinObject(List<Property[]> properties, int index, boolean forced);

  void setDateTimePicker(Property[] properties, String date);

  void setDateTimePicker(Property[] properties, int index, String date);

  void setDateTimePicker(Property[] properties, int index, boolean forced, String date);

  void setDateTimePicker(List<Property[]> properties, String date);

  void setDateTimePicker(List<Property[]> properties, int index, String date);

  void setDateTimePicker(List<Property[]> properties, int index, boolean forced, String date);

  String getDateTimePickerValue(Property[] properties);

  String getDateTimePickerValue(Property[] properties, int index);

  String getDateTimePickerValue(Property[] properties, int index, boolean forced);

  String getDateTimePickerValue(List<Property[]> properties);

  String getDateTimePickerValue(List<Property[]> properties, int index);

  String getDateTimePickerValue(List<Property[]> properties, int index, boolean forced);

  void selectDropDownlist(Property[] properties, String item);

  void selectDropDownlist(List<Property[]> properties, String item);

  void start(String path, String processName);

  void close();

  Object waitExists(int timeout, Page[] pages);
}

