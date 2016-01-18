package com.active.qa.automation.web.testapi.interfaces;

/**
 * A GuiObject provides generic access to objects in the software under test.
 * Created by tchen on 1/11/2016.
 */
public interface IGuiObject {
  public void click();

  public void doubleClick();

  public boolean exists();

  public String getProperty(String name);

  public boolean isEnabled();

  public boolean isVisible();

  public void unregister();

  public void hover();

  public void rightClick();
}

