package com.active.qa.automation.web.testdriver.driver.selenium.dialog;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.dialog.IFileDownloadDialog;
import com.active.qa.automation.web.testapi.util.StringUtil;
import com.active.qa.automation.web.testapi.util.SysInfo;
import com.sun.jna.WString;

/**
 * Created by tchen on 1/6/2016.
 */
public class FileDownloadDialog extends Dialog implements IFileDownloadDialog {

  public FileDownloadDialog(String attributes) {
    super(attributes);
  }

  public FileDownloadDialog() {
    this("[TITLE:File Download; CLASS:#32770]");
  }

  @Override
  public void clickOpen() {
    this.clickButton("Open");
  }

  public void clickSave() {
    this.clickButton("&Save");
  }

  @Override
  public void clickClose() {
    try {
      this.clickButton("Close");
    } catch (Exception e) {
      throw new ActionFailedException(e);
    }

  }

  public void clickOpenFolder() {
    this.clickButton("Open &Folder");
  }

  @Override
  public void clickCancel() {
    this.clickButton("Cancel");
  }

  @Override
  public void save(String fullPathAndFileName) {
    try {
      this.clickSave();
      String saveAsAttribute = "[TITLE:Save As; CLASS:#32770]";
      dialog.AU3_WinWait(new WString(saveAsAttribute), new WString(""), 60);
      SaveAsDialog saveAs = new SaveAsDialog(saveAsAttribute);
      saveAs.setFileName(fullPathAndFileName);
      saveAs.clickSave();
    } catch (Exception e) {
      throw new ActionFailedException(e);
    }
  }

  @Override
  public void closeThisDialogBoxWhenDownloadCompletes(boolean close) {
    String operation = close ? "Check" : "UnCheck";
    byte[] buf = new byte[2];
    dialog.AU3_WinActivate(new WString(attributes), new WString(""));
    dialog.AU3_ControlCommand(new WString(attributes), new WString(""), new WString("[CLASS:Button; TEXT:&Close this dialog box when download completes]"), new WString(operation), new WString(""), buf, 2);
    int result = Integer.parseInt(StringUtil.autoitxBytesToString(buf));
    if (result != 1) {
      throw new ActionFailedException("Failed to " + operation + " checkbox for CloseThisDialogBoxWhenDownloadCompletes");
    }
  }

  @Override
  public void waitUntilDownloadComplete() {
    int timeout = 120; //seconds
    boolean completed = false;
    byte[] buf = new byte[2];
    while (!completed && timeout > 0) {
      dialog.AU3_WinActivate(new WString(attributes), new WString(""));
      dialog.AU3_ControlCommand(new WString(attributes), new WString(""), new WString("[CLASS:Button; TEXT:Open]"), new WString("IsEnabled"), new WString(""), buf, 2);
      timeout--;
      completed = Integer.parseInt(StringUtil.autoitxBytesToString(buf)) == 0;
    }

    if (!completed) {
      throw new ActionFailedException("Timed out: download is not finished withing " + timeout + " seconds.");
    }
  }

  private class SaveAsDialog extends Dialog {

    public SaveAsDialog(String attributes) {
      super(attributes);
    }

    public void setFileName(String fileName) {
      String fileNameFieldProp;
      if (SysInfo.getIEVersion() >= 8) {
        fileNameFieldProp = "[CLASS:Edit; ID:1001]";
      } else {
        fileNameFieldProp = "[CLASS:Edit; ID:1148]";
      }
      dialog.AU3_WinActivate(new WString(attributes), new WString(""));
      dialog.AU3_ControlSetText(new WString(attributes), new WString(""), new WString(fileNameFieldProp), new WString(fileName));
    }

    public void clickSave() {
      this.clickButton("&Save");
    }

    public void clickCancel() {
      this.clickButton("Cancel");
    }

  }
}
