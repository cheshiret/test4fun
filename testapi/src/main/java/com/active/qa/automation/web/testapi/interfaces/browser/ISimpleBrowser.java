package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.util.KeyInput;

/**
 * Created by tchen on 1/11/2016.
 */
public interface ISimpleBrowser extends ISearch,IClick,IDropdownSelect,ISetText,IToggleSelect, ISynchronize {
    public boolean exists();

    public void waitExists();

    public void waitExists(int timeout);

    public void close();

    public String text();

    public String title();

    public String url();

    public boolean sync();

    public boolean sync(int timeout);

    public void inputKey(KeyInput... keys);

    public IHtmlObject[] getFrame(String idValue);

    //public IHtmlObject getFrame(int idIndex);

    public void catchScreenShot(String fullName);

    public void closeAllBrowsers();

    public String getDriverName();

    public void switchToFrame(int i);

    public void switchToFrameWd(String txt);

    public String getPageSource();
}