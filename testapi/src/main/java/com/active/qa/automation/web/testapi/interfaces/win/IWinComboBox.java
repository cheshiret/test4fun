package com.active.qa.automation.web.testapi.interfaces.win;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IWinComboBox {
    public void select(int index);
    public void select(String item);
    public String getSelectedText();
}

