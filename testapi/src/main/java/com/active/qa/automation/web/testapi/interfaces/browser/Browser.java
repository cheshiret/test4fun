package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.*;
import com.active.qa.automation.web.testapi.interfaces.html.ICheckBox;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.html.IRadioButton;
import com.active.qa.automation.web.testapi.interfaces.html.IText;
import com.active.qa.automation.web.testapi.interfaces.html.ISelect;
import com.active.qa.automation.web.testapi.interfaces.page.Loadable;
import com.active.qa.automation.web.testapi.pages.DialogPage;
import com.active.qa.automation.web.testapi.pages.Page;
import com.active.qa.automation.web.testapi.util.AutoItUtil;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.KeyInput;
import com.active.qa.automation.web.testapi.util.PageTrack;
import com.active.qa.automation.web.testapi.util.Property;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.StringUtil;
import com.active.qa.automation.web.testapi.util.TestProperty;
import com.active.qa.automation.web.testapi.util.Timer;
import com.compdev.jautoit.autoitx.AutoitXFactory;
import com.sun.jna.WString;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Extends SeleniumBrowser
 * Created by tchen on 1/11/2016.
 */
public abstract class Browser implements ISimpleBrowser,TestApiConstants {
    protected static AutomationLogger logger = AutomationLogger.getInstance();
    protected static IBrowser _instance=null;

    /**
     * This is the factory method to get a IBrowser instance.
     * The IBrowser instance should be initialized by the browser implementation based on the specific tool
     * @return
     */
    public static IBrowser getInstance() {
        if(null==_instance) {
            throw new ItemNotFoundException("Browser is not initialized");
        }

        return _instance;
    }

    public static void unregister(IHtmlObject... objs) {
        if(objs!=null) {
            for(int i=0;i<objs.length;i++) {
                if(objs[i]!=null) {
                    objs[i].unregister();
                    objs[i]=null;
                }
            }
        }
    }

    public static void unregister(IHtmlObject[]... objs) {
        if(objs!=null) {
            for(int i=0;i<objs.length;i++) {
                unregister(objs[i]);
            }
        }
    }

    public static List<Property[]> atList(Property[]... property) {
        List<Property[]> list=new ArrayList<Property[]>();
        for(int i=0;i<property.length;i++) {
            list.add(property[i]);
        }
        return list;
    }

    public static void sleep(int seconds) {
        try {
            Thread.sleep(1000*seconds);
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
    }


    @Override
    public void waitExists() {
        waitExists(LONG_SLEEP);

    }

    @Override
    public void waitExists(int timeout) {
        boolean found=exists();
        Timer timer=new Timer();
        while(!found && timer.diff()<timeout) {
            sleep(1);
            found=exists();
        }

        if(!found) {
            throw new ItemNotFoundException("Browser not found");
        }
    }

    /**
     * Check if the Checkbox with the given property selected or not
     * @param propertyKey
     * @param value
     * @return
     */
    @Override
    public boolean isCheckBoxSelected(String propertyKey, Object value) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return isCheckBoxSelected(p);
    }

    /**
     * Check if the Checkbox with the given property selected or not
     * @param propertyKey
     * @param value
     * @param top - search the object from
     * @return
     */
    @Override
    public boolean isCheckBoxSelected(String propertyKey, Object value,IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return isCheckBoxSelected(p,top);
    }
    /**
     * is check box selected by index
     * @param propertyKey
     * @param value
     * @param index
     * @return
     */
    public boolean isRadioButtonSelected(String propertyKey, Object value,int index){
        IHtmlObject[] objs=this.getRadioButton(propertyKey, value);
        boolean selected=((IRadioButton)objs[index]).isSelected();

        unregister(objs);

        return selected;
    }

    /**
     * Check if the Checkbox with the given property selected or not
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return
     */
    @Override
    public boolean isCheckBoxSelected(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        return isCheckBoxSelected(p);
    }

    /**
     * Check if the Checkbox with the given property selected or not
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return
     */
    @Override
    public boolean isCheckBoxSelected(String propertyKey1, Object value1,String propertyKey2, Object value2,IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        return isCheckBoxSelected(p,top);
    }

    /**
     * Check if the Checkbox with the given property selected or not
     * @param property
     * @return
     */
    @Override
    public boolean isCheckBoxSelected(Property[] property) {
        return isCheckBoxSelected(property,null);
    }

    @Override
    public boolean isCheckBoxSelected(Property[] property, IHtmlObject top) {
        IHtmlObject[] objs=getCheckBox(property,top);

        boolean selected=((ICheckBox)objs[0]).isSelected();

        unregister(objs);

        return selected;
    }

    public boolean isCheckBoxSelected(String propertyKey, Object value,int index){
        IHtmlObject[] objs=getCheckBox(propertyKey,value);

        boolean selected=((ICheckBox)objs[index]).isSelected();

        unregister(objs);

        return selected;
    }

    /**
     * Tick off the check box matching the given property
     * @param propertyKey
     * @param value
     */
    @Override
    public void unSelectCheckBox(String propertyKey, Object value) {
        unSelectCheckBox(propertyKey, value, 0);
    }

