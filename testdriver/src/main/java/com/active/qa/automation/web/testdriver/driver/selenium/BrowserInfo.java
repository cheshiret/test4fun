package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by tchen on 1/6/2016.
 */
public enum BrowserInfo {
  // Ordering is important in detecting browser.
  CHROME("Chrome"),
  FIREFOX("Firefox"),

  IPHONE("iPhone"),
  IPAD("iPad"),
  SAFARI("Safari"),

  OPERA("Opera"),
  // Get version from getMajorVersion
  MSIE("MSIE"),

  // Must always be last... put new browsers above
  UNKNOWN("Unkown");

  private String id;
  private Pattern pattern;

  private BrowserInfo(String id) {
    this.id = id;
    pattern = Pattern.compile(id + "[ \\/]([0-9]{1,}[/.0-9]{0,})");
  }

  public boolean isIE() {
    return id.startsWith("MSIE");
  }

  /**
   * What everyone wants to know...
   *
   * @return
   */
  public static boolean isCurrentIE() {
    return current().isIE();
  }

  /**
   * Determine the Browser for this request
   * based on the user-agent header
   *
   * @return
   */
  public static BrowserInfo current() {
    return getBroswer(((SeleniumBrowser) Browser.getInstance()).getUserAgent());
  }

  public static int getCurrentMajorVersion() {
    return getMajorVersion(((SeleniumBrowser) Browser.getInstance()).getUserAgent());
  }

  public static String getCurrentPlatform() {
    return getPlatform(((SeleniumBrowser) Browser.getInstance()).getUserAgent());
  }

  public static int getMSIEMajorVersion() {
    return getMajorVersion(((SeleniumBrowser) Browser.getInstance()).getUserAgent());
  }

  /**
   * Retrieves the real version of IE despite compatibility mode (which changes the userAgent to an older version).
   * ( only fails if user explicit set their browser to IE7 mode. )
   *
   * @return
   */
  public static int getMajorVersionIgnoreCompitablityMode() {
    String agent = ((SeleniumBrowser) Browser.getInstance()).getUserAgent();
    int version = getMajorVersion(agent);

    if (current() == MSIE && (version == 7 || version == 8)) {
      if (agent.indexOf("Trident/5.0") > -1)
        version = 9;
      else if (agent.indexOf("Trident/4.0") > -1)
        version = 8;
    }

    // System.out.println( "\n==============================\n     " + current() + " " + version + " " + getMajorVersion( SessionManager.get().getUserAgent() ) + "\n" + agent + "\n==============================\n "  );
    return version;

  }

  /**
   * @param key
   * @return
   */
  static BrowserInfo getBroswer(String userAgent) {
    for (BrowserInfo browser : values())
      if (browser != UNKNOWN && userAgent.contains(browser.id))
        return browser;

    return UNKNOWN;
  }

  /**
   * @param key
   * @return
   */
  static int getMajorVersion(String userAgent) {
    int majorVersion = -1;
    BrowserInfo b = getBroswer(userAgent);
    Matcher matcher = b.pattern.matcher(userAgent);
    if (matcher.find()) {
      int startIndex = matcher.start();
      int endIndex = matcher.end();
      int offset = b.id.length() + 1;
      if (endIndex > startIndex + offset) {
        String agent = userAgent.substring(startIndex + offset, endIndex);
        try {
          majorVersion = Integer.parseInt(agent.substring(0, agent.indexOf(".")));
        } catch (NumberFormatException e) {
          majorVersion = -1;
        }
//        if ( b == MSIE && ( majorVersion == 7 || majorVersion == 8 ) ) {
//
//        }
      }
    }
    return majorVersion;
  }

  public static String getPlatform(String userAgent) {
    for (Map.Entry<String, String> entry : platforms.entrySet()) {
      if (userAgent.matches(".*(" + entry.getValue() + ").*"))
        return entry.getKey();
    }
    return "Unknown";
  }

  private static Map<String, String> platforms = new LinkedHashMap<String, String>() {
    private static final long serialVersionUID = 1L;

    {
      put("Mac OS X(Apple)", "iPhone|iPad|iPod|MAC OS X|OS X");
      put("Apple\"s mobile/tablet", "iOS");
      put("BlackBerry", "BlackBerry");
      put("Android", "Android");
      put("Java Mobile Phones (J2ME)", "J2ME/MIDP|J2ME");
      put("Java Mobile Phones (JME)", "JavaME");
      put("JavaFX Mobile Phones", "JavaFX");
      put("Windows Mobile Phones", "WinCE|Windows CE");
      put("Windows 3.11", "Win16");
      put("Windows 95", "Windows 95|Win95|Windows_95");
      put("Windows 98", "Windows 98|Win98");
      put("Windows 2000", "Windows NT 5.0|Windows 2000");
      put("Windows XP", "Windows NT 5.1|Windows XP");
      put("Windows 2003", "Windows NT 5.2");
      put("Windows Vista", "Windows NT 6.0|Windows Vista");
      put("Windows 7", "Windows NT 6.1|Windows 7");
      put("Windows NT 4.0", "Windows NT 4.0|WinNT4.0|WinNT|Windows NT");
      put("Windows ME", "Windows ME");
      put("Open BSD", "OpenBSD");
      put("Sun OS", "SunOS");
      put("Linux", "Linux|X11");
      put("iPad", "iPad");
      put("iPod", "iPod");
      put("iPhone", "iPhone");
      put("Macintosh", "Mac_PowerPC|Macintosh");
      put("QNX", "QNX");
      put("BeOS", "BeOS");
      put("OS/2", "OS/2");
      put("ROBOT", "Spider|Bot|Ezooms|YandexBot|AhrefsBot|nuhk|Googlebot|bingbot|Yahoo|Lycos|Scooter|AltaVista|Gigabot|Googlebot-Mobile|Yammybot|Openbot|Slurp/cat|msnbot|ia_archiver|Ask Jeeves/Teoma|Java");
    }
  };
}
