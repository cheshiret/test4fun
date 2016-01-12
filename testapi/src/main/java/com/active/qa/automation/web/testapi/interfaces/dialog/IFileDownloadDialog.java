package com.active.qa.automation.web.testapi.interfaces.dialog;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IFileDownloadDialog extends IDialog {
    public void clickOpen();

    public void clickCancel();

    public void clickClose();

    public void save(String fullPathAndFileName);

    public void closeThisDialogBoxWhenDownloadCompletes(boolean close);

    public void waitUntilDownloadComplete();
}
