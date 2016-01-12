package com.active.qa.automation.web.testapi.interfaces.html;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IScrollable {
    public void hScrollTo(int position);

    public void vScrollTo(int position);

    public void scrollLineDown();

    public void scrollLineLeft();

    public void scrollLineRight();

    public void scrollLineUp();

    public void scrollPageLeft();

    public void scrollPageRight();

    public void scrollPageDown();

    public void scrollPageUp();
}

