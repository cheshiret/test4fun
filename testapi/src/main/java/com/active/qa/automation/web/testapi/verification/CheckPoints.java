package com.active.qa.automation.web.testapi.verification;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by tchen on 1/11/2016.
 */
public class CheckPoints {
    private static class SingletonHolder { private final static CheckPoints _instance=new CheckPoints(); }

    public static CheckPoints getInstance() {
        return SingletonHolder._instance;
    }

    private Map<CheckIdentifier, CheckPoint> checkpoints;
    private boolean locked;

    protected CheckPoints() {
        checkpoints=new HashMap<CheckIdentifier,CheckPoint>();
        locked=false;

    }

    public <C extends Checkable> void put(CheckIdentifier key, CheckPoint checkPoint) {
        if(!locked)
            checkpoints.put(key, checkPoint);
        else
            throw new ActionFailedException("There are still some checkpoints not performed in previous work flows: "+checkpoints.toString());
    }

    public boolean hasKey(CheckIdentifier key) {
        return checkpoints.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <C extends Checkable> CheckPoint getCheckPoint(CheckIdentifier key) {
        locked=true;
        try {
            return checkpoints.remove(key);
        } finally {
            if(isEmpty()) {
                locked=false;
            }
        }
    }

    public void reset() {
        checkpoints.clear();
        locked=false;
    }

    public int size() {
        return checkpoints.size();
    }

    public boolean isEmpty() {
        return checkpoints.size()<1;
    }

    @Override
    public String toString() {
        return checkpoints.toString();
    }

}

