package com.active.qa.automation.web.testapi.interfaces.browser;

import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.page.Loadable;
import com.active.qa.automation.web.testapi.util.Property;
import com.active.qa.automation.web.testapi.pages.Page;

import java.util.List;

/**
 * Created by tchen on 1/11/2016.
 */
public interface ISynchronize {
    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     */
    public void searchObjectWaitExists(String propertyKey, Object value);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     * @param name
     * @return
     */
    public void searchObjectWaitExists(String propertyKey, Object value,String name);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     * @param seconds
     */
    public void searchObjectWaitExists(String propertyKey, Object value,int seconds);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey
     * @param value
     * @param name
     * @param seconds
     * @return
     */
    public void searchObjectWaitExists(String propertyKey, Object value,String name,int seconds);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param name
     * @return
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2, String name);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyKey1
     * @param value1
     * @param propertyKey2
     * @param value2
     * @param seconds
     * @return
     */
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2, int seconds);

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
    public void searchObjectWaitExists(String propertyKey1, Object value1,String propertyKey2, Object value2, String name, int seconds);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param property
     * @param seconds
     */
    public void searchObjectWaitExists(Property[] property, int timeout);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyList
     * @param timeout
     */
    public void searchObjectWaitExists(List<Property[]> propertyList, int timeout);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param propertyList
     */
    public void searchObjectWaitExists(List<Property[]> propertyList);

    /**
     * Wait until the TestObject matching the given property exists.
     * @param property - the TestObject properties
     * @param name - the TestObject name
     * @param seconds
     */
    public void searchObjectWaitExists(Property[] property, String name,int timeout);

    /**
     * Wait for dropdown list options loading until the given option exists
     * @param propertyKey
     * @param value
     * @param option
     * @return
     */
    public void dropdownOptionWaitExists(String propertyKey,Object value, Object option);

    /**
     * Wait for dropdown list options loading until the given option exists
     * @param property
     * @param objectIndex
     * @param option
     * @param top
     * @return
     */
    public void dropdownOptionWaitExists(Property[] property,int objectIndex,int timeout,Object option,IHtmlObject top);

    /**
     * Wait for the existence of the given web page. If dialog popup opens, click OK/Yes button.
     * @param page - the web page
     * @return	the actual time spent for the page/object loading
     */
    public Object waitExists(Loadable... page);

    /**
     * Wait for the existence of the given web page. If dialog popup opens, click OK/Yes button.
     * @param timeout - maximum waiting time
     * @param page - the web page
     * @return	Page object found
     */
    public Object waitExists(int timeout,Loadable... page);

    /**
     * Wait for the existence of the TestObjects with the given properties
     * @param properties
     * @return the actual time spent for the page/object loading
     */
    public Object waitExists(Property[]... properties);

    /**
     * Wait for the existence of the TestObjects with the given properties
     * @param seconds
     * @param properties
     * @return the actual time spent for the page/object loading
     */
    public Object waitExists(int timeout,Property[]... properties);

    public boolean checkExists(int timeout,Page... page);

    void waitDisappear(int timeout, List<Property[]> list);

    void waitDisappear(int timeout, Loadable page);

    void waitDisappear(int timeout, Property[] property);

    public void waitDisplay(int timeout, List<Property[]> list);

    public void waitDisplay(int timeout, Property[] property);

    public void waitDisplay(int timeout, Loadable page);

    public boolean tentativeWaitExists(int timeout, Property[] propertyArray);

    public boolean tentativeWaitExists(int timeout, Loadable item);

}
