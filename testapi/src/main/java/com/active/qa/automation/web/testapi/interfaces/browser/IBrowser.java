package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.dialog.*;
import com.active.qa.automation.web.testapi.pages.DialogPage;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IBrowser  extends ISimpleBrowser {
    public void open(String url);

    public void open();

    public void load(String url);

    public void maximize();

    public void minimize();

    public void back();

    public void forward();

    public void refresh();

    public IBrowser getBrowser(String attributeName, Object value);

    public IBrowser switchBrowser();

    public IBrowser getBrowser(String attributeName, Object value, int timeout);

    //added by tchen
    public String getcurrentHandler();

    public ISimpleBrowser getHTMLDialog();

    public ISimpleBrowser getHTMLDialog(String attributeName, Object value);

    public IAlertDialog getAlertDialog();

    public IConfirmDialog getConfirmDialog();

    public IFileUploadDialog getFileUploadDialog();

    public IFileDownloadDialog getFiledownloadDialog();

    public IPrintDialog getPrintDialog();

    public IAuthenticationDialog getAuthenticationDialog(Object title);

    public int dismissDialogs(int timeout, DialogPage dialog, int limits);

}
