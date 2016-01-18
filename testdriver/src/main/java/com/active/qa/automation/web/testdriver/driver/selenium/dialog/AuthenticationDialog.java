package com.active.qa.automation.web.testdriver.driver.selenium.dialog;

import com.active.qa.automation.web.testapi.interfaces.dialog.IAuthenticationDialog;

/**
 * Created by tchen on 1/6/2016.
 */
public class AuthenticationDialog extends ConfirmDialog implements IAuthenticationDialog {

  public AuthenticationDialog(String attributes) {
    super(attributes);
  }

  public AuthenticationDialog() {
    this("[TITLE:Internet Explore; CLASS:#32770]");
  }


  @Override
  public void setUserName(String name) {
    this.setTextField(10508, name);
  }

  @Override
  public void setPassword(String password) {
    this.setTextField(10513, password);
  }

}
