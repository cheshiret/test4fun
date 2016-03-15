package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.exception.NotSupportedException;
import com.active.qa.automation.web.testapi.util.Property;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public class PageUtil {

  protected static final int NOTDEFINED = 0;
  protected static final int LINK = 1;
  protected static final int TEXT = 2;
  protected static final int LIST = 3;
  protected static final int CHECKBOX = 4;
  protected static final int RADIO = 5;
  protected static final int TABLE = 6;
  protected static final int FRAME = 7;
  protected static final int BUTTON = 8;
  protected static final int FRAMESET = 9;

  public static String getPageSource(WebDriver driver) {
    return driver.getPageSource();
  }

  public static String[] getUrlText(String pagesource, String location) {
    Document doc = Jsoup.parseBodyFragment(pagesource);
    ArrayList<String> list = new ArrayList<String>();
    Element body = doc.body();
    Elements contents = body.getElementsByClass(location);
    for (Element content : contents) {
      Elements links = content.getElementsByTag("a");
      for (Element link : links) {
//			  String linkHref = link.attr("href");
        String linkText = link.text();
        list.add(linkText);
//			  System.out.println(linkHref+":"+linkText);
      }
    }
    return list.toArray(new String[0]);

  }


  protected Property[] Property(String Parenttag, String tagName, String tag, RegularExpression regExp) {
    return Property.toPropertyArray(Parenttag, tagName, tag, regExp);
  }

  public static String[] getDropdownOptions(String pagesource, String location) {
    Document doc = Jsoup.parseBodyFragment(pagesource);
    ArrayList<String> list = new ArrayList<String>();
    Element body = doc.body();
    Elements contents = body.getElementsByClass(location);
    for (Element content : contents) {
      Elements options = content.getElementsByTag("option");
      for (Element option : options) {
//			  String linkHref = link.attr("href");
        String optionText = option.text();
        list.add(optionText);
//			  System.out.println(linkHref+":"+linkText);
      }
    }
    return list.toArray(new String[0]);

  }


  public String cssSelector(Property[] properties) {
    String tag = "*";
    String text = null;
    String id = null;
    String className = null;
    List<String> attr = new ArrayList<String>();

    for (Property p : properties) {
      String name = p.getPropertyName();
      Object value = p.getPropertyValue();

      if (name.equals(".class")) {
        tag = getTagName((String) value);
        if (tag.equalsIgnoreCase("input")) {
          String type = null;
          try {
            type = ((String) value).substring(11).trim();
          } catch (Exception e) {
          }
          ;
          if (!StringUtil.isEmpty(type)) {
            if (type.equalsIgnoreCase("text"))
              attr.add("[type!=hidden][type!=checkbox][type!=radio]");
            else
              attr.add("[type=" + type + "]");
          }
        }
      } else if (name.equals(".text")) {

        if (value instanceof String) {
          String t = (String) value;
          if (t.contains(TestApiConstants.CELL_DELIMITER)) {
            t = t.replaceAll(TestApiConstants.CELL_DELIMITER, " ");
          }
          text = ":matches(" + StringUtil.convertToRegex(t) + ")";
        } else {
          text = ":matches(" + ((RegularExpression) value).getJsoupPattern() + ")";
        }
      } else if (name.equalsIgnoreCase(".id")) {
        if (value instanceof String) {
          if (((String) value).contains(".") || ((String) value).contains("#") || ((String) value).contains(" ")) {
            attr.add("[id=" + value + "]");
          } else {
            id = "#" + (String) value;
          }
        } else {
          attr.add("[id~=" + ((RegularExpression) value).getJsoupPattern() + "]");
        }

      } else if (name.equalsIgnoreCase(".className")) {
        if (value instanceof String) {
          if (((String) value).contains(".") || ((String) value).contains("#") || ((String) value).contains(" ")) {
            attr.add("[class=" + (String) value + "]");
          } else {
            className = "." + (String) value;
          }
        } else {
          attr.add("[class~=" + ((RegularExpression) value).getJsoupPattern() + "]");
        }
      } else {
        name = getAttributeName(name);
        if (value instanceof String) {
          attr.add("[" + name + "=" + (String) value + "]");
        } else {
          attr.add("[" + name + "~=" + ((RegularExpression) value).getJsoupPattern() + "]");
        }
      }
    }

    StringBuffer buf = new StringBuffer();
    buf.append(tag);
    if (id != null) {
      buf.append(id);
    }

    if (className != null) {
      buf.append(className);
    }

    for (String s : attr) {
      buf.append(s);
    }

    if (text != null) {
      buf.append(text);
    }

    String selector = buf.toString();
//		logger.debug("CSS Selector="+selector);
    return selector;
  }


  protected String getAttributeName(String value) {
    if (value.equalsIgnoreCase(".className")) {
      return "class";
    } else {
      if (value.startsWith("."))
        return value.substring(1);
      else
        return value;
    }
  }

  protected String getTagName(String value) {
    String tagName = "";
    int i = value.indexOf(".");
    int j = value.lastIndexOf(".");
    if (i == j) {
      tagName = value.substring(i + 1);
    } else {
      tagName = value.substring(i + 1, j);
    }

    return tagName;
  }

  protected int getTestObjectTypeFromProperty(Property[] property) {
    for (int i = 0; i < property.length; i++) {
      if (property[i].getPropertyName().equalsIgnoreCase(".class")) {
        String value = property[i].getPropertyValue().toString();
        if (value.matches("Html.(INPUT.(text|password|time|date)|TEXTAREA)")) {
          return TEXT;
        } else if (value.equalsIgnoreCase("Html.A")) {
          return LINK;
        } else if (value.equalsIgnoreCase("Html.TABLE")) {
          return TABLE;
        } else if (value.equalsIgnoreCase("Html.SELECT")) {
          return LIST;
        } else if (value.equalsIgnoreCase("Html.INPUT.checkbox")) {
          return CHECKBOX;
        } else if (value.equalsIgnoreCase("Html.INPUT.radio")) {
          return RADIO;
        } else if (value.equalsIgnoreCase("Html.FRAME")) {
          return FRAME;
        } else if (value.equalsIgnoreCase("Html.FRAMESET")) {
          return FRAMESET;
        } else if (value.equalsIgnoreCase("Html.INPUT")) {
          throw new NotSupportedException("The type of INPUT is not specified. It should be Html.INPUT.<type>.");
        }
      }
    }
    return NOTDEFINED;
  }


//	public void getLable(Property[] p, String url){
//
//	}

//	public void getTextField(Property[] p, String url){
//
//}

//	public void getCheckbox(Property[] p, String url){
//
//}


  public void getLable(String url) {

  }


  public void getButtons(String url) {

  }

  public void getType(String url) {

  }

  public void compareHTML(String pagesource) {
    //find element / div id etc.
  }

}

