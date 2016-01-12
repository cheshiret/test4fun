package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.dialog.IAuthenticationDialog;
import com.active.qa.automation.web.testapi.util.RegularExpression;

/**
 * Created by tchen on 1/11/2016.
 */
public abstract class AuthenticationDialogPage extends ConfirmDialogPage implements IAuthenticationDialog{
    protected RegularExpression titlePattern;

    public boolean exists() {
        dialog= Browser.getInstance().getAuthenticationDialog(titlePattern);

        return dialog!=null;
    }

    @Override
    public void setPassword(String password) {
        ((IAuthenticationDialog)dialog).setPassword(password);
    }

    @Override
    public void setUserName(String name) {
        ((IAuthenticationDialog)dialog).setUserName(name);
    }

    public void login(String name,String password) {
        this.setUserName(name);
        this.setPassword(password);
        this.clickOK();
    }

}