package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.exception.NotSupportedException;
import com.active.qa.automation.web.testapi.interfaces.browser.IBrowser;
import com.active.qa.automation.web.testapi.interfaces.browser.ISimpleBrowser;
import com.active.qa.automation.web.testapi.interfaces.dialog.*;
import com.active.qa.automation.web.testapi.util.*;
import com.active.qa.automation.web.testdriver.driver.selenium.dialog.*;
import com.compdev.jautoit.autoitx.AutoitXFactory;
import com.compdev.jautoit.autoitx.Autoitx;
import com.sun.jna.WString;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Set;

/**
 * This is the runtime Browser implementation using Selenium driver
 * Created by tchen on 1/6/2016.
 */
public class SeleniumBrowser extends SimpleBrowser implements IBrowser {
    public static void init() {
        if(_instance==null) {
            _instance=new SeleniumBrowser();
        }
    }

    protected SeleniumBrowser(String attributeName, Object value) {
        throw new NotSupportedException("Selenium doesn't support attaching to an existing browser!");
    }

    protected SeleniumBrowser() {
        RuntimeUtil.cleanupTestDriver();
        browser=null;
        driverLocked=false;
        String ieDriver="IEDriverServer"+(System.getProperty("sun.arch.data.model").equals("32")?".exe":"_x64.exe");
        String driverPath=getDriverPath(ieDriver);
        System.setProperty("webdriver.ie.driver.name", ieDriver);
        System.setProperty("webdriver.ie.driver", driverPath);
        System.setProperty("webdriver.chrome.driver", getDriverPath("chromedriver.exe"));
        if(TestProperty.getBooleanProperty("debug", false)==true) {
            System.setProperty("webdriver.ie.driver.loglevel", "DEBUG");
            System.setProperty("webdriver.ie.driver.logFile", driverPath.replaceAll("\\.exe", "_"+SysInfo.getHostName()+".log"));
        }
    }

    protected SeleniumBrowser(WebDriver driver) {
        browser=driver;
        handler=driver.getWindowHandle();
        driverLocked=false;
    }
    private String getDriverPath(String driverName) {

        URL driverUrl=SeleniumBrowser.class.getResource(driverName);
        String path=driverUrl.getPath();
        if(path.contains("jar!")) {
            int index=path.substring(0, path.lastIndexOf(".jar!")).lastIndexOf("/");

            path=path.substring(0,index).replaceFirst("file:/", "")+"/"+driverName;

            File f=new File(path);
            if(!f.exists()) {
                try {
                    FileUtil.copyFile(driverUrl.openStream(), new FileOutputStream(f));
                } catch (Exception e) {
                    throw new ActionFailedException(e);
                }
            } else {
                path=path.replaceFirst("^/", "");
            }
        }
        return path;
    }

    @Override
    public boolean exists() {
        boolean exists=false;
        try {
            exists= browser!=null;
        }catch(Exception e) {

        }

        return exists;
    }

    @Override
    public void open(String url) {
        try {
            if(browser==null) {
                String useBrowser = TestProperty.getProperty( "browser.impl", "ie" );

                if( useBrowser.equals( "chrome" ) ) {
                    DesiredCapabilities dc = DesiredCapabilities.chrome();
                    dc.setCapability("unexpectedAlertBehaviour", "ignore");
                    dc.setCapability("acceptSslCerts", true);
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments(new String[] {"start-maximized",
                            "--start-maximized",
                            "allow-running-insecure-content",
                            "--test-type"});
                    dc.setCapability(ChromeOptions.CAPABILITY, options);
                    browser = new ChromeDriver(dc);
                    maximize();
                }
                if( useBrowser.equals( "firefox" ) ) {
                    DesiredCapabilities dc = DesiredCapabilities.firefox();
                    dc.setCapability("firefox_binary", "C:\\Program Files\\Mozilla Firefox\\firefox.exe");
                    browser = new FirefoxDriver(dc);
                    browser.manage().window().maximize();
                }
                if( useBrowser.equals( "phantomjs" ) ) {
                    DesiredCapabilities dc = DesiredCapabilities.phantomjs();
                    dc.setJavascriptEnabled(true);
                    dc.setCapability("takesScreenshot", false);
                    dc.setCapability("IgnoreSslErrors", true);
                    dc.setCapability("WebSecurity", false);
                    dc.setCapability("LocalToRemoteUrlAccess", true);
                    dc.setCapability("DiskCache", true);
                    dc.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:\\AWO_QA\\phantomjs-2.0.0-windows\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe");
                    browser = new PhantomJSDriver(dc);
                    //browser.manage().window().setSize(new Dimension(1618, 997));
                    browser.manage().window().maximize();
                }
                if( useBrowser.equals( "ie" ) ) {
                    DesiredCapabilities dc=DesiredCapabilities.internetExplorer();
                    dc.setCapability("unexpectedAlertBehaviour", "ignore");
                    dc.setCapability("requireWindowFocus", true);
                    dc.setCapability("acceptSslCerts", true);
                    dc.setCapability("initialBrowserUrl", "about:blank");
                    dc.setCapability("ie.ensureCleanSession", true);
                    browser=new InternetExplorerDriver(dc);
                    maximize();
                }

                handler=browser.getWindowHandle();
                logger.info( "A new browser is opened. " + BrowserInfo.current().name() + " version: " + SysInfo.getIEVersion() );
            }
            browser.get(url);

            logger.info("Loaded url: "+url);
        } catch(Exception e) {
            throw new ActionFailedException(e.getMessage(), e);
        }
    }

