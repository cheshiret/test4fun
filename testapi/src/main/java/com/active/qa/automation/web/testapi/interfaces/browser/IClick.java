package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IClick {
    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * Do nothing if the object doesn't exist.
     * If more than one objects found matching the property, click the 1st one.
     * @param propertyKey - the property key
     * @param value - the property value
     */
    public void clickGuiObject(String propertyKey, Object value);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * Do nothing if the object doesn't exist.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey, Object value, int index);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * If more than one objects found matching the property, click the 1st one.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickGuiObject(String propertyKey, Object value, boolean forced);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey, Object value,boolean forced, int index);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * @param propertyKey - the property key
     * @param value - the property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     * @param top - the top level GuiTestObject starting the search from
     */
    public void clickGuiObject(String propertyKey, Object value,boolean forced, int index, IHtmlObject top);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys.
     * Do nothing if the object doesn't exist.
     * If more than one objects found matching the property, click the 1st one.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys. If the object doesn't exist, do nothing
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2, int index);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys. If more than one objects found matching the property, click the 1st one.
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2, boolean forced);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys
     * @param propertyKey1 - the 1st property key
     * @param value1 - the 1st property value
     * @param propertyKey2 - the 2nd property key
     * @param value2 - the 2nd property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2, boolean forced, int index);

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
    public void clickGuiObject(String propertyKey1, Object value1,String propertyKey2, Object value2, boolean forced, int index,IHtmlObject top);

    /**
     * Make a mouse click on the Gui Object satisfying the given properties.
     * If more than one objects found, click the 1st one.
     * Do nothing if the object doesn't exist
     * @param property - the properties of the Gui Object
     */
    public void clickGuiObject(Property[] property);
    /**
     * Make a mouse click on the Gui Object satisfying the given properties.
     * If more than one objects found, click the 1st one.
     * @param property - the properties of the Gui Object
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickGuiObject(Property[] property, boolean forced);

    /**
     * Make a mouse click on the Gui Object satisfying the given properties.
     * Do nothing if the object doesn't exist
     * @param property - the properties of the Gui Object
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(Property[] property, int index);

    /**
     * Make a mouse click on the Test Object satisfying the given properties.
     * Each element in the list is a property set for a TestObject. The method iterate searching from descendant of the previous object in the list
     * Do nothing if the object doesn't exist
     * @param list - the list of property set of the Test Objects
     * @praam forced
     * @param index - the index of the gui object to click on
     */

    /**
     * Make a mouse click on the Gui Object satisfying the given properties
     * @param property - the properties of the Gui Object
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(Property[] property, boolean forced, int index);

    /**
     * Make a mouse click on the Gui Object satisfying the given propertys
     * @param property - the properties of the Gui Object
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param index - the index of the gui object to click on
     */
    public void clickGuiObject(Property[] property, boolean forced, int index,IHtmlObject top);

    public void clickGuiObject(List<Property[]> property, boolean forced, int index);

    public void clickGuiObject(List<Property[]> property);

    /**
     * Click the image area which has the same Href value as hrefValue.
     * @param imageProperties - image properties other than ".class"
     * @param areaHref - the area Href property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     * @param top - the top GuiTestObject searching from
     */
    public void clickImageArea(Property[] imageProperties, Object areaHref,boolean forced,IHtmlObject top);

    /**
     * Click the image area which has the same Href value as hrefValue.
     * @param imageProperties - image properties other than ".class"
     * @param areaHref - the area Href property value
     * @param forced - if true, if the object doesn't exist, throw an exception. If false, do nothing if the object doesn't exist
     */
    public void clickImageArea(Property[] imageProperties, Object areaHref,boolean forced);

    /**
     * Click the image area which has the same Href value as hrefValue.
     * @param imageProperties - image properties other than ".class"
     * @param areaHref - the area Href property value
     */
    public void clickImageArea(Property[] imageProperties, Object areaHref);

    public void focusOn(Property[] properties, int index, IHtmlObject top);

    public void focusOn(Property[] properties, int index);

    public void focusOn(Property[] propertiesp);

    public void focusOn(String propertyKey, Object value);

    public void focusOn(String propertyKey1, Object propertyValue1,String propertyKey2, Object propertyValue2, int index);

    public void rightClickGuiObject(Property[] property, boolean forced, int index, IHtmlObject top);

    public void mouseOverHtmlObject(List<Property[]> list);

    public void mouseOverHtmlObject(Property[] property);

    public void mouseOverHtmlObject(Property[] property, int index, IHtmlObject top);

    public void mouseOverHtmlObject(Property[] property, int index);
}

