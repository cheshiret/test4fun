package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.util.Property;

/**
 * Created by tchen on 1/11/2016.
 */
public abstract class HtmlPage extends Page {

    protected Property[] div() {
        return Property.toPropertyArray(".class","Html.DIV");
    }

    protected Property[] a() {
        return Property.toPropertyArray(".class","Html.A");
    }

    protected Property[] input(String type) {
        return Property.toPropertyArray(".class","Html.INPUT."+type.toLowerCase());
    }

    protected Property[] textarea() {
        return Property.toPropertyArray(".class","Html.AREA");
    }

    protected Property[] img() {
        return Property.toPropertyArray(".class","Html.IMG");
    }

    protected Property[] table() {
        return Property.toPropertyArray(".class","Html.TABLE");
    }

    protected Property[] tr() {
        return Property.toPropertyArray(".class","Html.TR");
    }

    protected Property[] td() {
        return Property.toPropertyArray(".class","Html.TD");
    }

    protected Property[] span() {
        return Property.toPropertyArray(".class","Html.SPAN");
    }

    protected Property[] tbody() {
        return Property.toPropertyArray(".class","Html.TBODY");
    }

    protected Property[] select() {
        return Property.toPropertyArray(".class","Html.SELECT");
    }

    protected Property[] option() {
        return Property.toPropertyArray(".class","Html.OPTION");
    }

    protected Property[] form() {
        return Property.toPropertyArray(".class","Html.FORM");
    }

    protected Property[] body() {
        return Property.toPropertyArray(".class","Html.BODY");
    }

    protected Property[] li() {
        return Property.toPropertyArray(".class","Html.LI");
    }

    protected Property[] ul() {
        return Property.toPropertyArray(".class","Html.UL");
    }

    protected Property[] ol() {
        return Property.toPropertyArray(".class","Html.OL");
    }

    protected Property[] label() {
        return Property.toPropertyArray(".class","Html.Label");
    }

    protected Property[] button() {
        return Property.toPropertyArray(".class","Html.BUTTON");
    }


    /**
     * frame tag is not supported in HTML 5
     * @return
     */
    protected Property[] frame() {
        return Property.toPropertyArray(".class","Html.FRAME");
    }

    /**
     * frameset is not supported in HTML 5
     * @return
     */
    protected Property[] frameset() {
        return Property.toPropertyArray(".class","Html.FRAMESET");
    }

    protected Property[] head() {
        return Property.toPropertyArray(".class","Html.HEAD");
    }
}
