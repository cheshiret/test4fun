package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.ActionFailedException;
import com.active.qa.automation.web.testapi.PageNotFoundException;
import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.datacollection.Data;
import com.active.qa.automation.web.testapi.datacollection.DataAttribute;
import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.page.Loadable;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.PageTrack;
import com.active.qa.automation.web.testapi.verification.Checkable;
import com.active.qa.automation.web.testapi.verification.Verification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by tchen on 1/11/2016.
 */
public abstract class Page implements Loadable,Checkable,TestApiConstants {
    protected int timeout;
    protected AutomationLogger logger;

    protected Page() {
        logger=AutomationLogger.getInstance();
        timeout = TestApiConstants.VERY_LONG_SLEEP;
    }

    public String getName() {
        String name=this.getClass().getName();

        int index=name.lastIndexOf(".");
        name=name.substring(index+1);

        return name;
    }

    @Override
    public void waitLoading() {
        Browser.getInstance().waitExists(timeout,this);
    }


    @Override
    public void check(Data<?>... data) {
        if(this!= PageTrack.getLastPageVisited()) {
            throw new PageNotFoundException("Current page '"+PageTrack.getLastPageVisited().getName()+"' is not expected "+this.getName());
        }

        for(Data<?> d:data) {

            Data<?> dtc=d.toBeCollected()?d:d.getEmptyData();

//			String dataName=d.getDataName();
            try {
//				Method method=this.getClass().getMethod("collect"+dataName,d.getClass());
//				method.invoke(this, dtc);
                collect(dtc);

            } catch (Exception e) {
                throw new ActionFailedException(e);
            }

            if(!d.toBeCollected()) {
                Verification.verifyData(dtc, d);
            }
        }
    }

    protected void collect(Data<?> data) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<?> keys=data.getKeys();

        for(Object key:keys) {
            Method m = getClass().getMethod("get"+key.toString(), (Class<?>[])null);
            Object value=m.invoke(this, (Object[])null);
            data.save((DataAttribute)key, value);
        }
    }

}
