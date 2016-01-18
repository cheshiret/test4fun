package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.exception.NotSupportedException;

/**
 * Created by tchen on 1/11/2016.
 */
public class AutoItUtil {
  public static String transferKeys(KeyInput[] keys) {
    StringBuffer sb = new StringBuffer();
    for (KeyInput k : keys) {
      sb.append(transferKeys(k));
    }

    return sb.toString();
  }

  public static String transferKeys(KeyInput key) {
    switch (key.getType()) {
      case 100:
        return key.getText();
      case 101:
        return transferKey(key.getNonTextKey());
      case 102:
        StringBuffer sb = new StringBuffer();
        int keyCode = key.getNonTextKey();
        String text = key.getText();
        if (keyCode == KeyInput.SHIFT) {
          sb.append("{SHIFTDOWN}");
          sb.append(text);
          sb.append("{SHIFTUP}");
        } else if (keyCode == KeyInput.CTRL) {
          sb.append("{CTRLDOWN}");
          sb.append(text);
          sb.append("{CTRLUP}");
        } else if (keyCode == KeyInput.ALT) {
          sb.append("{ALTDOWN}");
          sb.append(text);
          sb.append("{ALTTUP}");
        }
        return sb.toString();
      default:
        throw new NotSupportedException("Keys type " + key.getType() + " is not supported.");
    }
  }

  static String transferKey(int keyCode) {
    switch (keyCode) {
      case com.active.qa.automation.web.testapi.util.KeyInput.BACKSPACE:
        return "{BACKSPACE}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ENTER:
        return "{ENTER}";
      case com.active.qa.automation.web.testapi.util.KeyInput.SHIFT:
        return "{RSHIFT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.LEFT_SHIFT:
        return "{LSHIFT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.CTRL:
        return "{RCTRL}";
      case com.active.qa.automation.web.testapi.util.KeyInput.LEFT_CTRL:
        return "{LCTRL}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ALT:
        return "{RALT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.LEFT_ALT:
        return "{LALT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.TAB:
        return "{TAB}";
      case com.active.qa.automation.web.testapi.util.KeyInput.INSERT:
        return "{INSERT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.DELETE:
        return "{DELETE}";
      case com.active.qa.automation.web.testapi.util.KeyInput.HOME:
        return "{HOME}";
      case com.active.qa.automation.web.testapi.util.KeyInput.END:
        return "{END}";
      case com.active.qa.automation.web.testapi.util.KeyInput.PAGE_UP:
        return "{PGUP}";
      case com.active.qa.automation.web.testapi.util.KeyInput.PAGE_DOWN:
        return "{PGDN}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_UP:
        return "{UP}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_DOWN:
        return "{DOWN}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_LEFT:
        return "{LEFT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_RIGHT:
        return "{RIGHT}";
      case com.active.qa.automation.web.testapi.util.KeyInput.ESC:
        return "{ESC}";
      default:
        throw new NotSupportedException("Key code " + keyCode + " is not supported.");
    }
  }
}

