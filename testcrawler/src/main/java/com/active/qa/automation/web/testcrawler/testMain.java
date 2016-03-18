package com.active.qa.automation.web.testcrawler;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.File;

/**
 * @author : tchen
 * @since : 3/17/2016.
 */
public class testMain {
  public static void main(String[] args) throws Exception {
    String url = "https://orms-torqa5.dev.activenetwork.com/LicenseMgrLogin.do";
    System.setProperty("webdriver.ie.driver", "D:\\ShareFolder\\IEDriverServer_x64.exe");
    WebDriver driver = new InternetExplorerDriver();
    driver.get(url);
    PgParser ps = new PgParser();
    String source = ps.getPageSource(driver);
    ps.savePgSource(source);
    driver.close();

    File ieDriver=new File(System.getProperty("webdriver.ie.driver"));
    Runtime.getRuntime().exec("taskkill /F /IM "+ieDriver.getName()).waitFor();
    System.gc();
    System.exit(0);
  }
}
