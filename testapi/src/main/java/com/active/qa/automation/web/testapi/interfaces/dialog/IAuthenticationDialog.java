package com.active.qa.automation.web.testapi.interfaces.dialog;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IAuthenticationDialog extends IConfirmDialog {
    void setUserName(String name);
    void setPassword(String password);
}

