package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.exception.NotSupportedException;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.KeyInput;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Keys;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchen on 1/6/2016.
 */
public class RuntimeUtil {
  static int PAGElOADING_WAIT = 200;

  static String parsePropertyValue(Object value) {
    String text = "";
    if (value instanceof RegularExpression) {
      text = ((RegularExpression) value).getPattern();
      if (text.startsWith("^") && !text.startsWith("^\\s*")) {
        text = "^\\s*" + text.substring(1);
      }
      text = "/" + text + "/";
      if (!((RegularExpression) value).isCaseSensitive()) {
        text += "i";
      }
    } else if (value instanceof String) {
      text = (String) value;
    } else {
      throw new ItemNotFoundException("Value can only be either RegularExpression or String");
    }

    return text;
  }

  //private methods
  static String innerText(Element element) {
    StringBuffer sBuf = new StringBuffer();
    decendantInnerText(element, sBuf);
    return sBuf.toString().trim();
  }

  private static void decendantInnerText(Node node, StringBuffer sBuf) {
    NodeList nodeList = node.getChildNodes();
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++) {
      Node child = nodeList.item(i);
      if (child != null && child.getNodeType() == Node.TEXT_NODE) {
        sBuf.append(child.getNodeValue());
      }
      decendantInnerText(child, sBuf);
    }
  }

  static String trimDot(String text) {
    if (text.startsWith(".")) {
      return text.substring(1);
    }

    return text;
  }

  static boolean matchOrEqual(String text, Object value) {
    if (value instanceof String) {
      return ((String) value).equalsIgnoreCase(text);
    } else {
      return ((RegularExpression) value).match(text);
    }
  }

  static void sleep(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (Exception e) {
    }
  }

  static CharSequence[] transferKeysForSelenium(com.active.qa.automation.web.testapi.util.KeyInput... keys) {
    List<CharSequence> seq = new ArrayList<CharSequence>();

    for (com.active.qa.automation.web.testapi.util.KeyInput k : keys) {

      seq.add(getKeySequence(k));
    }

    return seq.toArray(new CharSequence[0]);
  }

  static CharSequence getKeySequence(KeyInput k) {
    switch (k.getType()) {
      case 100:
        return k.getText();
      case 101:
        return getNonTextKey(k.getNonTextKey());
      case 102:
        List<CharSequence> seq = new ArrayList<CharSequence>();
        seq.add(getNonTextKey(k.getNonTextKey()));
        seq.add(k.getText());
        return Keys.chord(seq.toArray(new CharSequence[0]));
      default:
        throw new NotSupportedException("Keys type " + k.getType() + " is not supported.");
    }
  }

  static CharSequence getNonTextKey(int keyCode) {
    switch (keyCode) {
      case com.active.qa.automation.web.testapi.util.KeyInput.BACKSPACE:
        return Keys.BACK_SPACE;
      case com.active.qa.automation.web.testapi.util.KeyInput.ENTER:
        return Keys.ENTER;
      case com.active.qa.automation.web.testapi.util.KeyInput.SHIFT:
        return Keys.SHIFT;
      case com.active.qa.automation.web.testapi.util.KeyInput.LEFT_SHIFT:
        return Keys.LEFT_SHIFT;
      case com.active.qa.automation.web.testapi.util.KeyInput.CTRL:
        return Keys.CONTROL;
      case com.active.qa.automation.web.testapi.util.KeyInput.LEFT_CTRL:
        return Keys.LEFT_CONTROL;
      case com.active.qa.automation.web.testapi.util.KeyInput.ALT:
        return Keys.ALT;
      case com.active.qa.automation.web.testapi.util.KeyInput.LEFT_ALT:
        return Keys.LEFT_ALT;
      case com.active.qa.automation.web.testapi.util.KeyInput.TAB:
        return Keys.TAB;
      case com.active.qa.automation.web.testapi.util.KeyInput.INSERT:
        return Keys.INSERT;
      case com.active.qa.automation.web.testapi.util.KeyInput.DELETE:
        return Keys.DELETE;
      case com.active.qa.automation.web.testapi.util.KeyInput.HOME:
        return Keys.HOME;
      case com.active.qa.automation.web.testapi.util.KeyInput.END:
        return Keys.END;
      case com.active.qa.automation.web.testapi.util.KeyInput.PAGE_UP:
        return Keys.PAGE_UP;
      case com.active.qa.automation.web.testapi.util.KeyInput.PAGE_DOWN:
        return Keys.PAGE_DOWN;
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_UP:
        return Keys.ARROW_UP;
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_DOWN:
        return Keys.ARROW_DOWN;
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_LEFT:
        return Keys.ARROW_LEFT;
      case com.active.qa.automation.web.testapi.util.KeyInput.ARROW_RIGHT:
        return Keys.ARROW_RIGHT;
      case com.active.qa.automation.web.testapi.util.KeyInput.ESC:
        return Keys.ESCAPE;
      default:
        throw new NotSupportedException("Key code " + keyCode + " is not supported.");
    }
  }

//    private int getCssSize(String css) {
//		String value=getWebElement().getCssValue(css);
//		return (int)Double.parseDouble(value.replaceAll("px", ""));
//	}
//
//	private int getLineHeight() {
//		String line_height=getWebElement().getCssValue("line-height");
//		int font_size=getCssSize("font-size");
//
//		if(line_height.equalsIgnoreCase("normal")) {
//			return font_size*12/10;
//		} else if(line_height.endsWith("%")) {
//			int value=Integer.parseInt(line_height.replaceAll("%", ""));
//			return font_size*value/100;
//		} else if(line_height.endsWith("px")){
//			return (int) Double.parseDouble(line_height.replaceAll("px", ""));
//		} else if (line_height.matches("^\\d(\\.\\d+)?$")){
//			double value=Double.parseDouble(line_height);
//			return (int) (font_size*value);
//		} else { //treat as normal
//			return font_size*12/10;
//		}
//	}
//
//	public int calculateIfLinkSpreadIntoMultipleLines() {
//		WebElement e=getWebElement();
//		int resolution=java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
//
//		int line_height=getLineHeight();
//		int padding_top=getCssSize("padding-top");
//		int padding_bottom=getCssSize("padding-bottom");
//		int border_top=getCssSize("border-top-width");
//		int border_bottom=getCssSize("border-bottom-width");
//
//		int h=Integer.parseInt(e.getAttribute("offsetHeight"));
//		int ratio=(h-padding_top-padding_bottom-border_top-border_bottom)/line_height;
//
//		return ratio;
//	}

  public static void cleanupTestDriver() {
    String temp = System.getProperty("java.io.tmpdir");
    File dir = new File(temp);
    String[] files = dir.list();

    for (String folder : files) {

      if (folder.matches("webdriver\\d+libs")) {
        try {
          FileUtils.deleteDirectory(new File(temp + folder));
        } catch (IOException e) {
          AutomationLogger.getInstance().warn("Failed to clean up " + folder);
        }
      }
    }
  }

}
