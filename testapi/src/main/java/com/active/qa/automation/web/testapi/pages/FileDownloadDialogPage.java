package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.dialog.IFileDownloadDialog;

/**
 * Created by tchen on 1/11/2016.
 */
public class FileDownloadDialogPage extends DialogPage implements
    IFileDownloadDialog {
  private static FileDownloadDialogPage _instance = null;
  private boolean dismissable;
  int time = TestApiConstants.FILE_DIALOG_LONG_SLEEP;

  public static FileDownloadDialogPage getInstance() {
    if (null == _instance) {
      _instance = new FileDownloadDialogPage();
    }

    return _instance;
  }

  protected FileDownloadDialogPage() {
    super();
    dismissable = true;
  }

  @Override
  public boolean exists() {
    dialog = Browser.getInstance().getFiledownloadDialog();
    boolean exists = dialog != null;
    return exists;
  }

  @Override
  public void clickCancel() {
    ((IFileDownloadDialog) dialog).clickCancel();
  }

  @Override
  public void clickOpen() {
    ((IFileDownloadDialog) dialog).clickOpen();
  }

  @Override
  public void clickClose() {
    ((IFileDownloadDialog) dialog).clickClose();
  }

  @Override
  public void closeThisDialogBoxWhenDownloadCompletes(boolean close) {
    ((IFileDownloadDialog) dialog)
        .closeThisDialogBoxWhenDownloadCompletes(close);
  }

  @Override
  public void save(String fullPathAndFileName) {
    ((IFileDownloadDialog) dialog).save(fullPathAndFileName);
  }

  @Override
  public void waitUntilDownloadComplete() {
    ((IFileDownloadDialog) dialog).waitUntilDownloadComplete();
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

  public void downloadSaveFile(String pathName) {
    this.setDismissible(false);
    this.setBeforePageLoading(false);
    Browser.getInstance().waitExists(time, this);
    this.save(pathName);
  }

}
