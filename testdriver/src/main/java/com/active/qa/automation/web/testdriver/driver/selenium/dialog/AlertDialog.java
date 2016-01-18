package com.active.qa.automation.web.testdriver.driver.selenium.dialog;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.dialog.IAlertDialog;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.StringUtil;
import com.sun.jna.WString;

/**
 * Created by tchen on 1/6/2016.
 */
public class AlertDialog extends Dialog implements IAlertDialog {

    public AlertDialog(String description) {
        super(description);
    }

    public AlertDialog() {
//		super("[TITLE:"+(SysInfo.getIEVersion() >= 8 ?"Message from webpage":"Windows Internet Explorer")+";CLASS:#32770]");
        //james[20130708]: changes to use regular expression for Title as the title can be either "Message from webpage" or "Windows Internet Explorer"
        //Title "Message from webpage" for javascript alert and confirmation dialog
        //Title "Windows Internet Explorer" for dialogs come from IE browser.
        super("[REGEXPTITLE:^(Message from webpage|Windows Internet Explorer|-- Webpage Dialog)$;CLASS:#32770]");
    }

    @Override
    public void clickOK() {
        try {
            //"Leave this page" will function as OK button which always has INSTANCE:1
            String okButtonProp="[CLASS:Button; INSTANCE:1]";
            dialog.AU3_WinActivate(new WString(attributes), new WString(""));
            dialog.AU3_ControlFocus(new WString(attributes), new WString(""), new WString(okButtonProp));
            dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(okButtonProp), new WString(""),1, 0, 0);
            if(this.exists()) {
                AutomationLogger.getInstance().warn("Dialog still exists. Try 2nd click on OK button");
                dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(okButtonProp), new WString(""),1, 0, 0);
            }
        } catch (Exception e) {
            throw new ActionFailedException(e);
        }
    }

//	public void clickLeaveThisPage() {
//		try {
//			String okButtonProp="[CLASS:Button; TEXT:&Leave this page]";
//			dialog.AU3_WinActivate(new WString(attributes), new WString(""));
//			dialog.AU3_ControlFocus(new WString(attributes), new WString(""), new WString(okButtonProp));
//			dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(okButtonProp), new WString(""),1, 0, 0);
//			if(this.exists()) {
//				AutomationLogger.getInstance().warn("Dialog still exists. Try 2nd click on Leave this page button");
//				dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(okButtonProp), new WString(""),1, 0, 0);
//			}
//		} catch (Exception e) {
//			throw new ActionFailedException(e);
//		}
//	}

    public String getDialogMessage() {
        String messageProp="[CLASS:Static; ID:65535]";
        byte[] buf=new byte[1024];//512
        dialog.AU3_ControlGetText(new WString(attributes), new WString(""), new WString(messageProp), buf, 512);//256
        return StringUtil.autoitxBytesToString(buf);
    }

}

