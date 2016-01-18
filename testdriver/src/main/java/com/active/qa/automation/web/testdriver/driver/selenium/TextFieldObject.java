package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.interfaces.html.IText;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.StringUtil;
import com.active.qa.automation.web.testapi.util.Timer;
import org.jsoup.nodes.Element;
import org.openqa.selenium.Keys;

/**
 * Created by tchen on 1/6/2016.
 */
class TextFieldObject extends HtmlObject implements IText {
  private boolean isChanged = false;

  public TextFieldObject(Element element, String... handler) {
    super(element, handler);
  }

  @Override
  public String getText() {
    if (isChanged) {
      return getWebElement().getAttribute("value");
    } else if (element.tagName().equalsIgnoreCase("TEXTAREA")) {
      return element.text();
    } else {
      return getWebElement().getAttribute("value");
    }
  }

  @Override
  public void setText(String text) {
    this.setText(text, false);
  }

  @Override
  public void setText(String text, boolean isForceToSet) {
    CharSequence[] seq = getCharSeq(text);

    if (seq != null) {
      if (getWebElement().isDisplayed()) {
        if (readOnly()) {
          if (isForceToSet) {
            this.removeReadOnlyAttr(element.id());
            if (seq.length > 1) {
              getWebElement().clear();
              seq = getCharSeq(text);
            }

          } else {
            AutomationLogger.getInstance().warn("Textfield is readonly..");
            return;
          }
        }

        boolean successful = input(seq, text);
        int count = 0;
        while (!successful && count < 5) {
          count++;
          AutomationLogger.getInstance().info("Try againt #" + count);
          seq = getCharSeq(text);
          if (!successful) {
            if (seq.length > 1) {//[Shane]20150120,for UWP new DOB can not clear by send keys
              getWebElement().clear();
            }
          }
          successful = input(seq, text);
        }

        if (!successful) {
          throw new ActionFailedException("Failed to set text '" + text + "'");
        }

      } else {
        AutomationLogger.getInstance().warn("Textfield is not displayed..");

      }
    }
  }

  private CharSequence[] getCharSeq(String text) {
    String currentText = getText();
    CharSequence[] seq = null;
    boolean append = false;
    if (StringUtil.isEmpty(currentText)) {
      append = true;
    } else if (text.startsWith(currentText)) {
      text = text.substring(currentText.length());
      append = true;
    }
    if (!append) {
      seq = new CharSequence[4];
      seq[0] = Keys.END;
      seq[1] = Keys.chord(Keys.CONTROL, "a");
      seq[2] = Keys.BACK_SPACE;
      seq[3] = text;
    } else if (text.length() > 0) {
      seq = new CharSequence[1];
      seq[0] = text;
    }

    return seq;
  }

  private boolean input(CharSequence[] seq, String expected) {
    String maxLength_attr = getAttributeValue("maxlength");
    int maxLength = StringUtil.isEmpty(maxLength_attr) ? -1 : Integer.parseInt(maxLength_attr);

    if (maxLength > 0 && expected.length() > maxLength) {
      expected = expected.substring(0, maxLength);
    }
    focus(); //Vivian[20141209] due to will focus on calendar//Quentin[20150106] will fail during set Ticket type num in Ticket Selection page

    getWebElement().sendKeys(seq);
    isChanged = true;
    int count = expected.length() / 50; //calculate the loop threshold based on the length of input
    if (count == 0) {
      count = 1;
    }

    //sendkeys is asyn, needs to check if input is finished or not
    String value = getText();
    boolean done = inputEqual(value, expected);

    while (!done && count > 0) { //input not finished, try again
      Timer.sleep(200);
      AutomationLogger.getInstance().debug("Input \"" + value + "\" is not expected \"" + expected + "\".  Wait......");
      count--;
      value = getText();

      done = inputEqual(value, expected);
    }

    if (!done) {
      AutomationLogger.getInstance().warn("Input \"" + value + "\" is not expected \"" + expected + "\", It failed to set text \"" + expected + "\" to text field: " + getWebElement().getAttribute("outerHTML"));
    }

    return done;
  }

  private boolean inputEqual(String actual, String expect) {
    boolean equal = false;
    if (getType().equalsIgnoreCase("file")) {
      java.io.File file = new java.io.File(expect);
      return actual.equalsIgnoreCase("c:\\fakepath\\" + file.getName());
    }
    if (expect.matches("\\d+")) {
      try { //handle scenarios of leading 0s and tail .00 in actual value
        equal = Long.parseLong(actual.replaceAll("\\.0+$", "")) == Long.parseLong(expect);
      } catch (Exception e) {
      }
//		} else if(expect.endsWith(" ")){
//			equal=actual.equals(expect);
    } else if (StringUtil.isEmpty(expect)) {
      equal = StringUtil.isEmpty(actual);
    } else {
      equal = actual.startsWith(expect);
    }

    return equal;
  }

  @Override
  public void clear() {
    getWebElement().clear();
  }

  @Override
  public boolean readOnly() {
    try {
      return element.hasAttr("readOnly") || !getWebElement().isEnabled();
    } catch (Exception e) {
      throw new ActionFailedException(e);
    }

  }

  @Override
  public void setText(int text) {
    setText(Integer.toString(text), false);

  }

  @Override
  public void setText(double text) {
    setText(Double.toString(text), false);
  }

  @Override
  public void setText(String text, Event... eventCodes) {
    this.setText(text, false, eventCodes);
  }

  @Override
  public void setText(String text, boolean isForceToSet, Event... eventCodes) {
    setText(text, isForceToSet);
    for (Event eventCode : eventCodes) {
      switch (eventCode) {
        case ONCHANGE:
          new FireEvent(getWebElement(), "onchange").start();
          javascriptSync();
          break;

        case ONKEYDOWN:
          new FireEvent(getWebElement(), "onkeydown").start();
          javascriptSync();
          break;
        case ENTERKEY:
//					getWebElement().click();
          getWebElement().sendKeys(Keys.ENTER);
          Timer.actionSync();
          break;
        case LOSEFOCUS:
//					getWebElement().click();
          getWebElement().sendKeys(Keys.TAB);
          Timer.actionSync();
          break;
        case FOCUS:
          getWebElement().click();
          Timer.actionSync();
          break;
        default:
          throw new ItemNotFoundException("Unknown event type " + eventCode);

      }

    }
  }

  protected void removeReadOnlyAttr(String id) {
    String script = "var setDate=document.getElementById(\"" + id + "\"); setDate.removeAttribute('readonly');";

    browser.executeJavascript(script, null);
  }

  @Override
  public String getType() {
    return element.attr("type");
  }
}

