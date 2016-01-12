package com.active.qa.automation.web.testapi.interfaces.page;

/**
 * Created by tchen on 1/11/2016.
 */
public interface Loadable {
    public boolean exists();

    public void waitLoading();

    public String getName();

}

