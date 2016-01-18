package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.exception.NotSupportedException;
import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.page.IPopupPage;
import com.active.qa.automation.web.testapi.interfaces.page.Loadable;
import com.active.qa.automation.web.testapi.pages.DialogPage;
import com.active.qa.automation.web.testapi.pages.HtmlPopupPage;
import com.active.qa.automation.web.testapi.pages.Page;
import com.active.qa.automation.web.testapi.util.*;
import com.active.qa.automation.web.testdriver.driver.selenium.dialog.AlertDialog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.BuildInfo;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SimpleBrowser will implement all methods in ISimpleBrowser.
 * Created by tchen on 1/6/2016.
 */
public abstract class SimpleBrowser  extends Browser {
    protected WebDriver browser;
    protected String handler;
    protected static final int NOTDEFINED=0;
    protected static final int LINK=1;
    protected static final int TEXT=2;
    protected static final int LIST=3;
    protected static final int CHECKBOX=4;
    protected static final int RADIO=5;
    protected static final int TABLE=6;
    protected static final int FRAME=7;
    protected static final int BUTTON=8;
    protected static final int FRAMESET=9;
    protected boolean driverLocked;
    protected boolean popupMayExist=false;
    Timer timer=new Timer(); 	//for page timing
    protected String selenium_version=new BuildInfo().getReleaseLabel();//"2.39.0";

    @Override
    public String getDriverName() {
        return "Selenium "+selenium_version;
    }

