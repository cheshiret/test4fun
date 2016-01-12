package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.dialog.IConfirmDialog;

/**
 * Created by tchen on 1/11/2016.
 */
public class ConfirmDialogPage extends AlertDialogPage {
    private static ConfirmDialogPage _instance=null;
    public static final boolean OK=true;
    public static final boolean CANCEL=false;
    private boolean dismissType;

    protected ConfirmDialogPage() {
        dismissType=OK;
    }

    public static ConfirmDialogPage getInstance() {
        if(null==_instance) {
            _instance=new ConfirmDialogPage();
        }

        return _instance;
    }

    public boolean exists() {
        dialog= Browser.getInstance().getConfirmDialog();
        return dialog!=null;
    }

    public void clickCancel() {
        ((IConfirmDialog)dialog).clickCancel();
    }

    public void clickStayOnThisPage() {
        ((IConfirmDialog)dialog).clickCancel();
    }

    public void setDismissMethod(boolean type) {
        dismissType=type;
    }


    public void dismiss(){
        if(dismissType==OK) {
            this.clickOK();
        } else if(dismissType==CANCEL) {
            this.clickCancel();
        }

        if(this.exists()) {
            this.quit();
        }
    }

    public String text(){
        return ((IConfirmDialog)dialog).getDialogMessage();
    }
}
