package com.active.qa.automation.web.testapi.interfaces.dialog;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IAlertDialog extends IDialog {
    void clickOK();
    //	void clickLeaveThisPage();
    String getDialogMessage();
}