    /**
     * Tick off the check box matching the given property
     * @param propertyKey
     * @param value
     * @param index
     */
    @Override
    public void unSelectCheckBox(String propertyKey, Object value, int index) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        unSelectCheckBox(p, index);
    }

    /**
     * Tick off the check box matching the given property
     * @param propertyKey
     * @param value
     * @param index
     * @param top - the top level TestObject start searching from
     */
    @Override
    public void unSelectCheckBox(String propertyKey, Object value, int index, IHtmlObject top){
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        unSelectCheckBox(p, index, top);
    }

    /**
     * Tick off the check box matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey1
     * @param value1
     */
    @Override
    public void unSelectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2){
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        unSelectCheckBox(p, 0);
    }

    /**
     * Tick off the check box matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey1
     * @param value1
     * @param index
     */
    @Override
    public void unSelectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, int index){
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        unSelectCheckBox(p, index);
    }

    /**
     * Tick off the check box matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey1
     * @param value1
     * @param index
     * @param top - the top level GuiTestObject starting search from.
     */
    @Override
    public void unSelectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, int index, IHtmlObject top){
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        unSelectCheckBox(p, index, top);
    }

    /**
     * Tick off the check box matching the given property
     * @param property
     * @param index
     */
    @Override
    public void unSelectCheckBox(Property[] property, int index){
        unSelectCheckBox(property, index, (IHtmlObject)null);
    }

    /**
     * Tick off the check box matching the given property
     * @param property
     * @param index
     */
    @Override
    public void unSelectCheckBox(Property[] property, int index, IHtmlObject top){
        IHtmlObject[] objs=getCheckBox(property,top);
//		boolean selected=false;
        if(objs!=null && objs.length>0) {
            ((ICheckBox)objs[index]).deselect();
//			selected=((ICheckBox)objs[index]).isSelected();
        }
        unregister(objs);
    }

    @Override
    public void unSelectCheckBox(Property[] property) {
        unSelectCheckBox(property, 0);
    }

    /**
     * Tick on the check box matching the given property
     * @param propertyKey
     * @param value
     */
    @Override
    public void selectCheckBox(String propertyKey, Object value) {
        selectCheckBox(propertyKey, value, false);
    }

    @Override
    public void selectCheckBox(String propertyKey, Object value,boolean forced) {
        selectCheckBox(propertyKey, value, 0,forced);
    }

    /**
     * Tick on the check box matching the given property
     * @param propertyKey
     * @param value
     * @param index
     */
    @Override
    public void selectCheckBox(String propertyKey, Object value, int index) {
        selectCheckBox(propertyKey,value, index,false);
    }

    @Override
    public void selectCheckBox(String propertyKey, Object value, int index,boolean forced) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);

        selectCheckBox(p, index,forced);
    }

    /**
     * Tick on the check box matching the given property
     * @param propertyKey
     * @param value
     * @param index
     * @param top - the top level GuiTestObject starting the search from
     */
    @Override
    public void selectCheckBox(String propertyKey, Object value, int index, IHtmlObject top) {
        selectCheckBox(propertyKey,value, index,false, top);
    }

    @Override
    public void selectCheckBox(String propertyKey, Object value, int index, boolean forced,IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);

        selectCheckBox(p, index, forced,top);
    }

    /**
     * Tick on the check box matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     */
    @Override
    public void selectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        selectCheckBox(propertyKey1, value1, propertyKey2, value2, 0,false);
    }

    @Override
    public void selectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, boolean forced) {
        selectCheckBox(propertyKey1, value1, propertyKey2, value2, 0,forced);
    }

    /**
     * Tick on the check box matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param index
     */
    @Override
    public void selectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, int index) {
        selectCheckBox(propertyKey1, value1, propertyKey2, value2, index,false);
    }

    @Override
    public void selectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, int index,boolean forced) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        selectCheckBox(p, index,forced);
    }

    /**
     * Tick on the check box matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param index
     * @param top - the top level GuiTestObject starting the search from
     */
    @Override
    public void selectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, int index, IHtmlObject top) {
        selectCheckBox(propertyKey1, value1, propertyKey2, value2, index,false,top);
    }

    @Override
    public void selectCheckBox(String propertyKey1, Object value1,String propertyKey2, Object value2, int index, boolean forced,IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        selectCheckBox(p, index, forced,top);
    }

    /**
     * Tick on the check box matching the given property
     * @param property
     */
    @Override
    public void selectCheckBox(Property[] property) {
        selectCheckBox(property, 0,false);
    }

    @Override
    public void selectCheckBox(Property[] property,boolean forced) {
        selectCheckBox(property, 0,forced);
    }

    /**
     * Tick on the check box matching the given property
     * @param property
     * @param index
     */
    @Override
    public void selectCheckBox(Property[] property, int index) {
        selectCheckBox(property, index, false,(IHtmlObject)null);
    }

    @Override
    public void selectCheckBox(Property[] property, int index,boolean forced) {
        selectCheckBox(property, index, forced,(IHtmlObject)null);
    }

    @Override
    public void selectCheckBox(Property[] property, int index, IHtmlObject top) {
        selectCheckBox(property, index, false,top);
    }

    @Override
    public void selectCheckBox(Property[] property, int index, boolean forced,IHtmlObject top) {
        IHtmlObject[] objs=getCheckBox(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }

        boolean verify = Boolean.parseBoolean(TestProperty.getProperty("verifySelectForSupportscript", "false"));
        if(objs!=null && objs.length>0) {
            ((ICheckBox)objs[index]).select();
            if(verify){//verify action perform correct for setup scripts
                waitExists(property);
                if(!isCheckBoxSelected(property,top)){
                    throw new ActionFailedException("Select checkbox failed.");
                }
            }
        } else if(forced) {
            throw new ItemNotFoundException("Failed to find checkbox with property: "+Property.propertyArrayToString(property));
        }
        unregister(objs);
    }

    @Override
    public void selectCheckBox(List<Property[]> property) {
        selectCheckBox(property,0);

    }

    @Override
    public void selectCheckBox(List<Property[]> property, int index) {
        selectCheckBox(property,index,false);

    }

    @Override
    public void selectCheckBox(List<Property[]> property, int index,boolean forced) {
        IHtmlObject[] objs=getCheckBox(property);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>0) {
            ((ICheckBox)objs[index]).select();
            if(forced){//verify action perform correct for setup scripts
                IHtmlObject[] cks = getCheckBox(property);
                if(!((ICheckBox)cks[index]).isSelected()){
                    throw new ActionFailedException("Select Check Box Failed.");
                }
                unregister(cks);
            }
        } else if(forced) {
            throw new ItemNotFoundException("Failed to find checkbox with property: "+Property.propertyToString(property));
        }
        unregister(objs);

    }

    @Override
    public void unSelectCheckBox(List<Property[]> property) {
        unSelectCheckBox(property,0);

    }

    @Override
    public void unSelectCheckBox(List<Property[]> property, int index) {
        unSelectCheckBox(property,index,false);
    }

    @Override
    public void unSelectCheckBox(List<Property[]> property, int index, boolean forced) {
        IHtmlObject[] objs = getCheckBox(property);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>0) {
            ((ICheckBox)objs[index]).deselect();
        } else if(forced) {
            throw new ItemNotFoundException("Failed to find checkbox with property: " + Property.propertyToString(property));
        }
        unregister(objs);
    }

    /**
     * Select first radio button matching the given property
     * @param propertyKey
     * @param value
     */
    @Override
    public void selectRadioButton(String propertyKey, Object value) {
        selectRadioButton(propertyKey,value,0);
    }

    /**
     * Select <param> index radio button matching the given property
     * @param propertyKey
     * @param value
     * @param index
     */
    @Override
    public void selectRadioButton(String propertyKey, Object value, int index) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);

        selectRadioButton(p, index);
    }

    /**
     * Select first radio button matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     */
    @Override
    public void selectRadioButton(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        selectRadioButton(propertyKey1,value1,propertyKey2,value2,0);
    }

    /**
     * Select <param> index radio button matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param index
     */
    @Override
    public void selectRadioButton(String propertyKey1, Object value1,String propertyKey2, Object value2, int index) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        selectRadioButton(p, index);
    }

    /**
     * Select <param> index radio button matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param index
     * @param forced - if true and Object not found, throw exception
     */
    @Override
    public void selectRadioButton(String propertyKey1, Object value1,String propertyKey2, Object value2,boolean forced, int index) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        selectRadioButton(p, forced,index);
    }


    /**
     * Select <param> index radio button matching the given property
     * @param property
     * @param index
     */
    @Override
    public void selectRadioButton(Property[] property, int index) {
        selectRadioButton(property, false,index,(IHtmlObject)null);
    }

    /**
     * Select <param> index radio button matching the given property
     * @param property
     * @param index
     * @param forced - if true and Object not found, throw exception
     */
    @Override
    public void selectRadioButton(Property[] property, boolean forced, int index) {
        selectRadioButton(property, forced,index,(IHtmlObject)null);
    }

    @Override
    public void selectRadioButton(Property[] property, boolean forced,int index, IHtmlObject top) {
        IHtmlObject[] objs=getRadioButton(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        boolean verify = Boolean.parseBoolean(TestProperty.getProperty("verifySelectForSupportscript", "false"));
        if(objs!=null && objs.length>index) {
            ((IRadioButton)objs[index]).select();
            unregister(objs);
            if(verify){
                waitExists(property);
                if(!isRadioButtonSelected(property, top)){
                    throw new ActionFailedException("Select radio button failed.");
                }
            }
        }else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find radio button," +Property.propertyToString(property)+", the index you supplied is '"+index+"'");
        }

    }

    @Override
    public void setPasswordField(Property[] property, String text) {
        setPasswordField(property, text, false);
    }

    public void setFileField(String propertyKey, Object value, String text) {
        setFileField(propertyKey, value, text, false);
    }
    @Override
    public void setTextArea(String propertyKey, Object value, String text) {
        setTextArea(propertyKey, value, text, false);
    }


    /**
     * Get the text value of the TextField matching the given property
     * @param propertyKey
     * @param value
     * @return
     */
    @Override
    public String getTextFieldValue(String propertyKey, Object value) {
        Property[] p=Property.toPropertyArray(propertyKey, value);
        return getTextFieldValue(p);
    }

    /**
     * Get the text value of the TextField matching the given property
     * @param propertyKey
     * @param value
     * @param top - search the object from
     * @return
     */
    @Override
    public String getTextFieldValue(String propertyKey1, Object value1,String propertyKey2, Object value2){
        Property[] p=Property.toPropertyArray(propertyKey1, value1, propertyKey2, value2);
        return getTextFieldValue(p);
    }

    /**
     * Get the text value of the TextField matching the given property
     * @param property - the property array
     * @return
     */
    @Override
    public String getTextFieldValue(Property[] property){
        return getTextFieldValue(property,(IHtmlObject)null);
    }

    /**
     * Get the text value of the TextField matching the given property
     * @param property - the property array
     * @param top - search the object from
     * @return
     */
    @Override
    public String getTextFieldValue(Property[] property, IHtmlObject top) {
        return getTextFieldValue(property,0,top);
    }

    /**
     * Get the text value of the TextField matching the given property
     * @param property - the property array
     * @param objectIndex - index of current text field
     * @param top - search the object from
     * @return
     */
    @Override
    public String getTextFieldValue(Property[] property,int objectIndex, IHtmlObject top){
        IHtmlObject[] objs=getTextField(property,top);

        String text=null;
        if(objs!=null && objs.length>0) {
            text=((IText)objs[objectIndex]).getText();
            unregister(objs);
        } else {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text field = "+Property.propertyToString(property));
        }
        return text;
    }

    /**
     * Get the text value of the TextField matching the given property
     * @param propertyKey
     * @param value
     * @param objectIndex - the needed TextField object index
     */
    public String getTextFieldValue(String propertyKey, Object value, int objectIndex) {
        Property property[] = new Property[1];
        property[0] = new Property(propertyKey, value);

        return getTextFieldValue(property, objectIndex);
    }

    /**
     * Get the text value of the TextField matching the given property
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param objectIndex - the needed TextField object index
     */
    @Override
    public String getTextFieldValue(String propertyKey1, Object value1, String propertyKey2, Object value2, int objectIndex) {
        Property[] property = new Property[2];
        property[0] = new Property(propertyKey1, value1);
        property[1] = new Property(propertyKey2, value2);

        return getTextFieldValue(property, objectIndex);
    }

    /**
     * Get the text of the TextField matching the given property
     * @param property
     * @param objectIndex - the needed TextField object index
     */
    public String getTextFieldValue(Property[] property, int objectIndex) {
        IHtmlObject objs[] = getTextField(property, null);

        String text = null;
        if(objs != null && objs.length > objectIndex) {
            text = ((IText)objs[objectIndex]).getText();
            unregister(objs);
        } else {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text field="+Property.propertyToString(property));
        }

        return text;
    }

    /**
     * Set the text value for the 1st TextField with the given property.
     * @param propertyKey
     * @param value
     * @param text
     */
    @Override
    public void setTextField(String propertyKey, Object value, String text) {
        setTextField(propertyKey, value, text, false, 0);
    }

    /**
     * Set the text value for the 1st TextField with the given property.
     * @param propertyKey
     * @param value
     * @param text
     * @param forced
     */
    @Override
    public void setTextField(String propertyKey, Object value, String text,	boolean forced) {
        this.setTextField(propertyKey, value, text, forced,0);
    }

    /**
     * Set the text value for TextField with the given property.
     * @param propertyKey
     * @param value
     * @param text
     * @param index
     */
    @Override
    public void setTextField(String propertyKey, Object value, String text,	int index) {
        setTextField(propertyKey, value, text, false, index);
    }

    /**
     * Set the text value for TextField with the given property.
     * @param property
     * @param text
     * @param index
     */
    @Override
    public void setTextField(Property[] property, String text, int index) {
        setTextField(property, text, false, index);
    }

    @Override
    public void setTextField(String propertyKey, Object value, String text,	int index, IText.Event... event) {
        setTextField(propertyKey, value, text, false, index,event);
    }

    /**
     * Set the text value for TextField with the given property.
     * @param propertyKey
     * @param value
     * @param text
     * @param forced
     * @param index
     */
    @Override
    public void setTextField(String propertyKey, Object value, String text,	boolean forced, int index) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        setTextField(p, text, forced, index);
    }

    @Override
    public void setTextField(String propertyKey, Object value, String text,	boolean forced, int index, IText.Event... event) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        setTextField(p, text, forced, index,event);
    }

    /**
     * Set the text value for TextField with the given property.
     * @param propertyKey
     * @param value
     * @param text
     * @param forced
     * @param index
     * @param top - the top level GuiTestObject searching from
     */
    @Override
    public void setTextField(String propertyKey, Object value, String text,	boolean forced, int index, IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        setTextField(p, text, forced, index, top);
    }

    @Override
    public void setTextField(String propertyKey, Object value, String text,	boolean forced, int index, IHtmlObject top, IText.Event... event) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        setTextField(p, text, forced, index, top, event);
    }

    /**
     * Set the text value for TextField with the given property.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param text
     * @param forced
     * @param index
     */
    @Override
    public void setTextField(String propertyKey1, Object value1,String propertyKey2, Object value2, String text, boolean forced,int index) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        setTextField(p, text, forced, index);
    }

    @Override
    public void setTextField(String propertyKey1, Object value1,String propertyKey2, Object value2, String text, boolean forced,int index, IText.Event... event) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        setTextField(p, text, forced, index, event);
    }

    /**
     * Set the text value for TextField with the given property.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param text
     * @param forced
     * @param index
     * @param top - the top level GuiTestObject searching from
     */
    @Override
    public void setTextField(String propertyKey1, Object value1,String propertyKey2, Object value2, String text, boolean forced,int index, IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        setTextField(p, text, forced, index, top);
    }

    @Override
    public void setTextField(String propertyKey1, Object value1,String propertyKey2, Object value2, String text, boolean forced,int index, IHtmlObject top, IText.Event... event) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        setTextField(p, text, forced, index,top, event);
    }

    @Override
    public void setTextField(String propertyKey1, Object value1, String text,IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey1, value1);
        setTextField(p, text, false, 0, top);
    }

    @Override
    public void setTextField(String propertyKey1, Object value1, String text, IHtmlObject top, IText.Event... event) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey1, value1);
        setTextField(p, text, false, 0,top, event);
    }

    @Override
    public void setTextField(String propertyKey, Object value, String text,	IText.Event... event) {
        setTextField(propertyKey,value,text,null,event);

    }

    @Override
    public void setTextField(Property[] property, String text) {
        setTextField(property,text,false,0);

    }

    /**
     * Set the text value for TextField with the given property.
     * @param property
     * @param text
     * @param forced
     * @param index
     */
    @Override
    public void setTextField(Property[] property, String text, boolean forced,int index) {
        setTextField(property, text, forced, index, (IHtmlObject)null);
    }

    @Override
    public void setTextField(Property[] property, String text, boolean forced,int index, IText.Event... event) {
        setTextField(property, text, forced, index, (IHtmlObject)null, event);
    }

    @Override
    public void setTextField(Property[] property, String text, boolean forced,int index, IHtmlObject top) {
        setTextField(property, text, forced, index, top, (IText.Event)null);
    }

    @Override
    public void setTextField(Property[] property, String text, boolean forced,int index, IHtmlObject top, IText.Event... event) {
        setTextField(property, text, forced,false,index,  top, event);
    }

    @Override
    public void setTextField(Property[] property, String text, boolean forced,boolean forcedToSetReadOnly,int index, IHtmlObject top, IText.Event... event) {
        IHtmlObject[] objs=getTextField(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>index) {
            if(event!=null && event.length>0 && event[0]!=null)
                ((IText)objs[index]).setText(text,forcedToSetReadOnly,event);
            else
                ((IText)objs[index]).setText(text,forcedToSetReadOnly);
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text field, "+Property.propertyToString(property)+", the value you supplied is '"+text+"'");
        }
    }

    public void setTextField(List<Property[]> list, String text) {
        setTextField(list, text, false);
    }

    public void setTextField(List<Property[]> list, String text, boolean forced) {
        setTextField(list, text, forced, 0);
    }

    public void setTextField(List<Property[]> list, String text, boolean forced, int index) {
        setTextField(list, text, forced, false,index);
    }

    public void setTextField(List<Property[]> list, String text, boolean forced, boolean forcedToSetReadOnly,int index) {
        IHtmlObject objs[] = this.getHtmlObject(list);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>index ) {
            ((IText)objs[index]).setText(text,forcedToSetReadOnly);
            unregister(objs);
            if(forced){
                IHtmlObject[] inputs = getHtmlObject(list);
                unregister(inputs);
            }
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text field, "+Property.propertyToString(list)+", the value you supplied is '"+text+"'");
        }
    }

    @Override
    public void setCalendarField(Property[] property, String text) {
        setCalendarField(property,text,0);
    }

    @Override
    public void setCalendarField(Property[] property, String text, int index){
        setCalendarField(property,text,index,null);
    }

    @Override
    public void setCalendarField(Property[] property, String text, int index, IHtmlObject top){
        setCalendarField(property,text,false,index,top);
    }

    @Override
    public void setCalendarField(Property[] property, String text, boolean forced) {
        setCalendarField(property,text,forced,0);
    }

    @Override
    public void setCalendarField(Property[] property, String text, boolean forced,int index) {
        setCalendarField(property,text,forced,index,null);
    }

    @Override
    public void setCalendarField(Property[] property, String text, boolean forced,int index, IHtmlObject top) {
        setCalendarField(property,text,forced,false,index,top);
    }

    @Override
    public void setCalendarField(Property[] property, String text, boolean forced,boolean forcedToSetReadOnly,int index, IHtmlObject top) {
        setTextField(property,text,forced,forcedToSetReadOnly,index,top,IText.Event.LOSEFOCUS);
    }

    @Override
    public void setCalendarField(String propertyKey, Object value, String text) {
        setCalendarField(propertyKey, value, text, 0);

    }

    @Override
    public void setCalendarField(String propertyKey, Object value, String text,
                                 boolean forced) {
        setCalendarField(propertyKey, value, text, forced, 0);

    }

    @Override
    public void setCalendarField(String propertyKey, Object value, String text,
                                 int index) {
        setCalendarField(propertyKey, value, text, false, index);
    }

    @Override
    public void setCalendarField(String propertyKey, Object value, String text,
                                 boolean forced, int index) {
        setCalendarField(propertyKey, value, text, forced, index, null);

    }

    @Override
    public void setCalendarField(String propertyKey, Object value, String text,
                                 boolean forced, int index, IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        setCalendarField(p, text, forced, index, top);
    }

    @Override
    public void setPasswordField(String propertyKey, Object value, String text,	boolean forced) {
        setPasswordField(Property.toPropertyArray(propertyKey,value),text,forced);

    }

    @Override
    public void setPasswordField(String propertyKey, Object value, String text) {
        setPasswordField(propertyKey,value,text,false);

    }
    @Override
    public void setPasswordField(Property[] property, String text,boolean forced) {
        IHtmlObject[] objs=getPasswordField(property);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>0) {
            ((IText)objs[0]).setText(text);
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the password field," +Property.propertyToString(property));
        }

    }

    public void setFileField(String propertyKey, Object value, String text,	boolean forced){
        IHtmlObject[] objs=getFileField(propertyKey,value);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>0) {
            ((IText)objs[0]).setText(text);
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the file field ["+propertyKey+" = "+value+" ],the value you supplied is '"+text+"'");
        }
    }

    @Override
    public void setTextArea(String propertyKey, Object value, String text, boolean forced, int index, IHtmlObject top) {
        setTextArea(Property.toPropertyArray(propertyKey, value), text,forced,index, top);
    }

    @Override
    public void setTextArea(String propertyKey, Object value, String text, boolean forced, int index){
        IHtmlObject[] objs=getTextArea(propertyKey,value);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>index) {
            ((IText)objs[index]).setText(text);
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text area ["+propertyKey+" = "+value +" ],the value you supplied is '"+text+"'");
        }
    }

    @Override
    public void setTextArea(String propertyKey, Object value, String text,	boolean forced) {
        this.setTextArea(propertyKey,value, text, forced, 0);
    }

    @Override
    public void setTextArea(Property[] properties, String text){
        setTextArea(properties,text,false);
    }

    @Override
    public void setTextArea(Property[] properties, String text, boolean forced){
        setTextArea(properties,text,forced,0);
    }
    @Override
    public void setTextArea(Property[] properties, String text, boolean forced, int index){
        setTextArea(properties,text,forced,index,null);
    }
    @Override
    public void setTextArea(Property[] properties, String text, boolean forced, int index, IHtmlObject top){
        IHtmlObject objs[] = getTextArea(properties, top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }

        if(objs != null && objs.length > index) {
            ((IText)objs[index]).setText(text);
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text area. "+Property.propertyToString(properties)+", the value you supplied is '"+text+"'");
        }
    }

    public String getTextAreaValue(String propertyKey, Object propertyValue) {
        return getTextAreaValue(Property.toPropertyArray(propertyKey, propertyValue));
    }

    public String getTextAreaValue(String propertyKey1, Object propertyValue1, String propertyKey2, Object propertyValue2) {
        return getTextAreaValue(Property.toPropertyArray(propertyKey1, propertyValue1, propertyKey2, propertyValue2));
    }

    public String getTextAreaValue(Property properties[]) {
        return getTextAreaValue(properties, null);
    }

    public String getTextAreaValue(Property properties[], IHtmlObject top) {
        return getTextAreaValue(properties, 0, top);
    }

    public String getTextAreaValue(Property properties[], int objectIndex, IHtmlObject top) {
        IHtmlObject objs[] = getTextArea(properties, top);

        String text = null;
        if(objs != null && objs.length > 0) {
            text = ((IText)objs[objectIndex]).getText();
            unregister(objs);
        } else {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the text area, "+Property.propertyToString(properties));
        }

        return text;
    }

    public void setIFrameTextField(Property[] property, String text, boolean forced, int index, IHtmlObject top) {
        IHtmlObject[] objs=getIFrameTextField(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>index) {
            objs[index].click();
            inputKey(KeyInput.get(KeyInput.BACKSPACE,"a"),KeyInput.get(KeyInput.BACKSPACE),KeyInput.get(text));
//			inputKey("^a{BKSP}");
//			inputKey(text);
        } else if(forced) {
            throw new ItemNotFoundException("Failed to find the IFrameText Field object " +Property.propertyToString(property));
        }
    }

    /**
     * Retrieve the current selection value of the dropdown list matching the given property
     * @param propertyKey - the property key
     * @param value - the property value
     * @return
     */
    public String getDropdownListValue(String propertyKey, Object value) {
        return getDropdownListValue(propertyKey, value, 0);
    }

    /**
     * Retrieve the current selection value of the dropdown list matching the given property
     * @param propertyKey - the property key
     * @param value - the property value
     * @return - the current selection, null if dropdown list doesn't exist
     */
    @Override
    public String getDropdownListValue(String propertyKey, Object value,int objectIndex) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return getDropdownListValue(p, objectIndex);
    }

    /**
     * Retrieve the current selection value of the dropdown list matching the given property
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @return - the current selection, null if dropdown list doesn't exist
     */
    @Override
    public String getDropdownListValue(String propertyKey1, Object value1,String propertyKey2, Object value2, int objectIndex) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        return getDropdownListValue(p, objectIndex);
    }

    /**
     * Retrieve the elements of the dropdown list with the given property
     * @param propertyKey - the property key
     * @param value - the property value
     * @return - element list
     */
    @Override
    public List<String> getDropdownElements(String propertyKey,	Object value) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return getDropdownElements(p);
    }

    /**
     * Retrieve the elements of the dropdown list with the given properties
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @return - element list
     */
    @Override
    public List<String> getDropdownElements(String propertyKey1,Object value1, String propertyKey2, Object value2) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        return getDropdownElements(p);
    }

    /**
     * Retrieve the elements of the dropdown list with the given properties
     * @param property - the array of property, except property ".class"
     * @return - element list
     */
    @Override
    public List<String> getDropdownElements(Property[] property) {
        return getDropdownElements(property,(IHtmlObject)null);
    }
    @Override
    public List<String> getDropdownElements(Property[] property, IHtmlObject top) {
        IHtmlObject[] objs=	getDropdownList(property,top);
        List<String> options=null;

        if(objs!=null && objs.length>0) {
            options= ((ISelect) objs[0]).getAllOptions();
        } else {
            logger.debug("Failed to find the dropdown list with property="+Property.propertyToString(property));
            options=new ArrayList<String>();
        }
        unregister(objs);

        return options;
    }

