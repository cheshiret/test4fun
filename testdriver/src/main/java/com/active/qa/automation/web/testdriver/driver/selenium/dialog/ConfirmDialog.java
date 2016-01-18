package com.active.qa.automation.web.testdriver.driver.selenium.dialog;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.dialog.IConfirmDialog;
import com.sun.jna.WString;

/**
 * Created by tchen on 1/6/2016.
 */
public class ConfirmDialog extends AlertDialog implements IConfirmDialog {

    public ConfirmDialog(String attributes) {
        super(attributes);
    }

    public ConfirmDialog() {
        super();
    }

    @Override
    public void clickCancel() {
        try {
            //"Stay on this page" button has same function as cancel which has INSTANCE:2
            String okButtonProp="[CLASS:Button; INSTANCE:2]";
            dialog.AU3_ControlFocus(new WString(attributes), new WString(""), new WString(okButtonProp));
            dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(okButtonProp), new WString(""),1, 0, 0);
        } catch (Exception e) {
            throw new ActionFailedException(e);
        }
    }

//	public void clickStayOnThisPage() {
//		try {
//			String okButtonProp="[CLASS:Button; TEXT:&Stay on this page]";
//			dialog.AU3_ControlFocus(new WString(attributes), new WString(""), new WString(okButtonProp));
//			dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(okButtonProp), new WString(""),1, 0, 0);
//		} catch (Exception e) {
//			throw new ActionFailedException(e);
//		}
//	}
}
