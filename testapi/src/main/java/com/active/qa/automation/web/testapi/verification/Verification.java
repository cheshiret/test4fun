package com.active.qa.automation.web.testapi.verification;

import com.active.qa.automation.web.testapi.exception.ErrorOnDataException;
import com.active.qa.automation.web.testapi.datacollection.Data;
import com.active.qa.automation.web.testapi.datacollection.DataAttribute;
import com.active.qa.automation.web.testapi.util.RegularExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by tchen on 1/11/2016.
 */
public class Verification {
    public static void verifyString(String actual, RegularExpression expected) {
        if( !((RegularExpression)expected).match(actual)) {
            throw new ErrorOnDataException("acutal:'"+actual+"', expected pattern: '"+expected.getPattern()+"'");
        }

    }

    public static void verifyString(String actual, String expected) {
        boolean matchOrEqual=false;
        if(expected.startsWith("<regex>=")) {
            String pattern=expected.substring(8);
            matchOrEqual=actual.matches(pattern);
        } else if(expected.startsWith("<?i>=")) {
            String iExpected=expected.substring(5);
            matchOrEqual=actual.equalsIgnoreCase(iExpected);
        } else {
            matchOrEqual=actual.equals(expected);
        }

        if(!matchOrEqual) {
            throw new ErrorOnDataException("acutal: '"+actual+"', expected: '"+expected+"'");
        }
    }

    public static void verify(Object actual, Object expected) {

        if(actual instanceof String) {
            if(expected instanceof RegularExpression) {
                verifyString((String) actual,(RegularExpression) expected);
            } else {
                verifyString((String) actual,(String) expected);
            }
        } else {
            if(!actual.equals(expected)) {
                throw new ErrorOnDataException("acutal: '"+actual+"', expected: '"+expected+"'");
            }
        }
    }

    public static void verifyData(Data<?> actual, Data<?> expected) {
        List<String> errorMsg=new ArrayList<String>();

        Set<?> keys= expected.getKeys();

        for(Object key:keys) {
            Object act=actual.getValue((DataAttribute)key);
            Object exp=expected.getValue((DataAttribute)key);

            try {
                if(act==null)
                {
                    throw new ErrorOnDataException("Value is null.");
                }else{
                    verify(act,exp);
                }
            } catch(ErrorOnDataException e) {
                errorMsg.add("["+key+"]: "+e.getMessage());
            }
        }

        if(errorMsg.size()>0) {
            throw new ErrorOnDataException(errorMsg.toString());
        }

    }

}

