package com.active.qa.automation.web.testcrawler;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * @author : tchen
 * @since : 3/17/2016.
 */
public class testMain {
  public static void main(String[] args) throws Exception {
    String url = "https://orms-torqa5.dev.activenetwork.com/";
    System.setProperty("webdriver.ie.driver", "D:\\ShareFolder\\IEDriverServer_x64.exe");
    WebDriver driver = new InternetExplorerDriver();
    driver.get(url);
    String source = PgParser.getPageSource(driver);
    System.out.println( PgParser.cleanrelaxed(source));
    driver.close();
  }
}
