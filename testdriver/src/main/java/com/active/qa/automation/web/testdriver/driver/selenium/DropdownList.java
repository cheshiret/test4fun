package com.active.qa.automation.web.testdriver.driver.selenium;

import java.util.ArrayList;
import java.util.List;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.html.ISelect;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Created by tchen on 1/6/2016.
 */
class DropdownList extends HtmlObject implements ISelect {
    protected Select selector=null;
    protected List<String> optTexts=null;
    protected List<String> optValues=null;
    protected Elements src_opts=null;
    protected List<WebElement> slm_opts=null;

    public DropdownList(Element sList,String... handler) {
        super(sList,handler);
    }

    protected Elements getElements() {
        if(src_opts==null) {
            src_opts=element.children().select("OPTION");
        }

        return src_opts;
    }
    protected List<WebElement> getOptionElements() {
        if(slm_opts==null)
            slm_opts=getWebElement().findElements(By.xpath("child::OPTION"));

        return slm_opts;
    }

    protected List<String> getSelectedTextsFromSrc() {
        try {
            List<String> texts=new ArrayList<String>();
            Elements os=element.select("option[selected]");
            for(Element e:os) {
                texts.add(e.text());
            }

            return texts;

        } catch(Exception e) {
            throw new ActionFailedException(e);
        }
    }

    private class MySelect extends Select {
        private boolean isMulti=false;
        public MySelect(WebElement e) {
            super(e);
            String value = e.getAttribute("multiple");
            isMulti = (value != null && value.length()>0 && !"false".equals(value));
        }

        public boolean isMultiple() {
            return isMulti;
        }
    }

    protected Select getSelect() {
        if(selector==null)
            selector=new MySelect(getWebElement());
        return selector;
    }

    protected int getOptionSize() {
        Elements optionElements=element.children().select("OPTION");

        return optionElements.size();
    }

    @Override
    public List<String> getAllOptions() {
        if(optTexts==null) {
            getElements();
            optTexts=new ArrayList<String>();
            for(Element e:src_opts) {
                optTexts.add(e.text());
            }

        }

        return optTexts;
    }

    /**
     * Get all values of the dropdown list elements.
     * Here the values mean that the element's attribute named "value", not the element's text.
     * @return
     */
    public List<String> getAllOptionsValues() {
        if(optValues==null) {
            getElements();
            optValues=new ArrayList<String>();
            for(Element e:src_opts) {
                optValues.add(e.val());
            }

        }

        return optValues;
    }

    @Override
    public String getSelectedText() {
//		if(getElements().size()>1000) {
//			AutomationLogger.getInstance().warn("The dropdown list has "+getElements().size()+" options, go through html source.");
//			return getSelectedTextsFromSrc().get(0);
//		} else
//			return getSelect().getFirstSelectedOption().getText();
        List<String> selected=getSelectedTexts();

        return selected.size()>0?selected.get(0):"";
    }

    @Override
    public List<String> getSelectedTexts() {
        try {
            if(getElements().size()>1000 ) {
                AutomationLogger.getInstance().warn("The dropdown list has "+getElements().size()+" options, go through html source");
                return getSelectedTextsFromSrc();
            } else if (!getWebElement().isDisplayed()) { //if the dropdown list is hidden, it has to go through source
                AutomationLogger.getInstance().warn("The dropdown list is not displayed, go through html source");
                return getSelectedTextsFromSrc();
            } else {
                List<String> texts=new ArrayList<String>();
                List<WebElement> options=getSelect().getAllSelectedOptions();
                for(WebElement e:options) {
                    texts.add(e.getText());
                }

                return texts;
            }

        } catch(Exception e) {
            throw new ActionFailedException("Current web element is "+this.toString(),e);
        }
    }

    @Override
    public void select(int index) {
        try {
            getSelect().selectByIndex(index);
        } catch(Exception e) {
            throw new ActionFailedException("Current web element is "+this.toString(),e);
        }
    }

    @Override
    public void select(String item) {
        boolean found=false;
        try {
            if(isMultiple()) {
                getSelect().deselectAll();
            }
            List<String> ops=getAllOptions();

            for(int i=0;i<ops.size();i++) {
                String op=ops.get(i);
                if(item.equalsIgnoreCase(op)) {
                    if(getWebElement().isDisplayed()) {
                        //this is a workaround for selenium issue
                        //somehow isDisplayed() return false for some visible element
                        getSelect().selectByVisibleText(op);
                    }else{
                        String script="var e=arguments[0]; if( e.selectedIndex !="+i+") {e.selectedIndex="+i+";}";
                        browser.executeJavascript(script, getWebElement());
                        new FireEvent(getWebElement(), "onchange").start();
                        javascriptSync();
                    }

                    found=true;
                    break;
                }
            }

        } catch (Exception e) {
            throw new ActionFailedException("Failed to select option: "+item+" due to: "+e);
        }
        if(!found) {
            throw new ActionFailedException("Expected option under "+this.toString()+": '"+item+"' is not available!");
        }
    }

    @Override
    public void select(RegularExpression itemPattern) {
        try {
            if(isMultiple()) {
                getSelect().deselectAll();
            }
            List<String> ops=getAllOptions();

            for(String e:ops) {
                if(itemPattern.match(e)) {
                    getSelect().selectByVisibleText(e);
                    if(!isMultiple()){//if not multiple selection, skill iteration after select first option
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new ActionFailedException("Current web element is "+this.toString(),e);
        }
    }

    @Override
    public boolean isMultiple() {
        return element.hasAttr("multiple");
    }

    @Override
    public void select(String[] items) {
        if(!isMultiple()) {
            throw new ActionFailedException("Current web element is "+this.toString()+ ". Cannot select multiple items");
        }

        try {
            getSelect().deselectAll();

            for(String o:items) {
                getSelect().selectByVisibleText(o);
            }
        }  catch (Exception e) {
            throw new ActionFailedException("Current web element is "+this.toString(),e);
        }

    }

    @Override
    public String toString() {

        String tag = StringUtil.isEmpty(this.tag()) ? "select" : this.tag();
        String id = StringUtil.isEmpty(this.id()) ? "" : this.id();
        String name = StringUtil.isEmpty(this.name()) ? "" : this.name();
        String className = StringUtil.isEmpty(this.className()) ? "" : this.className();

        return tag + "[ id='" + id + "',name='" + name + "',className='" + className + "' ]";
    }
}
