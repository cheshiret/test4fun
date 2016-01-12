package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Change the behavior of WebElement to be consistent across browsers.
 * Created by tchen on 1/6/2016.
 */
public class ConsistentWebElement implements WebElement, WrapsElement, Locatable {
    WebElement element;

    public ConsistentWebElement( WebElement delegate ) {
        if ( delegate instanceof ConsistentWebElement )
            System.err.println( "          !!!!!!!!!!@@@@@@@@@@@ Double Wrap @@@@@@@@@@@@!!!!!!!!!!!" );
        element = delegate;
    }

    public void click() {
        element.click();
    }

    public void submit() {
        element.submit();
    }

    public void sendKeys( CharSequence... paramArrayOfCharSequence ) {
        element.sendKeys( paramArrayOfCharSequence );
    }

    public void clear() {
        element.clear();
    }

    public String getTagName() {
        return StringUtil.unNull( element.getTagName() );
    }

    public String getAttribute( String paramString ) {
        return StringUtil.unNull( element.getAttribute( paramString ) );
    }

    public boolean isSelected() {
        return element.isSelected();
    }

    public boolean isEnabled() {
        return element.isEnabled();
    }

    public String getText() {
        return StringUtil.unNull( element.getText() );
    }

    public List<WebElement> findElements(By paramBy ) {
        List<WebElement> elements = new ArrayList<>();
        List<WebElement> es=element.findElements( paramBy );
        for ( WebElement w :  es)
            elements.add( new ConsistentWebElement( w ) );

        return elements;
    }

    public WebElement findElement( By paramBy ) {
        return new ConsistentWebElement( element.findElement( paramBy ) );
    }

    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    public Point getLocation() {
        return element.getLocation();
    }

    public Dimension getSize() {
        return element.getSize();
    }

    public String getCssValue( String paramString ) {
        return StringUtil.unNull( element.getCssValue( paramString ) );
    }

    @Override
    public WebElement getWrappedElement() {
        return element;
    }


    public Point getLocationOnScreenOnceScrolledIntoView() {
//    if ( element instanceof Locatable )
//      return ( (Locatable)element ).getLocationOnScreenOnceScrolledIntoView();

        return null;
    }

    @Override
    public Coordinates getCoordinates() {
        if ( element instanceof Locatable )
            return ( (Locatable)element ).getCoordinates();

        return null;
    }

}