    @Override
    public void back() {
        try {
            browser.navigate().back();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            browser.close();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public void forward() {
        try {
            browser.navigate().forward();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public void load(String url) {
        try {
            browser.navigate().to(url);
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public void maximize() {
        try {
            logger.info("Maximize browser.");
            ((JavascriptExecutor) browser).executeScript("if (window.screen){window.moveTo(0, 0);window.resizeTo(window.screen.availWidth,window.screen.availHeight);};");
            executeJavascript("if (window.screen){window.moveTo(0, 0); window.resizeTo(window.screen.availWidth,window.screen.availHeight);};",null);
//			browser.manage().window().maximize();
            Timer.sleep(1000);
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public void minimize() {
        try {
            logger.info("Minimize browser.");
            ((JavascriptExecutor) browser).executeScript("if (window.screen){window.moveTo(0, 0);window.resizeTo(0,0);window.blur()};");
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public void open() {
        try {
            closeAllBrowsers();
            browser=new InternetExplorerDriver();
            handler=browser.getWindowHandle();
            maximize();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public void refresh() {
        try {
            browser.navigate().refresh();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    @Override
    public boolean sync() {
        return sync(LONG_SLEEP);
    }

    @Override
    public boolean sync(int timeout) {
        boolean synced=true;
        try {
            //WebDriver will return the control back after browser loading finished.
            //no sync needed here
        } catch (Exception e) {

        }
        return synced;

    }

    protected String getBrowserStatus() {
        try {
            String ieAttributes="[TITLE:"+title()+" - Windows Internet Explorer; CLASS:IEFrame]";
            Autoitx win= AutoitXFactory.getAutoitx();
            byte[] buf=new byte[1024];
            if(win.AU3_WinExists(new WString(ieAttributes), new WString(""))==1) {
                win.AU3_StatusbarGetText(new WString(ieAttributes), new WString(""), 1,buf, 512);
                return StringUtil.autoitxBytesToString(buf);
            } else {
                return null;
            }
        } catch(IOException e) {
            throw new ActionFailedException(e);
        }
    }

    @Override
    public String text() {
        try {
            return StringUtil.convertSpaceUnicode2ASCII(getDOM().text()).trim();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    @Override
    public String title() {
        try {
            return browser.getTitle();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    @Override
    public String url() {
        try {
            //switch to main browser handler
            switchTo(handler);
            return browser.getCurrentUrl();
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    @Override
    public ISimpleBrowser getHTMLDialog() {
        ISimpleBrowser popup=null;

        try {
            Set<String> handlers=browser.getWindowHandles();
            if(handlers.size()>1) {
                for(String h:handlers) {
                    if(!h.equalsIgnoreCase(handler)) {
                        popup= new HTMLPopup(switchTo(h));
                        break;
                    }
                }
            }
        } catch(Exception e) {
        }
        return popup;
    }

    @Override
    public ISimpleBrowser getHTMLDialog(String attributeName, Object value){
        ISimpleBrowser popup=null;

        String text="";
        try {
            switchTo(handler);
            Set<String> handlers=browser.getWindowHandles();
            if(handlers.size()>1) {

                for(String h:handlers) {
                    if(!h.equalsIgnoreCase(handler)) {
                        popup= new HTMLPopup(switchTo(h));
                        if(attributeName.equalsIgnoreCase("title")) {
                            try {
                                if(SysInfo.getOSName().equalsIgnoreCase("Windows 7")) {
                                    text=popup.getObjectText(".class","Html.TITLE");
                                } else
                                    text=popup.title();

                            } catch(Throwable e){}
                        } else if(attributeName.equalsIgnoreCase("url")) {
                            try {text=popup.url();} catch(Throwable e){};
                        } else {
                            throw new ItemNotFoundException("Unknown attribute: "+attributeName);
                        }

                        if(RuntimeUtil.matchOrEqual(text, value)) {
                            break;
                        } else {
                            popup=null;
                        }
                    }
                }

            }
        } catch (Exception e) {
            popup=null;
        }
        return popup;
    }

    String html() {
        try {
            return browser.getPageSource();
        }catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    WebDriver getWebDriver() {
        return browser;
    }

	/*
	 * @author tchen
	 * Check handler value
	 *
	 */

    public String getcurrentHandler() {
        String currentHandler= browser.getWindowHandle();
        Set<String> handlers = browser.getWindowHandles();
//		return handlers.toString();
        return currentHandler;
    }

	/*
	 * @author tchen
	 * @see com.activenetwork.qa.testapi.interfaces.browser.IBrowser#switchBrowser()
	 * switch to another browser
	 */

    public IBrowser switchBrowser(){
        try {
            String currentHandler= browser.getWindowHandle();
            Set<String> handlers = browser.getWindowHandles();
            for(String handler: handlers) {
                if(!handler.equals(currentHandler)) {
                    browser.switchTo().window(handler);
                } else {
                    continue;
                }
            }
        } catch (Exception e) {}

        return null;

    }


    public IBrowser getBrowser(String attributeName,Object value) {
        try {

            String currentHandler=handler;
            browser.switchTo().window(currentHandler);
            Set<String> winHandlers=browser.getWindowHandles();
            for(String handler: winHandlers) {
                if(handler.equals(currentHandler)) {
                    continue;
                } else {
                    browser.switchTo().window(handler);
                    String url=browser.getCurrentUrl();
                    String title=browser.getTitle();

                    if( (attributeName.equalsIgnoreCase("title") && RuntimeUtil.matchOrEqual(title, value)) ||
                            (attributeName.equalsIgnoreCase("url") && RuntimeUtil.matchOrEqual(url, value))) {
                        return new SeleniumBrowser(browser);
                    }
                }


            }
        } catch (Exception e) {}

        return null;

    }

    public IBrowser getBrowser(String attributeName,Object value, int timeout) {
        long startTime = Calendar.getInstance().getTimeInMillis();
        IBrowser browser=null;
        while(browser==null && DateFunctions.getTimeDiff(startTime)<timeout) {
            try {
                browser=getBrowser(attributeName,value);
            } catch(Exception e) {
            }
        }

        return browser;
    }

    int getMajorVersion() {
        BrowserInfo.current();
        return BrowserInfo.getMajorVersionIgnoreCompitablityMode();
    }

    private String userAgent;

    String getUserAgent() {
        if ( userAgent == null ) {
            Object result = ((JavascriptExecutor)browser).executeScript( "return navigator.userAgent" );
            if ( result instanceof String )
                userAgent = (String)result;
        }
        return userAgent;
    }

    @Override
    public IAlertDialog getAlertDialog() {
        try {
            IAlertDialog dialog=new AlertDialog();
            if(dialog.exists()) {
                return dialog;
            } else {
                return null;
            }

        } catch(Exception e) {
        }
        return null;
    }

    @Override
    public IConfirmDialog getConfirmDialog() {
        try {
            IConfirmDialog dialog=new ConfirmDialog();
            if(dialog.exists()) {
                return dialog;
            } else {
                return null;
            }

        } catch(Exception e) {
        }
        return null;
    }

    @Override
    public IFileUploadDialog getFileUploadDialog() {
        try {
            IFileUploadDialog dialog=new FileUploadDialog();
            if(dialog.exists()) {
                return dialog;
            } else {
                return null;
            }

        } catch(Exception e) {
        }
        return null;
    }

    @Override
    public IFileDownloadDialog getFiledownloadDialog() {
        try {
            IFileDownloadDialog dialog=new FileDownloadDialog();
            if(dialog.exists()) {
                return dialog;
            } else {
                return null;
            }

        } catch(Exception e) {
        }
        return null;
    }

    @Override
    public IPrintDialog getPrintDialog() {
        try {
            IPrintDialog dialog=new PrintDialog();
            if(dialog.exists()) {
                return dialog;
            } else {
                return null;
            }

        } catch(Exception e) {
        }
        return null;
    }

    @Override
    public IAuthenticationDialog getAuthenticationDialog(Object title) {
        try {
            IAuthenticationDialog dialog=new AuthenticationDialog();
            if(dialog.exists()) {
                return dialog;
            } else {
                return null;
            }

        } catch(Exception e) {
        }
        return null;
    }

}

