package com.active.qa.automation.web.testdriver.driver.selenium.dialog;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.dialog.IDialog;
import com.active.qa.automation.web.testapi.util.StringUtil;
import com.sun.jna.WString;
import com.compdev.jautoit.autoitx.AutoitXFactory;
import com.compdev.jautoit.autoitx.Autoitx;
import java.io.IOException;

/**
 * This is a general windows dialog
 * Created by tchen on 1/6/2016.
 */
public class Dialog implements IDialog {
    protected Autoitx dialog;
    protected String attributes;

    public Dialog(String attributes) {
        try {
            this.dialog=AutoitXFactory.getAutoitx();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.attributes=attributes;
    }

    public Dialog() {
        //construct a general dialog with class:#32770
        this("[CLASS:#32770]");
    }

    public boolean exists() {
        return dialog.AU3_WinExists(new WString(attributes), new WString(""))==1;
    }

    @Override
    public void quit() {
        try {
            dialog.AU3_WinClose(new WString(attributes), new WString(""));
        } catch(Exception e) {
            throw new ActionFailedException(e);
        }

    }

    @Override
    public String text() {
        try {
            byte[] buf=new byte[512];
            dialog.AU3_WinGetText(new WString(attributes), new WString(""),buf,256);
            return StringUtil.autoitxBytesToString(buf);
        } catch(Exception e) {
            throw new ActionFailedException(e);
        }
    }

    @Override
    public String title() {
        try {
            byte[] buf=new byte[512];
            dialog.AU3_WinGetTitle(new WString(attributes), new WString(""),buf,256);
            return StringUtil.autoitxBytesToString(buf);
        } catch(Exception e) {
            throw new ActionFailedException(e);
        }
    }

    protected void clickButton(String text) {
        String buttonProp="[CLASS:Button; TEXT:"+text+"]";
        dialog.AU3_WinActivate(new WString(attributes), new WString(""));
        dialog.AU3_ControlFocus(new WString(attributes), new WString(""), new WString(buttonProp));
        dialog.AU3_WinActivate(new WString(attributes), new WString(""));
        dialog.AU3_ControlClick(new WString(attributes), new WString(""), new WString(buttonProp), new WString(""),1, 0, 0);
    }

    public void setTextField(int objectID, String text) {
        String textFieldProp="[CLASS:Edit; ID:"+objectID+"]";
        dialog.AU3_WinActivate(new WString(attributes), new WString(""));
        dialog.AU3_ControlSetText(new WString("attributes"), new WString(""), new WString(textFieldProp),new WString(text));
    }
}