    @Override
    public IHtmlObject[] getAllTestObjects() {
        try {
            Document dom=getDOM();
            Elements elements=dom.getAllElements();
            IHtmlObject[] objs=null;
            int size=elements.size();
            if(size>0) {
                objs=new HtmlObject[size];
                for(int i=0;i<size;i++) {
                    objs[i]=new HtmlObject(elements.get(i), browser.getWindowHandle());
                }
            }

            return objs;

        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    protected Document getDOM() {
        switchTo(handler);
        return Jsoup.parse(browser.getPageSource());
    }

    protected boolean isFrame(HtmlObject e) {
        String tag=e.getElement().tagName();
        RegularExpression frame=new RegularExpression("frame|iframe",false);
        return frame.match(tag);
    }

    protected String constructXPath(Property[] property, List<Property> regexProperty) {
        String tag="*";
        List<String> attributes=new ArrayList<String>();
        String text=null;
        logger.debug(Property.propertyToString(property));
        for(Property p: property) {
            String name=p.getPropertyName();
            Object o=p.getPropertyValue();
            String value=null;
            if(o instanceof String) {
                value=(String)o;
            } else if(o instanceof RegularExpression){
                regexProperty.add(p);
            } else {
                throw new ItemNotFoundException("Value should be either String or RegularExpression");
            }

            if(name.equalsIgnoreCase(".class")) {
                tag=getTagName(value).toUpperCase();
            } else if(name.equalsIgnoreCase(".text")) {
                if(value==null) {
                    text="string-length()>0";
                } else {
                    if(value.contains(TestApiConstants.CELL_DELIMITER)) {
                        value=value.replaceAll(TestApiConstants.CELL_DELIMITER, "");
                    }
                    if(value.contains("'")) {
                        text="normalize-space()="+generateConcatForXPath(value);
                    } else {
                        text="normalize-space()='"+value+"'";
                    }
                }
            } else{
                name=getAttributeName(name);

                if(value==null){
                    attributes.add("@"+name);
                } else {
                    if(value.contains("'")) {
                        attributes.add("@"+name+"="+generateConcatForXPath(value));
                    } else {
                        attributes.add("@"+name+"='"+value+"'");
                    }
                }
            }


        }

        StringBuffer xpath=new StringBuffer();
        xpath.append("//");
        xpath.append(tag);

        boolean isfirst=true;
        if(text !=null || attributes.size()>0) {
            xpath.append("[");
            if(text !=null) {
                xpath.append(text);
                isfirst=false;
            }

            for(String attr:attributes) {
                if(!isfirst) {
                    xpath.append(" and ");
                }
                xpath.append(attr);
                isfirst=false;
            }
            xpath.append("]");
        }

        String toReturn=xpath.toString();
        logger.debug("XPath="+toReturn);
        return toReturn;
    }

    protected String generateConcatForXPath(String text) {
        char[] chars=text.toCharArray();

        StringBuffer b=new StringBuffer();

        b.append("concat(");
        char previous;
        for(int i=0;i<chars.length;i++) {
            if(i==0) {
                previous='(';
            } else {
                previous=chars[i-1];
            }

            if(chars[i]=='\'') {
                if(i==0) {
                    b.append("\"");

                } else if(previous!='\'') {
                    b.append("',\"");
                }
                b.append("'");
            }else {
                if(i==0){
                    b.append("'");
                } else if(previous=='\''){
                    b.append("\",'");
                }
                b.append(chars[i]);
            }

            if(i>=chars.length-1) {
                if(previous=='\'') {
                    b.append("\"");
                } else {
                    b.append("'");
                }
            }


        }
        b.append(")");
        return b.toString();
    }

    protected String constructCSSSelector(Property[] properties) {
        String tag="*";
        String text=null;
        String id=null;
        String className=null;
        List<String> attr=new ArrayList<String>();

        for(Property p:properties) {
            String name=p.getPropertyName();
            Object value=p.getPropertyValue();

            if(name.equals(".class")) {
                tag=getTagName((String)value);
                if(tag.equalsIgnoreCase("input")) {
                    String type=null;
                    try {type=((String)value).substring(11).trim();} catch(Exception e){};
                    if(!StringUtil.isEmpty(type) ) {
                        if(type.equalsIgnoreCase("text"))
                            attr.add("[type!=hidden][type!=checkbox][type!=radio]");
                        else
                            attr.add("[type="+type+"]");
                    }
                }
            } else if(name.equals(".text")) {

                if(value instanceof String) {
                    String t=(String) value;
                    if(t.contains(TestApiConstants.CELL_DELIMITER)) {
                        t=t.replaceAll(TestApiConstants.CELL_DELIMITER, " ");
                    }
                    text=":matches("+StringUtil.convertToRegex(t)+")";
                } else {
                    text=":matches("+((RegularExpression)value).getJsoupPattern()+")";
                }
            } else if(name.equalsIgnoreCase(".id")){
                if(value instanceof String) {
                    if(((String) value).contains(".") || ((String) value).contains("#") || ((String) value).contains(" ")) {
                        attr.add("[id="+value+"]");
                    } else {
                        id="#"+(String)value;
                    }
                } else {
                    attr.add("[id~="+((RegularExpression)value).getJsoupPattern()+"]");
                }

            } else if(name.equalsIgnoreCase(".className")) {
                if(value instanceof String) {
                    if(((String) value).contains(".") || ((String) value).contains("#") || ((String) value).contains(" ")) {
                        attr.add("[class="+(String)value+"]");
                    } else {
                        className="."+(String)value;
                    }
                }else {
                    attr.add("[class~="+((RegularExpression)value).getJsoupPattern()+"]");
                }
            } else {
                name=getAttributeName(name);
                if(value instanceof String) {
                    attr.add("["+name+"="+(String)value+"]");
                } else {
                    attr.add("["+name+"~="+((RegularExpression)value).getJsoupPattern()+"]");
                }
            }
        }

        StringBuffer buf=new StringBuffer();
        buf.append(tag);
        if(id!=null) {
            buf.append(id);
        }

        if(className!=null) {
            buf.append(className);
        }

        for(String s:attr) {
            buf.append(s);
        }

        if(text!=null) {
            buf.append(text);
        }

        String selector=buf.toString();
//		logger.debug("CSS Selector="+selector);
        return selector;
    }

    protected IHtmlObject[] internalGetHtmlObject(String selector,int type, HtmlObject top) {
        List<HtmlObject> elements=new ArrayList<HtmlObject>();
        if(isLocked()) {
            logger.debug("Driver is locked");
            return new HtmlObject[0];
        }

        if(top!=null) {
            String[] aHandler;
            Elements es;
            if(isFrame(top)) {
                String id=top.id();
                aHandler=new String[]{handler,"frame:id="+id};
                switchTo(aHandler);
                es= Jsoup.parse(browser.getPageSource()).select(selector);

            }  else {
                es=top.getElement().select(selector);
                aHandler=top.getHandler();
            }
            for(Element e:es) {
                elements.add(constructHtmlObject(e,type,aHandler));
            }

            searchInsideFrames(elements, aHandler, top.getElement(), selector, type);

        } else {
            long diff=RuntimeUtil.PAGElOADING_WAIT-timer.diffLong();
            if(diff>0) {
                Timer.sleep(diff);
            }
//			System.out.println(handler);

            switchTo(handler);

            Document doc=Jsoup.parse(browser.getPageSource());

            Elements es=doc.select(selector);

            for(Element e:es) {
                elements.add(constructHtmlObject(e,type,browser.getWindowHandle()));
            }

            if(type!=FRAMESET) { //further search inside frames
                searchInsideFrames(elements, new String[]{handler}, doc, selector, type);
            }
        }
//		int size=elements.size();

//		logger.debug("Found "+size+" objects");
        return elements.toArray(new HtmlObject[0]);
    }

    private void searchInsideFrames(List<HtmlObject> elements, String[] handler, Element top, String selector, int type) {
        Elements frameset=top.select("FRAMESET");
        String[] rowsizes=null;
        if(frameset.size()>0) {
            String frameRows=frameset.get(0).attr("rows");
            rowsizes=frameRows.split(",");
        }
        Elements frames=top.select("FRAME, IFRAME");
        int size=frames.size();
        for(int i=0;i<size;i++) {
            if(rowsizes!=null && i<rowsizes.length && rowsizes[i].equalsIgnoreCase("0px")) { //skip 0 size frames
                continue;
            }
            String[] frameHandler=StringUtil.arrayExtend(handler,"frame:index="+i);
            try {
                switchTo(frameHandler);
                top=Jsoup.parse(browser.getPageSource());
                Elements es=top.select(selector);
                for(Element ee:es) {
                    elements.add(constructHtmlObject(ee,type,frameHandler));
                }
                searchInsideFrames(elements,frameHandler,top,selector,type);

            } catch(NoSuchFrameException except) {}

        }
    }

    @Override
    public IHtmlObject[] getHtmlObject(Property[] property, IHtmlObject top) {
        int type=getTestObjectTypeFromProperty(property);
        String selector=constructCSSSelector(property);
        try {
            IHtmlObject[] objs= internalGetHtmlObject(selector,type,(HtmlObject)top);
//			System.out.println(top.toString());
            return objs;
        } catch(org.openqa.selenium.UnhandledAlertException e) {
            return new HtmlObject[0];
        }
    }

    protected HtmlObject constructHtmlObject(Element e, int type, String... handler) {
        HtmlObject obj;
        switch(type) {
            case LINK:
                obj=new LinkObject(e,handler);
                break;
            case TEXT:
                obj=new TextFieldObject(e,handler);
                break;
            case CHECKBOX:
                obj=new CheckBoxObject(e,handler);
                break;
            case RADIO:
                obj=new RadioButtonObject(e,handler);
                break;
            case FRAME:
                obj=new FrameObject(e,handler);
                break;
            case TABLE:
                obj=new TableObject(e,handler);
                break;
            case LIST:
                obj=new DropdownList(e,handler);
                break;
            case NOTDEFINED:
            default:
                obj=new HtmlObject(e,handler);
        }

        return obj;
    }

    @Override
    public IHtmlObject[] getHtmlObject(List<Property[]> list) {
        return getHtmlObject(list,(IHtmlObject)null);
    }

    @Override
    public IHtmlObject[] getHtmlObject(List<Property[]> list, IHtmlObject top) {
        String selector="";
        int size=list.size();
        int type=getTestObjectTypeFromProperty(list.get(size-1));

        for(int i=0;i<size;i++) {
            Property[] p=list.get(i);
            Object classValue=Property.getValue(p, ".class");
            if(classValue!=null && ((String)classValue).matches("Html\\.(I)?FRAME")) {
                top=(HtmlObject)getFrame((String)Property.getValue(p, ".id"))[0];
            } else {
                selector +=" "+this.constructCSSSelector(p);
            }
        }

        selector=selector.trim();

        return internalGetHtmlObject(selector,type,(HtmlObject)top);
    }

    @Override
    public IHtmlObject[] getTableTestObject(Property[] property, IHtmlObject top) {
        property=Property.addToPropertyArray(property, ".class", "Html.TABLE");
        return getHtmlObject(property,top);
    }

    @Override
    public IHtmlObject[] getDropdownList(Property[] property, IHtmlObject top) {
        property=Property.addToPropertyArray(property, ".class", "Html.SELECT");
        return getHtmlObject(property,top);
    }

    @Override
    public IHtmlObject[] getTextField(Property[] property, IHtmlObject top) {
        property=Property.addToPropertyArray(property, ".class", "Html.INPUT.text");
        return getHtmlObject(property,top);
    }

    @Override
    public IHtmlObject[] getIFrameTextField(Property[] property, IHtmlObject top) {
        property=Property.addToPropertyArray(property, ".class", "Html.IFRAME");
        return getHtmlObject(property,top);
    }

    @Override
    public void setIFrameTextField(Property[] property, String text, boolean forced, int index, IHtmlObject top) {
        IHtmlObject[] objs=getIFrameTextField(property,top);

        if(objs!=null && objs.length>index) {

            String frameHandler = "frame:";
            if(StringUtil.notEmpty(((HtmlObject)objs[index]).element.id()))
            {
                frameHandler += "id="+((HtmlObject)objs[index]).element.id();
            }else{
                frameHandler += "index="+index;
            }
            switchTo(handler,frameHandler);
            WebElement iFrameBoday=browser.findElement(By.xpath("/HTML/BODY"));
            iFrameBoday.click();
            iFrameBoday.sendKeys(Keys.END);
            iFrameBoday.sendKeys(Keys.chord(Keys.CONTROL,"a"));
            iFrameBoday.sendKeys(Keys.BACK_SPACE);
            iFrameBoday.sendKeys(text);
            switchTo(handler);
        } else if(forced) {
            throw new ItemNotFoundException("Failed to find the IFrameText Field object");
        }
    }

    @Override
    public IHtmlObject[] getRadioButton(Property[] property, IHtmlObject top){
        property=Property.addToPropertyArray(property, ".class", "Html.INPUT.radio");
        return getHtmlObject(property,top);
    }

    protected int getTestObjectTypeFromProperty(Property[] property) {
        for(int i=0;i<property.length;i++) {
            if(property[i].getPropertyName().equalsIgnoreCase(".class")) {
                String value=property[i].getPropertyValue().toString();
                if(value.matches("Html.(INPUT.(text|password|time|date)|TEXTAREA)")){
                    return TEXT;
                } else if(value.equalsIgnoreCase("Html.A")) {
                    return LINK;
                } else if(value.equalsIgnoreCase("Html.TABLE")) {
                    return TABLE;
                } else if(value.equalsIgnoreCase("Html.SELECT")) {
                    return LIST;
                } else if(value.equalsIgnoreCase("Html.INPUT.checkbox")) {
                    return CHECKBOX;
                } else if(value.equalsIgnoreCase("Html.INPUT.radio")) {
                    return RADIO;
                } else if(value.equalsIgnoreCase("Html.FRAME")) {
                    return FRAME;
                } else if(value.equalsIgnoreCase("Html.FRAMESET")) {
                    return FRAMESET;
                } else if(value.equalsIgnoreCase("Html.INPUT")) {
                    throw new NotSupportedException("The type of INPUT is not specified. It should be Html.INPUT.<type>.");
                }
            }
        }
        return NOTDEFINED;
    }

    protected String getTagName(String value) {
        String tagName="";
        int i=value.indexOf(".");
        int j=value.lastIndexOf(".");
        if(i==j) {
            tagName=value.substring(i+1);
        } else {
            tagName=value.substring(i+1,j);
        }

        return tagName;
    }

    protected String getAttributeName(String value) {
        if(value.equalsIgnoreCase(".className")) {
            return "class";
        } else {
            if(value.startsWith("."))
                return value.substring(1);
            else
                return value;
        }
    }

    @Override
    public IHtmlObject[] getFrame(String idValue) {
        try {
            switchTo(handler);
            Document doc=Jsoup.parse(browser.getPageSource());
            Elements frames=doc.select("FRAME#"+idValue+", IFRAME#"+idValue);
            IHtmlObject[] objs=new FrameObject[frames.size()];
            for(int i=0;i<frames.size();i++) {
                objs[i]=new FrameObject(frames.get(i),handler);
            }
            return objs;
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

//	@Override
//	public IHtmlObject getFrame(int idIndex) {
//		try {
//			switchTo(handler);
//			Document doc=Jsoup.parse(browser.getPageSource());
//			Elements frames=doc.select("FRAME");
//			IHtmlObject[] objs=new FrameObject[frames.size()];
//			for(int i=0;i<frames.size();i++) {
//				objs[i]=new FrameObject(frames.get(i),handler);
//				System.out.println(objs[i].toString());
//			}
//			return objs[idIndex];
//		} catch (Exception e) {
//			throw new ActionFailedException(e.getMessage());
//		}
//	}

    @Override
    public void switchToFrame(int i) {
        try {
            browser.switchTo().frame(i);
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    @Override
    public void switchToFrameWd(String txt) {
        try {
            String path = "//frame[@name='"+txt+"']";
            browser.switchTo().frame(browser.findElement(By.xpath(path)));
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    @Override
    public String getPageSource() {
        return browser.getPageSource();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int[] loadingSync(int timeout,List<Property> list) {
        boolean found=false;
        int dismissCount=0;
        int foundIndex=-1;

        //index 0-code
        //index 1-dialogCount
        //index 2-htmlPopupCount
        //index 3-normalPageCount
        //index 4-printPopupCount
        //index 5-dialogAfterPageLoading
        //index 6-popupAfterPageLoading
        //index 7-totalAfterPageLoading
        int[] analyzeCode=anylyzePageList(list); //0- no dialog and html popup; 1- only dialog; 2 - at least one dialog or html popup plus other pages
        popupMayExist=analyzeCode[0]!=0;
        if(analyzeCode[7]>0 || analyzeCode[8]>0) {
            sleep(PAGELOADING_SYNC_TIME);
        }

        do {
            for(int i=0;i<list.size() && !found;i++) {
                logger.debug("\tChecking page: "+list.get(i).getPropertyName());
                Object o=list.get(i).getPropertyValue();
                if(o instanceof DialogPage) {
                    DialogPage dialog=(DialogPage) o;
                    if(dialog.dismissible()) {
                        while(dialog.exists() && dismissCount<30) {//limit the dismiss Count to avoid infinite loop
                            dialog.dismiss();
                            sleep(1);
                            dismissCount++;
                        }
                    } else {
                        found=dialog.exists();
                    }

					/*the logic below will handle the scenario that dialog is the only page and is dismissable.
					  in this scenario, we need to get out of waitExists and return if dialogs got dismissed and
					  set found=true if the dialog is the only page and got dismissed and no more dismissed in 5 loops.
					  this is based on the assumption that if many dialogs may appear, they appear in sequence*/
                    if(!found && analyzeCode[0]==1 && dialog.dismissible() && dismissCount>0) {
                        found=true;
                    }
                } else if(o instanceof IPopupPage) {
                    found=((IPopupPage)o).exists();
                    if(found && o instanceof HtmlPopupPage) {
                        AlertDialog dialog=new AlertDialog();
                        int count=0;
                        while(dialog.exists() && count<5) {
                            dialog.clickOK();
                            sleep(1);
                            count++;
                        }

                        if(dialog.exists()) {
                            throw new ItemNotFoundException("Could not dismiss some dialog!");
                        }
                    }
                } else {
                    boolean popupExists=analyzeCode[0]==0?false:checkPopupExists();
                    if(o instanceof Loadable) {

                        found=!popupExists && ((Loadable) o).exists();
                    } else if(o instanceof Property[]) {
                        found=!popupExists && checkHtmlObjectExists((Property[]) o);
                    } else if(o instanceof List) {
                        found=!popupExists && checkHtmlObjectExists((List<Property[]>) o);
                    }
                }

                if(found) {
                    foundIndex=i;
                } else if(analyzeCode[0]==1 && dismissCount>0) {
                    found=true;
                    foundIndex=0;
                } else {
                    sleep(FIND_OBJECT_WAIT_BETWEEN_RETRY);
                }
            }
        }while(!found && timer.diff()<timeout);

        int loadingTime=timer.diff();

        return new int[]{foundIndex,loadingTime};

    }

    protected int[] anylyzePageList(List<Property> list) {
        int dialogCount=0;
        int htmlPopupCount=0;
        int dialogAfterPageLoading=0;
        int popupAfterPageLoading=0;
        int totalAfterPageLoading=0;
        int normalPageCount=0;
        int printPopupCount=0;
        int containLastVisited=0;

        Page last= PageTrack.getLastPageVisited();

        for(int i=0;i<list.size();i++) {
            Object o=list.get(i).getPropertyValue();
            if(last==o) {
                containLastVisited++;
            }

            if(o instanceof DialogPage) {
                dialogCount++;
                if(!((DialogPage)o).isBeforePageLoading()) {
                    dialogAfterPageLoading++;
                    totalAfterPageLoading++;
                }
            } else if(o instanceof HtmlPopupPage) {
                htmlPopupCount++;
                if(!((HtmlPopupPage)o).isBeforePageLoading()) {
                    popupAfterPageLoading++;
                    totalAfterPageLoading++;
                }

            } else {
                normalPageCount++;
            }
        }

        int code;

        if(dialogCount<1 && htmlPopupCount<1) {
            code= 0; //no dialog and html popup
        } else if(dialogCount>0 && htmlPopupCount<1 && normalPageCount<1) {
            code= 1; // only dialog
        } else if(dialogCount<1 && htmlPopupCount>0 && normalPageCount<1) {
            code= 2; // only html popup
        } else if(dialogCount>0 && htmlPopupCount<1 && normalPageCount>0) {
            code= 3; // dialog and normal pages
        } else if(dialogCount<1 && htmlPopupCount>0 && normalPageCount>0) {
            code= 4; // html popup and normal pages
        } else {
            code= 5; // dialog and html popup exists
        }
        //index 0-code
        //index 1-dialogCount
        //index 2-htmlPopupCount
        //index 3-normalPageCount
        //index 4-printPopupCount
        //index 5-dialogAfterPageLoading
        //index 6-popupAfterPageLoading
        //index 7-totalAfterPageLoading
        //index 8-containLastVisited
        return new int[] {code,dialogCount,htmlPopupCount,normalPageCount,printPopupCount,dialogAfterPageLoading,popupAfterPageLoading,totalAfterPageLoading,containLastVisited};
    }

    protected boolean isDialogOnly(List<Property> list) {
        int dialogCount=0;
        int otherCount=0;

        for(int i=0;i<list.size();i++) {
            Object o=list.get(i).getPropertyValue();
            if(o instanceof DialogPage) {
                dialogCount++;
            } else  {
                otherCount++;
            }
        }

        if(dialogCount>0 && otherCount<1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clickImageArea(Property[] imageProperties, Object areaHref,boolean forced, IHtmlObject top) {
        Property[] properties=Property.toPropertyArray(".class","Html.AREA",".href",areaHref);
        IHtmlObject[] areas=getHtmlObject(properties,top);
        IHtmlObject[] imgs=getHtmlObject(imageProperties, top);

        if(areas.length>0 && imgs.length>0) {
            String coords=areas[0].getAttributeValue("coords");
            int[] point=getCenterPoint(coords.split(","));

            Actions builder=new Actions(browser);
            Action click=builder.moveToElement(((HtmlObject)imgs[0]).getWebElement(), point[0], point[1]).click().build();
            click.perform();
        }  else if(forced) {
            throw new ItemNotFoundException("Failed to find area in the image with the given href");
        }
    }

    @Override
    public void closeAllBrowsers() {
        super.closeAllBrowsers();
        try {
            String driverName = System.getProperty("webdriver.ie.driver.name");
            Runtime.getRuntime().exec("taskkill /F /IM "+driverName+" /T").waitFor();
        } catch (Exception e) {
            logger.error("Failed to stop IEDriverServer due to "+e.getMessage());
        }
        browser=null;
    }

    private int[] getCenterPoint(String[] coords) {
        int size=coords.length/2;
        int[] xs=new int[size];
        int[] ys=new int[size];
        for(int i=0;i<size;i++) {
            xs[i]=Integer.parseInt(coords[i*2]);
            ys[i]=Integer.parseInt(coords[i*2+1]);
        }

        Polygon p= new Polygon(xs,ys,size);

        Rectangle r=p.getBounds();

        int x=r.x;
        int y=r.y;
        int h=r.height;
        int w=r.width;

        x= x+w/2;
        y= y+h/2;
        return new int[]{x,y};

    }

    protected boolean checkPopupExists() {
        logger.debug("Checking Popup exists......");
        String url=null;
        try {
            if(popupMayExist) {
                switchTo(handler);
                boolean exists=false;

                Set<String> handlers=browser.getWindowHandles();

                if(handlers.size()>1) {
                    for(String h:handlers) {
                        if(!h.equals(handler)) {
                            switchTo(handler,h);
                            try {
                                url=browser.getCurrentUrl();
                                exists=url!=null && url.trim().length()>0;

                                if(exists) {
                                    logger.debug("Popup with url=\""+url+"\" exists.");
                                    break;
                                }
                            } catch(Throwable e) {
                                logger.warn(e);
                            }
                        }
                    }
                }
                switchTo(handler);

                if(!exists) {
                    exists=new AlertDialog().exists();
                    if(exists) {
                        logger.debug("Dialog exists!");
                    } else {
                        logger.debug("Popup doesn't exists.");
                    }
                }
                return exists;
            } else {
                return false;
            }
        } catch(org.openqa.selenium.UnhandledAlertException e) {
            return true;
        }
    }

//	int getIEVersion() {
//		if(IEVersion<=0) {
////			String javascript="var rv = 999; if (navigator.userAgent.indexOf('MSIE') != -1) rv = parseFloat(navigator.userAgent.split('MSIE')[1]); return rv;";
////			Object version=((JavascriptExecutor) browser).executeScript(javascript);
////			if(version instanceof Long) {
////				IEVersion=((Long)version).intValue();
////			} else if(version instanceof Double) {
////				IEVersion=((Double)version).intValue();
////			}
//			IEVersion=SysInfo.getIEVersion();
//		}
//
//		return IEVersion;
//	}

    WebDriver switchTo(String... handlers) {
        WebDriver driver=null;
        for(String h: handlers) {
            if(h.startsWith("frame:")) {
                String identifier=h.substring(6);
                if(identifier.startsWith("index=")) {
                    String index=identifier.substring(6);
                    driver= browser.switchTo().frame(Integer.parseInt(index));
                } else {
                    int i=identifier.indexOf("=");
                    driver=browser.switchTo().frame(identifier.substring(i+1));
                }

            } else {
                driver= browser.switchTo().window(h);
            }
        }
        seleniumSync();

        return driver;
    }

    private void seleniumSync() {
        int benchmark=SysInfo.benchmark();
        if(benchmark > 1500 ) {
            Timer.sleep(benchmark/10);
        }
    }

    Object executeJavascript(String script, WebElement e) {
        Object toReturn=null;
        try {
//			String msg=script.length()>30?script.substring(0, 30)+"...":script;
//			logger.info("Executing javascript: "+msg);
            if(e==null) {
                toReturn=((JavascriptExecutor) browser).executeScript(script);
            } else {
                toReturn=((JavascriptExecutor) browser).executeScript(script,e);
            }
//			logger.info("Javascript done.");
        } catch (Exception ex) {
            logger.warn("Failed to run javascript \""+script+"\" due to "+ex.getMessage());
            return ex;
        }

        return toReturn;
    }

    void executeJavascriptInThread(String script, WebElement e) {
        JavaScript js=new JavaScript(e,script);
        js.start();
    }

    class JavaScript extends Thread {
        WebElement e;
        String script;
        Object toReturn=null;

        public JavaScript(WebElement element,String script) {
            this.e=element;
            this.script=script;
        }

        public void run() {
            lockDriver();
            toReturn=executeJavascript(script,e);
            unlockDriver();
        }
    }

    void lockDriver() {
//		logger.debug("lock Driver");
        driverLocked=true;
    }

    void unlockDriver() {
//		logger.debug("Driver is unlocked");
        driverLocked=false;
    }

    boolean isLocked() {
        return driverLocked;
    }
}


