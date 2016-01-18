package com.active.qa.automation.web.testapi.interfaces.flex;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.exception.NotInitializedException;
import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public abstract class Flex implements IFlex {
  protected static Flex _instance = null;

  public static Flex getInstance() {
    if (_instance == null) {
      throw new NotInitializedException("Flex is not initialized.");
    }

    return _instance;
  }

  public static void unregister(IFlexObject... objs) {
    if (objs != null) {
      for (int i = 0; i < objs.length; i++) {
        objs[i].unregister();
      }
    }
  }

  public static void unregister(IFlexObject[]... objs) {
    if (objs != null) {
      for (int i = 0; i < objs.length; i++) {
        unregister(objs[i]);
      }
    }
  }

  @Override
  public void waitExists() {
    waitExists(TestApiConstants.SLEEP);
  }

  @Override
  public void waitExists(int timeout) {
    boolean exists = false;
    exists = exists();
    int count = timeout;
    while (!exists && count > 0) {
      count--;
      Browser.sleep(1);
      exists = exists();
    }

    if (!exists) {
      throw new ItemNotFoundException("Flex application is not found in " + timeout + " seconds.");
    }
  }

  @Override
  public boolean checkFlexObjectExists(Property[] properties) {
    IFlexObject[] objs = getFlexObject(properties);
    boolean exists = objs != null && objs.length > 0;
    unregister(objs);
    return exists;
  }

  @Override
  public IFlexObject[] getFlexTextArea(Property[] properties) {
    return getFlexObject(Property.addToPropertyArray(properties, ".class", "FlexTextArea"));
  }

  @Override
  public IFlexObject[] getFlexTable(Property[] properties) {
    return getFlexObject(Property.addToPropertyArray(properties, ".class", "FlexDataGrid"));
  }

  @Override
  public IFlexObject[] getFlexComboBox(Property[] properties) {
    return getFlexObject(Property.addToPropertyArray(properties, ".class", "FlexComboBox"));
  }

  @Override
  public IFlexObject[] getFlexDateField(Property[] properties) {
    return getFlexObject(Property.addToPropertyArray(properties, ".class", "FlexDateField"));
  }

  @Override
  public void setTextArea(Property[] properties, String text) {
    setTextArea(properties, 0, text);
  }

  @Override
  public void setTextArea(Property[] properties, int index, String text) {
    setTextArea(properties, index, false, text);

  }

  @Override
  public void setTextArea(Property[] properties, int index, boolean forced, String text) {
    IFlexObject[] objs = getFlexTextArea(properties);

    if (objs.length > index) {
      ((IFlexTextArea) objs[0]).setText(text);
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find Text Area with property: " + Property.propertyArrayToString(properties));
    }
  }

  @Override
  public void clickFlexObject(Property[] properties) {
    clickFlexObject(properties, 0);

  }

  @Override
  public void clickFlexObject(Property[] properties, int index) {
    clickFlexObject(properties, index, false);

  }

  @Override
  public void clickFlexObject(Property[] properties, int index, boolean forced) {
    IFlexObject[] objs = getFlexObject(properties);

    if (objs.length > index) {
      objs[0].click();
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find Flex object with property: " + Property.propertyArrayToString(properties));
    }

  }

  @Override
  public void doubleclickFlexObject(Property[] properties) {
    doubleclickFlexObject(properties, 0);
  }

  @Override
  public void doubleclickFlexObject(Property[] properties, int index) {
    doubleclickFlexObject(properties, index, false);
  }

  @Override
  public void doubleclickFlexObject(Property[] properties, int index, boolean forced) {
    IFlexObject[] objs = getFlexObject(properties);

    if (objs.length > index) {
      objs[0].doubleClick();
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find Flex object with property: " + Property.propertyArrayToString(properties));
    }
  }

  @Override
  public void selectComboBox(Property[] properties, String option) {
    selectComboBox(properties, 0, option);
  }

  @Override
  public void selectComboBox(Property[] properties, int index, String option) {
    selectComboBox(properties, index, false, option);

  }

  @Override
  public void selectComboBox(Property[] properties, int index, boolean forced, String option) {
    IFlexObject[] objs = getFlexComboBox(properties);

    if (objs.length > index) {
      ((IFlexComboBox) objs[0]).select(option);
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find ComboBox with property: " + Property.propertyArrayToString(properties));
    }

  }

  @Override
  public void selectComboBoxByIndex(Property[] properties, int optionIndex) {
    selectComboBoxByIndex(properties, 0, optionIndex);
  }

  @Override
  public void selectComboBoxByIndex(Property[] properties, int objIndex, int optionIndex) {
    selectComboBoxByIndex(properties, objIndex, false, optionIndex);
  }

  @Override
  public void selectComboBoxByIndex(Property[] properties, int objIndex, boolean forced, int optionIndex) {
    IFlexObject[] objs = getFlexComboBox(properties);

    if (objs.length > objIndex) {
      ((IFlexComboBox) objs[0]).select(optionIndex);
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find ComboBox with property: " + Property.propertyArrayToString(properties));
    }
  }

  @Override
  public void setComboBox(Property[] properties, String text) {
    setComboBox(properties, 0, text);

  }

  @Override
  public void setComboBox(Property[] properties, int index, String text) {
    setComboBox(properties, index, false, text);

  }

  @Override
  public void setComboBox(Property[] properties, int index, boolean forced, String text) {
    IFlexObject[] objs = getFlexComboBox(properties);

    if (objs.length > index) {
      ((IFlexComboBox) objs[0]).setText(text);
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find ComboBox with property: " + Property.propertyArrayToString(properties));
    }

  }

  @Override
  public String getComboBoxValue(Property[] properties) {
    return getComboBoxValue(properties, 0);
  }

  @Override
  public String getComboBoxValue(Property[] properties, int index) {
    return getComboBoxValue(properties, index, false);
  }

  @Override
  public String getComboBoxValue(Property[] properties, int index, boolean forced) {
    IFlexObject[] objs = getFlexComboBox(properties);
    String value = null;

    if (objs.length > index) {
      value = ((IFlexComboBox) objs[0]).getComboBoxValue();
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find ComboBox with property: " + Property.propertyArrayToString(properties));
    }

    return value;
  }

  @Override
  public List<String> getComboxBoxValues(Property[] properties, int index) {
    return getComboxBoxValues(properties, index, false);
  }

  @Override
  public List<String> getComboxBoxValues(Property[] properties) {
    return getComboxBoxValues(properties, 0);
  }

  @Override
  public List<String> getComboxBoxValues(Property[] properties, int index, boolean forced) {
    IFlexObject[] objs = getFlexComboBox(properties);
    List<String> value = null;

    if (objs.length > index) {
      value = ((IFlexComboBox) objs[0]).getDropdownOptions();
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find ComboBox with property: " + Property.propertyArrayToString(properties));
    }

    return value;
  }

  @Override
  public void selectCalendar(Property[] properties, String date) {
    selectCalendar(properties, 0, date);
  }

  @Override
  public void selectCalendar(Property[] properties, int index, String date) {
    selectCalendar(properties, index, false, date);
  }

  @Override
  public void selectCalendar(Property[] properties, int index, boolean forced, String date) {
    IFlexObject[] objs = getFlexDateField(properties);

    if (objs.length > index) {
      ((IFlexDateField) objs[0]).setDate(date);
      unregister(objs);
    } else if (forced) {
      unregister(objs);
      throw new ItemNotFoundException("Failed to find Calendar with property: " + Property.propertyArrayToString(properties));
    }
  }

}

