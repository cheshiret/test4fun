package com.active.qa.automation.web.testcrawler;

import com.active.qa.automation.web.testapi.util.DateFunctions;
import com.active.qa.automation.web.testapi.util.Property;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author : tchen
 * @since : 3/17/2016.
 */
public class PgParser {

  public static String getPageSource(WebDriver driver) {
    return driver.getPageSource();
  }

  public static String getPgTitle(String pagesource) {
    Document doc = Jsoup.parse(pagesource);
    String title = doc.title();
    if (!title.isEmpty()) {
      return title;
    } else {
      return "NoPageTitle";
    }

  }

  public static Elements getContents(String pagesource, String location) {
    Document doc = Jsoup.parseBodyFragment(pagesource);
    Elements contents;
    Element body = doc.body();
    if (location.isEmpty()) {
      contents = body.getElementsByClass(location);
    } else {
      contents = body.getAllElements();
    }
    return contents;
  }

  public static String[] getTagText(String pagesource, String location, String tagName) {
    Elements contents = getContents(pagesource, location);
    ArrayList<String> list = new ArrayList<>();
    for (Element content : contents) {
      Elements tags = content.getElementsByTag(tagName);
      for (Element tag : tags) {
        String tagText = tag.text();
        list.add(tagText);
      }
    }
    return list.toArray(new String[0]);
  }

  public static String[] getAnchorUrlText(String pagesource, String location) {
    return getTagText(pagesource, location, "a");
  }

  public static String[] getLabelText(String pagesource, String location) {
    return getTagText(pagesource, location, "label");
  }


  public static String[] getDropdownOptions(String pagesource, String location) {
    return getTagText(pagesource, location, "option");
  }

  public static String[] getUrlText(String pagesource) {
    return getAnchorUrlText(pagesource, "");
  }

  public static String cleanbasic(String input) {
    return Jsoup.clean(input, Whitelist.basic());
  }

  public static String cleanrelaxed(String input) {
    return Jsoup.clean(input, Whitelist.relaxed());
  }


  public static String parsePg(String pagesource) {
    Whitelist whitelist = new Whitelist();
    whitelist.addAttributes("a", "href");
    whitelist.addAttributes("input", "type", "class", "name", "id", "value");
    whitelist.addAttributes("select", "name", "id");
    return Jsoup.clean(pagesource, whitelist);
  }

  public static String pagesourceName(String pagesource) {
    String name = null;
    name = getPgTitle(pagesource).replaceAll("\\|*\\s+", "") + "_" +
        DateFunctions.getLongDateStamp();
    return name;
  }

  public static String gettable(String input) {
    return Jsoup.clean(input, Whitelist.basic());
  }

  public static String getdiv(String input, int ind) {
    return Jsoup.clean(input, Whitelist.basic());
  }

  public static String getform(String input, int ind) {
    return Jsoup.clean(input, Whitelist.basic());
  }

  public static String gettext(String input, int ind) {
    return Jsoup.clean(input, Whitelist.basic());
  }

  //id or name
  public static String getcommonattr(String input) {
    return Jsoup.clean(input, Whitelist.basic());
  }

  public void savePgSource(String pagesource) {
    File file = new File("D:\\" + File.separator + this.pagesourceName(pagesource) + ".txt");
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(file, true);
      //fileWriter.write("123");
      fileWriter.write(parsePg(pagesource));
      System.out.println("File was saved successfully!");
      fileWriter.close();
    } catch (IOException e) {
      System.out.println("Error" + e);
    }
  }

  protected Property[] Property(String Parenttag, String tagName, String tag, RegularExpression regExp) {
    return Property.toPropertyArray(Parenttag, tagName, tag, regExp);
  }

  public void compareHTML(String pagesource) {
    //find element / div id etc.
  }

}
