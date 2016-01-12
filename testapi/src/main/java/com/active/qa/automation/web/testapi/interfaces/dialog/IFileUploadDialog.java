package com.active.qa.automation.web.testapi.interfaces.dialog;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IFileUploadDialog extends IDialog {
    public void clickOpen();

    public void clickCancel();

    public void clickClose();

    public void setFileName(String fullPathAndFileName);

    public void selectFilesOfType(String type);

    public void chooseAndOpenFile(String fullPathAndFileName);
}
