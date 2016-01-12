package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.dialog.IFileUploadDialog;

/**
 * Created by tchen on 1/11/2016.
 */
public class FileUploadDialogPage extends DialogPage implements IFileUploadDialog {

    private boolean dismissable;

    private static FileUploadDialogPage _instance = null;

    protected FileUploadDialogPage() {
        super();
        dismissable = true;
    }

    public static FileUploadDialogPage getInstance() {
        if(_instance == null) {
            _instance = new FileUploadDialogPage();
        }

        return _instance;
    }

    @Override
    public boolean exists() {
        dialog = Browser.getInstance().getFileUploadDialog();
        boolean exists = dialog != null;

        return exists;
    }

    @Override
    public void clickOpen() {
        ((IFileUploadDialog)dialog).clickOpen();
    }

    @Override
    public void clickCancel() {
        ((IFileUploadDialog)dialog).clickCancel();
    }

    @Override
    public void clickClose() {

    }

    @Override
    public void setFileName(String fullPathAndFileName) {
        ((IFileUploadDialog)dialog).setFileName(fullPathAndFileName);
    }

    public void setDismissible(boolean choice) {
        dismissable = choice;
    }

    @Override
    public boolean dismissible() {
        return dismissable;
    }

    @Override
    public void dismiss() {
        this.clickCancel();
    }

    @Override
    public void selectFilesOfType(String type) {

    }

    @Override
    public void chooseAndOpenFile(String fullPathAndFileName) {
        ((IFileUploadDialog)dialog).chooseAndOpenFile(fullPathAndFileName);
    }

    public void uploadFile(String fullPathAndFileName) {
        this.setDismissible(false);
        this.setBeforePageLoading(false);
        Browser.getInstance().waitExists(this);
        this.chooseAndOpenFile(fullPathAndFileName);
    }
}

