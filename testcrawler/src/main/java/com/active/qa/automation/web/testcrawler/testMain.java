package com.active.qa.automation.web.testcrawler;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * @author : tchen
 * @since : 3/17/2016.
 */
public class testMain {
  public static void main(String[] args) throws Exception {
    String url = "https://www.baidu.com/";
    System.setProperty("webdriver.ie.driver", "D:\\ShareFolder\\IEDriverServer_x64.exe");
    WebDriver driver = new InternetExplorerDriver();
    driver.get(url);
    System.out.println(PgParser.getPageSource(driver));
    String source = PgParser.getPageSource(driver);
    PgParser.getUrlText(source, "listView");
    driver.close();
  }
}