//	/**
//	 * Get all values of the dropdown list elements
//	 * Here the values mean that the element's attribute named "value", not the element's text.
//	 * <option value="">
//	 * @param property
//	 * @param top
//	 * @return
//	 */
//	public List<String> getDropdownElementsValues(String propertyKey,	Object value) {
//		Property[] p = new Property[1];
//		p[0] = new Property(propertyKey, value);
//		return getDropdownElementsValues(p);
//	}
//
//	public List<String> getDropdownElementsValues(String propertyKey1,Object value1, String propertyKey2, Object value2) {
//		Property[] p = new Property[2];
//		p[0] = new Property(propertyKey1, value1);
//		p[1] = new Property(propertyKey2, value2);
//		return getDropdownElementsValues(p);
//	}
//
//	public List<String> getDropdownElementsValues(Property[] property) {
//		return getDropdownElementsValues(property,(IHtmlObject)null);
//	}
//
//	public List<String> getDropdownElementsValues(Property[] property, IHtmlObject top) {
//		IHtmlObject[] objs=	getDropdownList(property,top);
//		List<String> options=null;
//
//		if(objs!=null && objs.length>0) {
//			options= ((ISelect) objs[0]).getAllOptionsValues();
//		} else {
//			logger.debug("Failed to find the dropdown list with property="+Property.propertyToString(property));
//			options=new ArrayList<String>();
//		}
//		unregister(objs);
//
//		return options;
//	}

    @Override
    public String getDropdownListValue(Property[] property, int objectIndex,IHtmlObject top){
        IHtmlObject[] objs=getDropdownList(property,top);
        String selectedText=null;
        if(objs!=null && objs.length>objectIndex) {
            selectedText=((ISelect)objs[objectIndex]).getSelectedText();
            unregister(objs);
        }else {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the dropdown list with property="+Property.propertyToString(property));
        }

        return selectedText;
    }

    @Override
    public String getDropdownListValue(Property[] property, int objectIndex) {
        return this.getDropdownListValue(property, objectIndex, (IHtmlObject)null);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * when dropdown list doesn't exist, do nothing.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, String item) {
        selectDropdownList(propertyKey, value, item, false);
    }

    /**
     * Select the given text in the dropdown list with the given property.
     * @param propertyKey -the property key
     * @param value -the property value
     * @param text
     * @param index
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, String text,int index ){
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p,text, false,index,(IHtmlObject)null);
    }

    /**
     * Select the given text in the dropdown list with the given property.
     * @param property -the property
     * @param text
     * @param index
     */
    @Override
    public void selectDropdownList(Property[] property, String text,int index ){
        selectDropdownList(property, text, false,index,(IHtmlObject)null);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value,String item, boolean forced) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p, item, forced);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject searching from
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value,String item, boolean forced, IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p, item, forced, top);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * when dropdown list doesn't exist, do nothing.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, String item) {
        selectDropdownList(propertyKey1, value1, propertyKey2, value2, item, false);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, String item, boolean forced) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        selectDropdownList(p, item, forced);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject search from
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, String item, boolean forced,IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        selectDropdownList(p, item, forced, top);
    }

    @Override
    public void selectDropdownList(Property[] property, String item) {
        selectDropdownList(property,item,false);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * @param property - array of properties except property ".class"
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     */
    @Override
    public void selectDropdownList(Property[] property, String item,boolean forced) {
        selectDropdownList(property, item, forced, (IHtmlObject)null);
    }

    /**
     * Select the <param> item in the dropdown list with the given property.
     * @param property - array of properties except property ".class"
     * @param item - the option name in the dropdown list. do nothing if item is empty.
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject search from
     */
    @Override
    public void selectDropdownList(Property[] property, String item,boolean forced, IHtmlObject top) {
        selectDropdownList(property, item, forced, 0, top);
    }

    @Override
    public void selectDropdownList(Property[] property, String item,boolean forced, int objectIndex, IHtmlObject top) {
        if(StringUtil.isEmpty(item)) {
            if(forced) {
                throw new ItemNotFoundException("Option to be selected is not specified."+Property.propertyToString(property));
            } else {
                return;
            }
        }

        IHtmlObject[] objs=getDropdownList(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        boolean verify = Boolean.parseBoolean(TestProperty.getProperty("verifySelectForSupportscript", "false"));

        if(objs!=null && objs.length>objectIndex && objs[objectIndex].isEnabled()) {
            ((ISelect) objs[objectIndex]).select(item);

            if(verify && !verifySelect(property, objectIndex, item, top)) {
                throw new ActionFailedException("Select dropdown list as option - '" + item + "' failed.");
            }

            unregister(objs);
        } else if(forced){
            unregister(objs);
            throw new ItemNotFoundException("Dropdown list not found/enabled/displayed,"+Property.propertyToString(property)+", the value you supplied is '"+item+"'");
        } else {
            logger.warn("Select action skipped due to dropdown list is not found/enabled/displayed.");
        }

    }

    /**
     * Select the drop down list with the corresponding item which has the same index in array
     * @param propertyKey
     * @param value
     * @param item
     * @param forced
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value,String[] item, boolean forced) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p, item, forced, (IHtmlObject)null);
    }

    @Override
    public void selectDropdownList(Property[] property, String[] item,boolean forced, IHtmlObject top) {
        selectDropdownList(property,item,item.length>1,forced,top);
    }

    @Override
    public void selectDropdownList(Property[] property, String[] item, boolean multipleSelection, boolean forced, IHtmlObject top) {
        IHtmlObject[] objs=getDropdownList(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if((objs==null || objs.length<1) && forced) {
            unregister(objs);
            throw new ItemNotFoundException("Dropdown list not found,"+Property.propertyToString(property)+", the value you supplied is '"+StringUtil.arrayToString(item)+"'");
        } else if((objs!=null && objs.length>0)){
            if(multipleSelection) {
                ((ISelect) objs[0]).select(item);
            } else {
                int size=item.length>objs.length?objs.length:item.length;

                for(int i=0;i<size;i++) {
                    ((ISelect) objs[i]).select(item[i]);
                }
            }
        }
        unregister(objs);
    }

    /**
     * Select the drop down list with the corresponding item which has the same index in array
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param item
     * @param forced
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, String[] item, boolean forced) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        selectDropdownList(p, item, forced, (IHtmlObject)null);
    }

    /**
     * Select the drop down list with the corresponding item which has the same index in array
     * @param property - object properies array
     * @param item - array of items
     * @param forced - throw exception if drop down list not found
     */
    @Override
    public void selectDropdownList(Property[] property, String[] item,	boolean forced) {
        selectDropdownList(property, item, forced, (IHtmlObject)null);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * when dropdown list doesn't exist, do nothing.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of option in the dropdown list
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, int index) {
        selectDropdownList(propertyKey, value, index, 0, false,	(IHtmlObject)null);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * when dropdown list doesn't exist, do nothing.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of option in the dropdown list
     * @param objectIndex - the index of text object if more than one drop down lists available
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, int index,int objectIndex) {
        selectDropdownList(propertyKey, value, index, objectIndex, false,(IHtmlObject)null);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of option in the dropdown list
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, int index,	boolean forced) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p, index, forced);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of option in the dropdown list
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject starting the search from
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, int index,	boolean forced, IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p, index, 0, forced, top);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of option in the dropdown list
     * @param objectIndex - the index of test object if more than one drop down list available
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject starting the search from
     */
    @Override
    public void selectDropdownList(String propertyKey, Object value, int index,	int objectIndex, boolean forced, IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        selectDropdownList(p, index, objectIndex, forced, top);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * when dropdown list doesn't exist, do nothing.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param index - the index of option in the dropdown list
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, int index) {
        selectDropdownList(propertyKey1, value1, propertyKey2, value2, index,false);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param index - the index of option in the dropdown list
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, int index, boolean forced) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        selectDropdownList(p, index, forced);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param index - the index of option in the dropdown list
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject starting the search from
     */
    @Override
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, int index, boolean forced,IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        selectDropdownList(p, index, 0, forced, top);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param index - the index of option in the dropdown list
     * @param objectIndex - the index of test object if more than one drop down lists found
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject starting the search from
     */
    public void selectDropdownList(String propertyKey1, Object value1,String propertyKey2, Object value2, int index, int objectIndex,boolean forced, IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        selectDropdownList(p, index, objectIndex, forced, top);
    }

    @Override
    public void selectDropdownList(Property[] property, int index) {
        selectDropdownList(property,index,false);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param property - array of properties except property ".class"
     * @param index - the index of option in the dropdown list
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     */
    public void selectDropdownList(Property[] property, int index,boolean forced) {
        selectDropdownList(property, index, 0, forced, (IHtmlObject)null);
    }

    /**
     * Select the <param> index item in the dropdown list with the given property.
     * @param property - array of properties except property ".class"
     * @param index - the index of option in the dropdown list
     * @param forced - when dropdown list doesn't exist, do nothing if fasle, throw exception if true
     * @param top - the top level GuiTestObject starting the search from
     */
    public void selectDropdownList(Property[] property, int index,boolean forced, IHtmlObject top) {
        selectDropdownList(property, index, 0, forced, top);
    }

    @Override
    public void selectDropdownList(Property[] property, int index,int objectIndex, boolean forced, IHtmlObject top) {
        IHtmlObject[] objs=getDropdownList(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>objectIndex) {
            ((ISelect)objs[objectIndex]).select(index);
            unregister(objs);

        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Dropdown list not found, "+Property.propertyToString(property)+", the index you supplied is '"+index+"'");
        }

    }

    /**
     * Check if the dropdown list contains the given option
     * @param propertyKey
     * @param value
     * @param option
     * @return
     */
    public boolean dropdownListContains(String propertyKey,Object value,Object option) {
        Property[] p=Property.toPropertyArray(propertyKey, value);
        return dropdownListContains(p,option);
    }

    /**
     * Check if the dropdown list contains the given option
     * @param property
     * @param option
     * @return
     */
    public boolean dropdownListContains(Property[] property, Object option){
        return dropdownListContains(property,option,(IHtmlObject)null);
    }

    /**
     * Check if the dropdown list contains the given option
     * @param property
     * @param option
     * @param top
     * @return
     */
    public boolean dropdownListContains(Property[] property, Object option, IHtmlObject top){
        return dropdownListContains(property,0,option,top);
    }

    /**
     * Check if the dropdown list contains the given option
     * @param property - the property of the dropdown list
     * @param objectIndex
     * @param option - the option either String or RegularExpression
     * @param top - search the dropdown list from
     * @return
     */
    public boolean dropdownListContains(Property[] property, int objectIndex,Object option, IHtmlObject top){
        List<String> list=this.getDropdownElements(property,top);
        Iterator<String> iter=list.iterator();
        if(option instanceof RegularExpression) {
            while(iter.hasNext()) {
                String text=iter.next();
                boolean matched=((RegularExpression) option).match(text);
                if(matched) {
                    return true;
                }

            }
        }else if (option instanceof String) {
            while(iter.hasNext()) {
                String text=iter.next();
                boolean matched=text.equalsIgnoreCase((String)option);
                if(matched) {
                    return true;
                }

            }
        } else {
            throw new ItemNotFoundException("Option can only be either String or RegularExpression");
        }


        return false;
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * Do nothing if the object doesn't exist.
     * If more than one objects found matching the property, click the 1st one.
     * @param propertyKey - the property key
     * @param value - the property value
     */
    public void clickGuiObject(String propertyKey, Object value) {
        clickGuiObject(propertyKey, value, false);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * Do nothing if the object doesn't exist.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey, Object value, int index) {
        clickGuiObject(propertyKey, value, false, index);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * If more than one objects found matching the property, click the 1st one.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickGuiObject(String propertyKey, Object value, boolean forced) {
        clickGuiObject(propertyKey, value, forced, 0);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey, Object value,
                               boolean forced, int index) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        clickGuiObject(p, forced, index);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     * @param top - the top level GuiTestObject starting the search from
     */
    public void clickGuiObject(String propertyKey, Object value, boolean forced, int index, IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        clickGuiObject(p, forced, index, top);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * Do nothing if the object doesn't exist.
     * If more than one objects found matching the property, click the 1st one.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        clickGuiObject(propertyKey1, value1, propertyKey2, value2, false);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys. If the object doesn't exist, do nothing
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey1, Object value1,	String propertyKey2, Object value2, int index) {
        clickGuiObject(propertyKey1, value1, propertyKey2, value2, false, index);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys. If more than one objects found matching the property, click the 1st one.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2, boolean forced) {
        clickGuiObject(propertyKey1, value1, propertyKey2, value2, forced, 0);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2, boolean forced, int index) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        clickGuiObject(p, forced, index);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     * @param top - the top level GuiTestObject starting the search from
     */
    public void clickGuiObject(String propertyKey1, Object value1,	String propertyKey2, Object value2, boolean forced, int index,IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        clickGuiObject(p, forced, index, top);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given properties.
     * If more than one objects found, click the 1st one.
     * Do nothing if the object doesn't exist
     * @param property - the properties of the Gui Object
     */
    public void clickGuiObject(Property[] property) {
        clickGuiObject(property, false, 0);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given properties.
     * If more than one objects found, click the 1st one.
     * @param property - the properties of the Gui Object
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickGuiObject(Property[] property, boolean forced) {
        clickGuiObject(property, forced, 0);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given properties.
     * Do nothing if the object doesn't exist
     * @param property - the properties of the Gui Object
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(Property[] property, int index) {
        clickGuiObject(property, false, index);
    }

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys
     * @param property - the properties of the Gui Object
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(Property[] property, boolean forced, int index) {
        clickGuiObject(property, forced, index, (IHtmlObject)null);
    }

    public void clickGuiObject(List<Property[]> list) {
        clickGuiObject(list,false,0);
    }

    public void clickGuiObject(List<Property[]> list, boolean forced, int index) {
        IHtmlObject[] objs=this.getHtmlObject(list);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>index ) {
            objs[index].click();
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            StringBuffer msg=new StringBuffer();
            msg.append("List: ");
            for(int i=0;i<list.size();i++) {
                msg.append("#");
                msg.append(i);
                msg.append("-");
                msg.append(Arrays.toString(list.get(i)));
                msg.append(" ");
            }
            throw new ItemNotFoundException("Failed to find the IHtmlObject with "+msg);
        }

    }

    public void clickGuiObject(Property[] property, boolean forced, int index,IHtmlObject top) {
        IHtmlObject[] objs=this.getHtmlObject(property,top);
        if(!forced){
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs!=null && objs.length>index ) {
            objs[index].click();
            unregister(objs);
        } else if(forced) {
            unregister(objs);

            throw new ItemNotFoundException("Failed to find the IHtmlObject with "+Arrays.toString(property));
        } else {
            logger.warn("Failed to find IHtmlObject with "+Arrays.toString(property));
        }

    }

    /**
     * Click the image area which has the same Href value as hrefValue.
     * @param imageProperties - image properties other than ".class"
     * @param areaHref - the area Href property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickImageArea(Property[] imageProperties, Object areaHref,boolean forced){
        clickImageArea(imageProperties,areaHref,forced,null);
    }

    /**
     * Click the image area which has the same Href value as hrefValue.
     * @param imageProperties - image properties other than ".class"
     * @param areaHref - the area Href property value
     */
    public void clickImageArea(Property[] imageProperties, Object areaHref) {
        clickImageArea(imageProperties,areaHref,false,null);
    }

    /**
     * Search all TestObjects matching the given property
     * @param propertyKey1 - the property key
     * @param value1 - the property value
     * @return
     */
    public IHtmlObject[] getHtmlObject(String propertyKey, Object value) {
        Property[] p=new Property[1];
        p[0]=new Property(propertyKey,value);

        return getHtmlObject(p);
    }

    /**
     * Search all TestObjects matching the given properties
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @return
     */
    public IHtmlObject[] getHtmlObject(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] property = new Property[2];
        property[0] = new Property(propertyKey1, value1);
        property[1] = new Property(propertyKey2, value2);

        return getHtmlObject(property, (IHtmlObject)null);
    }

    /**
     * Search all TestObjects matching the given properties
     * @param propertyKey1 - the property key
     * @param value1 - the property value
     * @param mapAble
     * @param top - the top level GuiTestObject starting the search from
     * @return
     */
    public IHtmlObject[] getHtmlObject(String propertyKey, Object value,IHtmlObject top) {
        Property[] property = new Property[1];
        property[0] = new Property(propertyKey, value);
        return getHtmlObject(property, top);
    }

    /**
     * Search all TestObjects matching the given properties
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param mapAble
     * @param top - the top level GuiTestObject starting the search from
     * @return
     */
    public IHtmlObject[] getHtmlObject(String propertyKey1, Object value1,String propertyKey2, Object value2,IHtmlObject top) {
        Property[] property = new Property[2];
        property[0] = new Property(propertyKey1, value1);
        property[1] = new Property(propertyKey2, value2);

        return getHtmlObject(property, top);
    }

    /**
     * Search all TestObjects matching the given properties
     * @param property - the array of property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getHtmlObject(Property[] property) {
        return getHtmlObject(property, (IHtmlObject)null);
    }

    /**
     * Search all Html.TABLE TestObjects matching the given properties
     * @param propertyKey
     * @param value
     * @param mapAble
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTableTestObject(String propertyKey, Object value) {
        Property[] property = new Property[2];
        property[0] = new Property(".class", "Html.TABLE");
        property[1] = new Property(propertyKey, value);

        return getTableTestObject(property);
    }

    /**
     * Search all Html.TABLE TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param mapAble
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTableTestObject(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] property = new Property[3];
        property[0] = new Property(".class", "Html.TABLE");
        property[1] = new Property(propertyKey1, value1);
        property[2] = new Property(propertyKey2, value2);

        return getTableTestObject(property);
    }

    /**
     * Search all Html.TABLE TestObjects matching the given properties
     * @param property
     * @param mapAble
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTableTestObject(Property[] property) {
        return getTableTestObject(property, (IHtmlObject)null);
    }

    /**
     * Search all Html.SELECT TestObjects matching the given properties
     * @param propertyKey
     * @param value
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getDropdownList(String propertyKey, Object value){
        Property[] property = new Property[1];
        property[0] = new Property(propertyKey, value);
        return getDropdownList(property);
    }

    /**
     * Search all Html.SELECT TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param mapAble
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getDropdownList(String propertyKey1, Object value1, String propertyKey2, Object value2){
        Property[] property = new Property[2];
        property[0] = new Property(propertyKey1, value1);
        property[1] = new Property(propertyKey2, value2);

        return getDropdownList(property);
    }

    /**
     * Search all Html.SELECT TestObjects matching the given properties
     * @param property
     * @param mapAble
     * @return - array of TestObjects found
     */
    @Override
    public IHtmlObject[] getDropdownList(Property[] property) {
        return getDropdownList(property,(IHtmlObject)null);
    }



    /**
     * Search all Html.SELECT TestObjects matching the given properties
     * @param propertyKey
     * @param value
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextField(String propertyKey, Object value) {
        Property[] property = new Property[1];
        property[0] = new Property(propertyKey, value);

        return getTextField(property);
    }

    /**
     * Search all Html.INPUT.text TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param mapAble
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextField(String propertyKey1, Object value1, String propertyKey2, Object value2){
        Property[] property = new Property[2];
        property[0] = new Property(propertyKey1, value1);
        property[1] = new Property(propertyKey2, value2);
        return getTextField(property);
    }

    /**
     * Search all Html.SELECT TestObjects matching the given properties
     * @param property
     * @param mapAble
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextField(Property[] property){
        return getTextField(property,(IHtmlObject)null);
    }

    public IHtmlObject[] getPasswordField(String propertyKey, Object value){
        Property[] p=Property.toPropertyArray(propertyKey, value);
        return getPasswordField(p);
    }

    /**
     * Search all html.INPUT.password TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getPasswordField(String propertyKey1, Object value1, String propertyKey2, Object value2){
        Property[] p=Property.toPropertyArray(propertyKey1, value1, propertyKey2, value2);
        return getPasswordField(p);
    }

    /**
     * Search all html.INPUT.password TestObjects matching the given properties
     * @param property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getPasswordField(Property[] property) {
        return getPasswordField(property,(IHtmlObject)null);
    }

    /**
     * Search all html.INPUT.password TestObjects matching the given properties
     * @param property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getPasswordField(Property[] property,IHtmlObject top){
        Property[] p=Property.concatPropertyArray(property,".class", "Html.INPUT.password");

        return getTextField(p, top);
    }

    /**
     * Search all Html.INPUT.file TestObjects matching the given properties
     */
    public IHtmlObject[] getFileField(String propertyKey, Object value){
        Property[] p=Property.toPropertyArray(propertyKey, value);
        return getFileField(p);
    }

    /**
     * Search all Html.INPUT.file TestObjects matching the given properties
     */
    public IHtmlObject[] getFileField(String propertyKey1, Object value1, String propertyKey2, Object value2){
        Property[] p=Property.toPropertyArray(propertyKey1, value1, propertyKey2, value2);
        return getPasswordField(p);
    }

    /**
     * Search all Html.INPUT.file TestObjects matching the given properties
     */
    public IHtmlObject[] getFileField(Property[] property) {
        return getFileField(property,(IHtmlObject)null);
    }

    /**
     * Search all Html.INPUT.file TestObjects matching the given properties
     */
    public IHtmlObject[] getFileField(Property[] property,IHtmlObject top){
        Property[] p=Property.concatPropertyArray(property,".class", "Html.INPUT.file");

        return getTextField(p, top);
    }

    /**
     * Search all text area TestObjects matching the given properties
     * @param propertyKey
     * @param value
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextArea(String propertyKey, Object value) {
        Property[] property = new Property[1];
        property[0] = new Property(propertyKey, value);

        return getTextArea(property);
    }

    /**
     * Search all text area TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextArea(String propertyKey1, Object value1, String propertyKey2, Object value2){
        Property[] property = new Property[2];
        property[0] = new Property(propertyKey1, value1);
        property[1] = new Property(propertyKey2, value2);
        return getTextArea(property);
    }

    /**
     * Search all text area TestObjects matching the given properties
     * @param property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextArea(Property[] property){
        return getTextArea(property,(IHtmlObject)null);
    }

    /**
     * Search all text area TestObjects matching the given properties
     * @param property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getTextArea(Property[] property,IHtmlObject top) {
        Property[] p=Property.concatPropertyArray(property,".class", "Html.TEXTAREA");
        return getHtmlObject(p,top);
    }

    /**
     * Search all Html.SELECT.checkbox TestObjects matching the given properties
     * @param propertyKey
     * @param value
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getCheckBox(String propertyKey, Object value) {
        Property[] p=Property.toPropertyArray(propertyKey, value);
        return getCheckBox(p);
    }

    /**
     * Search all Html.SELECT.checkbox TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getCheckBox(String propertyKey1, Object value1, String propertyKey2, Object value2) {
        Property[] p=Property.toPropertyArray(propertyKey1, value1, propertyKey2, value2);
        return getCheckBox(p);
    }

    /**
     * Search all Html.SELECT.checkbox TestObjects matching the given properties
     * @param property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getCheckBox(Property[] property){
        return getCheckBox(property,(IHtmlObject)null);
    }

    @Override
    public IHtmlObject[] getCheckBox(Property[] property,IHtmlObject top) {
        property=Property.concatPropertyArray(property, ".class", "Html.INPUT.checkbox");
        return getHtmlObject(property,top);
    }

    @Override
    public IHtmlObject[] getCheckBox(List<Property[]> property) {
        Property[] last=property.remove(property.size()-1);
        property.add(Property.concatPropertyArray(last, ".class", "Html.INPUT.checkbox"));
        return getHtmlObject(property);
    }

    /**
     * Search all Html.SELECT.radio TestObjects matching the given properties
     * @param propertyKey
     * @param value
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getRadioButton(String propertyKey, Object value) {
        Property[] p=Property.toPropertyArray(propertyKey, value);
        return getRadioButton(p);
    }

    /**
     * Search all Html.SELECT.radio TestObjects matching the given properties
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getRadioButton(String propertyKey1, Object value1, String propertyKey2, Object value2){
        Property[] p=Property.toPropertyArray(propertyKey1, value1, propertyKey2, value2);
        return getRadioButton(p);
    }

    /**
     * Search all Html.SELECT.radio TestObjects matching the given properties
     * @param property
     * @return - array of TestObjects found
     */
    public IHtmlObject[] getRadioButton(Property[] property){
        return getRadioButton(property,(IHtmlObject)null);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * @param propertyName
     * @param value
     * @return
     */
    public boolean checkHtmlObjectExists(String propertyKey, Object value) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return checkHtmlObjectExists(p);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * @param propertyName1
     * @param value1
     * @param propertyName2
     * @param value2
     * @return
     */
    public boolean checkHtmlObjectExists(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        return checkHtmlObjectExists(p);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param property
     * @return - true if test object exist, false otherwise.
     */
    public boolean checkHtmlObjectExists(Property[] property) {
        return checkHtmlObjectExists(property, null);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param propertyName
     * @param value
     * @param top
     * @return - true if test object exist, false otherwise.
     */
    @Override
    public boolean checkHtmlObjectExists(String propertyName, Object value,	IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyName, value);
        return checkHtmlObjectExists(p,top);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param propertyName1
     * @param value1
     * @param propertyName2
     * @param value2
     * @param top
     * @return - true if test object exist, false otherwise.
     */
    public boolean checkHtmlObjectExists(String propertyName1, Object value1,String propertyName2, Object value2, IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyName1, value1);
        p[1] = new Property(propertyName2, value2);
        return checkHtmlObjectExists(p,top);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param property
     * @param top
     * @return - true if test object exist, false otherwise.
     */
    public boolean checkHtmlObjectExists(Property[] property, IHtmlObject top) {
        return checkObjectExists(property,top);
    }

    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param list
     * @return - true if test object exist, false otherwise.
     */
    public boolean checkHtmlObjectExists(List<Property[]> list) {
        return checkObjectExists(list,null);
    }


    /**
     * This method finds all objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param property
     * @return - true if test object exist, false otherwise.
     */
    @SuppressWarnings("unchecked")
    private boolean checkObjectExists(Object property, IHtmlObject top) {
        IHtmlObject[] objs;
        if(property instanceof List) {
            List<Property[]> list=(List<Property[]>) property;
            objs=getHtmlObject(list);
        } else {
            objs=getHtmlObject((Property[])property,top);
        }

        boolean exists=objs!=null && objs.length>0;

        unregister(objs);
        return exists;
    }

    public boolean checkHtmlObjectEnabled(String propertyKey,Object value){
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return checkHtmlObjectEnabled(p);
    }

    public boolean checkHtmlObjectEnabled(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        return checkHtmlObjectEnabled(p);
    }

    public boolean checkHtmlObjectEnabled(Property[] property) {
        return checkObjectEnabled(property, null);
    }

    public boolean checkHtmlObjectEnabled(String propertyName, Object value,	IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyName, value);
        return checkHtmlObjectEnabled(p,top);
    }

    public boolean checkHtmlObjectEnabled(Property[] property, IHtmlObject top) {
        return checkObjectEnabled(property,top);
    }

    public boolean checkHtmlObjectEnabled(List<Property[]> list) {
        return checkObjectEnabled(list,null);
    }

    @SuppressWarnings("unchecked")
    protected boolean checkObjectEnabled(Object property, IHtmlObject top) {
        IHtmlObject[] objs;
        if(property instanceof List) {
            List<Property[]> list=(List<Property[]>) property;
            objs=getHtmlObject(list);
        } else {
            objs=getHtmlObject((Property[])property,top);
        }

        boolean enabled=false;

        if(objs!=null && objs.length>0) {
            for(IHtmlObject obj:objs) {
                if(obj.isEnabled()) {
                    enabled=true;
                    break;
                }
            }
        }

        unregister(objs);
        return enabled;
    }

    @Override
    public boolean checkHtmlObjectDisplayed(String propertyKey, Object value) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return checkHtmlObjectDisplayed(p);
    }

    /**
     * This method finds all visible objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * @param propertyName1
     * @param value1
     * @param propertyName2
     * @param value2
     * @return
     */
    @Override
    public boolean checkHtmlObjectDisplayed(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        return checkHtmlObjectDisplayed(p);
    }

    /**
     * This method finds all visible objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param property
     * @return - true if test object is visible, false otherwise.
     */
    @Override
    public boolean checkHtmlObjectDisplayed(Property[] property) {
        return checkObjectDisplayed(property, null);
    }

    /**
     * This method finds all visible objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param propertyName
     * @param value
     * @param top
     * @return - true if test object is visible, false otherwise.
     */
    @Override
    public boolean checkHtmlObjectDisplayed(String propertyName, Object value,	IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyName, value);
        return checkObjectDisplayed(p,top);
    }

    /**
     * This method finds all visible objects in HtmlDocument and checks if there is an object whose property value
     * is the same as the given value.
     * Please understand that if the test object exits, it may not be displayed in UI depends on the automation tool
     * @param propertyName1
     * @param value1
     * @param propertyName2
     * @param value2
     * @param top
     * @return - true if test object is visible, false otherwise.
     */
    @Override
    public boolean checkHtmlObjectDisplayed(String propertyName1, Object value1,String propertyName2, Object value2, IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyName1, value1);
        p[1] = new Property(propertyName2, value2);
        return checkObjectDisplayed(p,top);
    }
    @Override
    public boolean checkHtmlObjectDisplayed(Property[] property, IHtmlObject top) {
        return checkObjectDisplayed(property,top);
    }

    @Override
    public boolean checkHtmlObjectDisplayed(List<Property[]> list) {
        return checkObjectDisplayed(list,null);
    }

    @SuppressWarnings("unchecked")
    protected boolean checkObjectDisplayed(Object property, IHtmlObject top) {
        IHtmlObject[] objs;
        if(property instanceof List) {
            List<Property[]> list=(List<Property[]>) property;
            objs=getHtmlObject(list);
        } else {
            objs=getHtmlObject((Property[])property,top);
        }

        boolean displayed=false;

        if(objs!=null && objs.length>0) {
            for(IHtmlObject obj:objs) {
                try{displayed=obj.isVisible();} catch(Exception e){};
                if(displayed) {
                    break;
                }
            }
        }

        unregister(objs);
        return displayed;
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     */
    public void searchObjectWaitExists(String propertyKey, Object value) {
        searchObjectWaitExists(propertyKey, value, LONG_SLEEP);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     * @param name
     * @return
     */
    public void searchObjectWaitExists(String propertyKey, Object value,String name) {
        searchObjectWaitExists(propertyKey, value, name,LONG_SLEEP);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     * @param seconds
     */
    public void searchObjectWaitExists(String propertyKey, Object value,int seconds) {
        searchObjectWaitExists(propertyKey, value, null, null, seconds);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     * @param name
     * @param seconds
     * @return
     */
    public void searchObjectWaitExists(String propertyKey, Object value,String name,int seconds) {
        searchObjectWaitExists(propertyKey, value, null, null, name,seconds);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        searchObjectWaitExists(propertyKey1, value1, propertyKey2, value2,LONG_SLEEP);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param name - the object name
     * @return
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2, String name) {
        searchObjectWaitExists(propertyKey1, value1, propertyKey2, value2,name,LONG_SLEEP);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param seconds
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2, int seconds) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        searchObjectWaitExists(p, seconds);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param name - the name of the object
     * @param seconds
     * @return
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2, String name, int seconds) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);
        searchObjectWaitExists(p, name, seconds);
    }

    @Override
    public void searchObjectWaitExists(Property[] property, int timeout) {
        String name=Property.propertyToString(property);
        searchObjectWaitExists(property,name,timeout);
    }

    /**
     * Wait until the TestObject matching the given property exists.
     * @param property - the TestObject properties
     * @param name - the TestObject name
     * @param timeout
     */
    public void searchObjectWaitExists(Property[] property, String name,int timeout) {
        Property p=new Property(name,property);
        ArrayList<Property> list=new ArrayList<Property>();
        list.add(p);

        waitExists(timeout,list);
    }

    public void searchObjectWaitExists(List<Property[]> pList) {
        String name=Property.propertyToString(pList);
        searchObjectWaitExists(pList,name,SLEEP);
    }

    public void searchObjectWaitExists(List<Property[]> pList, int timeout) {
        String name=Property.propertyToString(pList);
        searchObjectWaitExists(pList,name,timeout);
    }

    public void searchObjectWaitExists(List<Property[]> pList, String name,int timeout) {
        Property p=new Property(name,pList);
        ArrayList<Property> list=new ArrayList<Property>();
        list.add(p);

        waitExists(timeout,list);
    }

    /**
     * Tentatively check if a IHtmlObject exists within the given time period
     * @param timeout
     * @param property
     * @return
     */
    public boolean tentativeWaitExists(int timeout, Property[] property) {
        return tentativeWaitExists(property,timeout);
    }

    /**
     * Tentatively check if a IHtmlObject exists within the given time period
     * @param timeout
     * @param list
     * @return
     */
    public boolean tentativeWaitExists(int timeout, List<Property[]> list) {
        return tentativeWaitExists(list,timeout);
    }

    @Override
    public boolean tentativeWaitExists(int timeout, Loadable page) {
        return tentativeWaitExists(page,timeout);
    }

    /**
     * Tentatively check if an object extists within the given time period
     * @param property
     * @param timeout
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean tentativeWaitExists(Object property, int timeout) {
        Timer timer=new Timer();
        boolean exists=false;
        while(!exists && timer.diff()<timeout) {
            sleep(1);
            if(property instanceof List) {
                exists=checkHtmlObjectExists((List<Property[]>)property);
            } else if(property instanceof Property[] ) {
                exists=checkHtmlObjectExists((Property[])property);
            } else {
                exists=((Page)property).exists();
            }
        }

        return exists;
    }

    /**
     * Wait for dropdown list options loading until the given option exists
     * @param propertyKey
     * @param value
     * @param option
     * @return
     */
    @Override
    public void dropdownOptionWaitExists(String propertyKey, Object value,Object option) {
        dropdownOptionWaitExists(Property.toPropertyArray(propertyKey,value),0,VERY_LONG_SLEEP,option,(IHtmlObject)null);

    }
    public void dropdownOptionWaitExists(Property[] p, Object option){
        dropdownOptionWaitExists(p,0,VERY_LONG_SLEEP,option,(IHtmlObject)null);
    }

    /**
     * Wait for dropdown list options loading until the given option exists
     * @param property
     * @param objectIndex
     * @param option
     * @param top
     * @return
     */
    public void dropdownOptionWaitExists(Property[] property,int objectIndex,int timeout,Object option,IHtmlObject top){
        boolean found=false;
        Timer timer=new Timer();
        sync(timeout);
        do {
            found=this.dropdownListContains(property, option,top);
            sleep(1);
        }while(!found && timer.diff()<timeout);

        int time=timer.diff();
        if(!found) {
            throw new ItemNotFoundException("Failed to find the given option in "+time+" seconds");
        }
    }

    public void dropdownOptionWaitExists(Property[] property,int objectIndex,int timeout, int optionTreshold, IHtmlObject top) {
        boolean found=false;
        Timer timer=new Timer();
        sync(timeout);
        do {
            List<String> options=getDropdownElements(property, top);
            found=options.size()>=optionTreshold;
            sleep(1);
        }while(!found && timer.diff()<timeout);

        int time=timer.diff();
        if(!found) {
            throw new ItemNotFoundException("Failed to find the given option in "+time+" seconds");
        }
    }

    public void focusOn(Property[] properties,int index, IHtmlObject top){
        IHtmlObject[] objs=this.getHtmlObject(properties, top);
        if(objs.length<index+1){
            throw new ObjectNotFoundException("can't find the Html Object by "+properties);
        }
        objs[index].click();
        unregister(objs);
    }

    @Override
    public void focusOn(Property[] properties) {
        focusOn(properties,0);

    }

    @Override
    public void focusOn(Property[] properties,int index){
        this.focusOn(properties, index, (IHtmlObject)null);
    }

    @Override
    public void focusOn(String propertyKey1,Object propertyValue1,String propertyKey2,Object propertyValue2,int index){
        focusOn(new Property[]{new Property(propertyKey1,propertyValue1),new Property(propertyKey2,propertyValue2)},index);
    }

    @Override
    public void focusOn(String propertyKey1,Object propertyValue1){
        focusOn(new Property[]{new Property(propertyKey1,propertyValue1)},0);
    }

    /**
     * Wait for the existence for one of the given web pages.
     * @param page - the web pag
     * @return
     */
    public Object waitExists(Loadable... page){
        return waitExists(VERY_LONG_SLEEP,page);
    }


    @SuppressWarnings("unchecked")
    public Object waitExists(List<Property[]>... pList) {
        return waitExists(VERY_LONG_SLEEP,pList);
    }

    /**
     * Wait for the existence of the given web page.
     * @param seconds
     * @param page - the web page
     * @return
     */
    public Object waitExists(int timeout,Loadable... page){
        ArrayList<Property> list=new ArrayList<Property>();

        for(int i=0;i<page.length;i++) {
            Property p=new Property(page[i].getName(),page[i]);
            list.add(p);
        }
        return waitExists(timeout,list);
    }

    /**
     * Wait for the existence of the TestObjects with the given properties
     * @param properties
     * @return the page/object loaded
     */
    public Object waitExists(Property[]... properties) {
        return waitExists(LONG_SLEEP,properties);
    }

    /**
     * Wait for the existence of the TestObjects with the given properties
     * @param seconds
     * @param properties
     * @return the page/object loaded
     */
    public Object waitExists(int timeout, Property[]... properties){
        ArrayList<Property> list=new ArrayList<Property>();

        for(int i=0;i<properties.length;i++) {
            Property p=new Property(Property.propertyToString(properties[i]),properties[i]);
            list.add(p);
        }

        return waitExists(timeout,list);
    }

    @SuppressWarnings("unchecked")
    public Object waitExists(int timeout, List<Property[]>... pLists) {
        ArrayList<Property> list=new ArrayList<Property>();

        for(int i=0;i<pLists.length;i++) {
            Property p=new Property(Property.propertyToString(pLists[i]),pLists[i]);
            list.add(p);
        }

        return waitExists(timeout,list);
    }

    public boolean checkExists(int timeout,Page... page) {
        boolean exists=false;
        try {
            waitExists(timeout,page);
            exists=true;
        } catch (Exception e){

        }

        return exists;
    }

    /**
     * Wait for the existence of the Web pages/TestObjects with the given properties
     * @param timeout
     * @param list - the list of Web pages/TestObjects' properties
     * @return
     */
    protected Object waitExists(int timeout, List<Property> list) {
        String objName = "";
        for (int i = 0; i < list.size(); i++) {
            if(objName.length()>0) {
                objName +=" or ";
            }

            objName +=list.get(i).getPropertyName();
        }

        logger.debug("Checking page(s) exist: "+objName);
        int[] result=loadingSync(timeout,list);
        int foundIndex=result[0];
        int loadingTime=result[1];
        if (foundIndex<0) {
            throw new PageNotFoundException(objName+ " is not found in "+loadingTime+" seconds.");
        }
        logger.debug("Found page: "+list.get(foundIndex).getPropertyName());

        if(BrowserPlugin.getInstance()!=null) {
            BrowserPlugin.getInstance().recordPageTiming(list.get(foundIndex).getPropertyName(), loadingTime);
        }

        Object founded=list.get(foundIndex).getPropertyValue();
        if(founded instanceof Page) {
            PageTrack.addAVisitedPage((Page)founded);
        }

        return founded;
    }

    /**
     * loop through the objects in the list and check which one exists after browser finished loading
     * @param timeout
     * @param list - list of object to check for loading
     * @return - int array of size 2, the 1st one is the index of list, the 2nd one is the loading time
     */
    protected abstract int[] loadingSync(int timeout, List<Property> list);

    /**
     * Wait for a IHtmlObject with the given property disappear
     * @param timeout
     * @param list
     */
    @Override
    public void waitDisappear(int timeout, List<Property[]> list) {
        waitDisappear(list,timeout);
    }

    /**
     * Wait for a IHtmlObject with the given property disappear
     * @param timeout
     * @param property
     */
    @Override
    public void waitDisappear(int timeout, Property[] property) {
        waitDisappear(property,timeout);
    }

    @Override
    public void waitDisappear(int timeout, Loadable page) {
        waitDisappear(page,timeout);
    }

    /**
     * Wait for a IHtmlObject with the given property disappear
     * @param timeout
     * @param property
     */
    @SuppressWarnings("unchecked")
    private void waitDisappear(Object property,int timeout) {
        Timer timer=new Timer();
        boolean exists=true;

        while(exists && timer.diff()<timeout) {
            sleep(1);
            if(property instanceof List) {
                exists=checkHtmlObjectDisplayed(((List<Property[]>)property));
            } else if(property instanceof Property[]){
                exists=checkHtmlObjectDisplayed((Property[])property);
            } else {
                exists=((Loadable) property).exists();
            }
        }

        if(exists) {
            String name="";
            if(property instanceof List) {
                name="Html Object: "+Property.propertyToString(((List<Property[]>)property));
            } else if(property instanceof Property[]){
                name="Html Object: "+Property.propertyToString(((Property[])property));
            } else {
                name=((Loadable)property).getName();
            }
            throw new ItemNotFoundException("The "+name+" doesn't disappear in "+timeout+" seconds.");
        }
    }

    /**
     * Wait for a IHtmlObject with the given property displayed
     * @param timeout
     * @param list
     */
    @Override
    public void waitDisplay(int timeout, List<Property[]> list) {
        waitDisplay(list,timeout);
    }

    @Override
    public void waitDisplay(int timeout, Property[] property) {
        waitDisplay(property,timeout);
    }

    @Override
    public void waitDisplay(int timeout, Loadable page) {
        waitDisplay(page,timeout);
    }

    /**
     * Wait for a IHtmlObject with the given property displayed
     * @param timeout
     * @param property
     */
    @SuppressWarnings("unchecked")
    private void waitDisplay(Object property,int timeout) {
        Timer timer=new Timer();
        boolean exists=false;

        while(!exists && timer.diff()<timeout) {
            sleep(1);
            if(property instanceof List) {
                exists=checkHtmlObjectDisplayed(((List<Property[]>)property));
            } else if(property instanceof Property[]){
                exists=checkHtmlObjectDisplayed((Property[])property);
            } else {
                exists=((Loadable) property).exists();
            }
        }

        if(!exists) {
            String name="";
            if(property instanceof List) {
                name="Html Object: "+Property.propertyToString(((List<Property[]>)property));
            } else if(property instanceof Property[]){
                name="Html Object: "+Property.propertyToString(((Property[])property));
            } else {
                name=((Loadable)property).getName();
            }
            throw new ItemNotFoundException("The "+name+" doesn't appear in "+timeout+" seconds.");
        }
    }

    /**
     * This method will dismiss dialogs that appears within the given timeout period.
     * @param timeout
     * @param dialog
     * @param limits - the at most number of dialogs to be dismissed.
     */
    public int dismissDialogs(int timeout, DialogPage dialog, int limits) {
        Timer timer=new Timer();
        int count=0;

        while (count<limits && timer.diff()<timeout) {
            if(dialog.exists()) {
                count++;
                dialog.dismiss();
            }
            sleep(1);
        }

        logger.info("Dismissed "+count+" dialogs");
        return count;
    }

    /**
     * Retrieve the text property of the TestObject
     * @param propertyName
     * @param value
     * @return
     */
    public String getObjectText(String propertyName,Object value) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyName, value);

        return getObjectText(p);
    }

    /**
     * Retrieve the text property of the TestObject
     * @param propertyName1
     * @param value1
     * @param propertyName2
     * @param value2
     * @return
     */
    @Override
    public String getObjectText(String propertyName1,Object value1, String propertyName2,Object value2){
        Property[] p = new Property[2];
        p[0] = new Property(propertyName1, value1);
        p[1] = new Property(propertyName2, value2);
        return getObjectText(p);
    }

    @Override
    public String getObjectText(Property...properties) {
        IHtmlObject[] objs=this.getHtmlObject(properties);
        String text=null;
        if(objs.length>0) {
            text= objs[0].text();
        }

        unregister(objs);

        return text;
    }

    @Override
    public String getObjectText(List<Property[]> list) {
        IHtmlObject[] objs=this.getHtmlObject(list);
        String text=null;
        if(objs.length>0) {
            text= objs[0].text();
        }

        unregister(objs);

        return text;
    }

    @Override
    public List<String> getObjectsText(Property[] properties) {
        IHtmlObject[] objs=this.getHtmlObject(properties);
        List<String> texts = new ArrayList<String>();
        if(objs.length>0) {
            for(int i=0; i<objs.length; i++){
                texts.add(objs[i].text());
            }
        }

        unregister(objs);

        return texts;
    }

    @Override
    public String getObjectAttribute(String propertyName, Object value,
                                     String attrName) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyName, value);

        return getObjectAttribute(attrName,p);
    }

    @Override
    public String getObjectAttribute(String propertyName1, Object value1,
                                     String propertyName2, Object value2, String attrName) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyName1, value1);
        p[1] = new Property(propertyName2, value2);

        return getObjectAttribute(attrName,p);
    }

    @Override
    public String getObjectAttribute(String attrName, Property... properties) {
        IHtmlObject[] objs=this.getHtmlObject(properties);
        String attr_value=null;
        if(objs.length>0) {
            attr_value= objs[0].getAttributeValue(attrName);
        }

        unregister(objs);
        return attr_value;
    }

    @Override
    public String getObjectAttribute(List<Property[]> list, String attrName) {
        IHtmlObject[] objs=this.getHtmlObject(list);
        String attr_value=null;
        if(objs.length>0) {
            attr_value= objs[0].getAttributeValue(attrName);
        }

        unregister(objs);

        return attr_value;
    }

    @Override
    public void catchScreenShot(String filename) {
        try {
            File imageFile = null;

            if (!filename.endsWith(".png"))
                imageFile = new File(filename + ".png");
            else
                imageFile = new File(filename);

            filename=filename.replaceAll("\\\\+", "/");

            String pathString=filename.substring(0,filename.lastIndexOf("/"));

            File path=new File(pathString);
            if(!path.exists()){
                boolean successful=path.mkdirs();
                if(!successful) {
                    throw new RuntimeException("Failed to create directory");
                }
            }
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = robot.createScreenCapture(captureSize);

            ImageIO.write(img, "png", imageFile);
        } catch (Exception e) {
            logger.warn("Can't create the screenshot for " + filename);
            e.printStackTrace();
        }
    }

    @Override
    public void closeAllBrowsers() {
        logger.info("Close all browsers");
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("taskkill /F /IM iexplore.exe /T").waitFor();
            rt.exec("taskkill /F /IM chrome.exe /T").waitFor();
            rt.exec("taskkill /F /IM firefox.exe /T").waitFor();
            rt.exec("taskkill /F /IM dw20.exe /T").waitFor();
            rt.exec("taskkill /F /IM phantomjs.exe /T").waitFor();
            rt.exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255").waitFor();//clean up cache
        }catch (Exception e) {
            logger.error("Failed to close browsers due to "+e.getMessage());
        }
    }

    @Override
    public void inputKey(KeyInput... keys) {
        try {
            for(KeyInput key: keys) {
                int flag=key.getType()==100?1:0;
                AutoitXFactory.getAutoitx().AU3_Send(new WString(AutoItUtil.transferKeys(key)), flag);
            }
        } catch (Exception e) {
            throw new ActionFailedException(e.getMessage());
        }

    }

    /**
     * Check if the RadioButton with the given property selected or not
     * @param propertyKey
     * @param value
     * @return
     */
    @Override
    public boolean isRadioButtonSelected(String propertyKey, Object value) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return isRadioButtonSelected(p);
    }

    /**
     * Check if the RadioButton with the given property selected or not
     * @param propertyKey
     * @param value
     * @param top - search the object from
     * @return
     */
    @Override
    public boolean isRadioButtonSelected(String propertyKey, Object value,IHtmlObject top) {
        Property[] p = new Property[1];
        p[0] = new Property(propertyKey, value);
        return isRadioButtonSelected(p,top);
    }

    /**
     * Check if the RadioButton with the given property selected or not
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @return
     */
    @Override
    public boolean isRadioButtonSelected(String propertyKey1, Object value1,String propertyKey2, Object value2) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        return isRadioButtonSelected(p);
    }

    @Override
    public boolean isRadioButtonSelected(String propertyKey1, Object value1,String propertyKey2, Object value2,IHtmlObject top) {
        Property[] p = new Property[2];
        p[0] = new Property(propertyKey1, value1);
        p[1] = new Property(propertyKey2, value2);

        return isRadioButtonSelected(p,top);
    }

    /**
     * Check if the RadioButton with the given property selected or not
     * @param property
     * @return
     */
    @Override
    public boolean isRadioButtonSelected(Property[] property) {
        return isRadioButtonSelected(property,null);
    }

    @Override
    public boolean isRadioButtonSelected(Property[] property, IHtmlObject top) {
        IHtmlObject[] objs=getRadioButton(property,top);

        boolean selected=((IRadioButton)objs[0]).isSelected();

        unregister(objs);

        return selected;
    }

    public void rightClickGuiObject(Property[] property, boolean forced, int index, IHtmlObject top) {
        IHtmlObject[] objs = this.getHtmlObject(property, top);
        if(!forced) {
            forced = Boolean.parseBoolean(TestProperty.getProperty("forceOperation", "false"));
        }
        if(objs != null && objs.length > index ) {
            objs[index].rightClick();
            unregister(objs);
        } else if(forced) {
            unregister(objs);
            throw new ItemNotFoundException("Failed to find the IHtmlObject with " + Arrays.toString(property));
        } else {
            logger.warn("Failed to find IHtmlObject with " + Arrays.toString(property));
        }
    }

    @Override
    public boolean verifySelect(Property[] property, int objectIndex,
                                String option, IHtmlObject top) {
        waitExists(property);
        String tmp = getDropdownListValue(property, objectIndex,top);

        return tmp.equalsIgnoreCase(option);
    }

    public void switchToFrameWd(String txt) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    private void mouseOverHtmlObject(Object property, IHtmlObject top, int index) {
        IHtmlObject[] objs;
        if(property instanceof List) {
            List<Property[]> list=(List<Property[]>) property;
            objs=getHtmlObject(list);
        } else {
            objs=getHtmlObject((Property[])property,top);
        }

        if(objs!=null && objs.length>index) {
            objs[index].onMouseOver();
        }

        unregister(objs);
    }

    public void mouseOverHtmlObject(List<Property[]> list) {
        mouseOverHtmlObject(list, null, 0);
    }

    public void mouseOverHtmlObject(Property[] property, int index, IHtmlObject objs) {
        mouseOverHtmlObject(property, objs, index);
    }

    public void mouseOverHtmlObject(Property[] property, int index) {
        mouseOverHtmlObject(property, null, index);
    }

    public void mouseOverHtmlObject(Property[] property) {
        mouseOverHtmlObject(property, 0);
    }
}
