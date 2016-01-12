package com.active.qa.automation.web.testapi.datacollection;

import com.active.qa.automation.web.testapi.ItemNotFoundException;
import com.active.qa.automation.web.testapi.util.StringUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tchen on 1/11/2016.
 */
public class Data<E extends Enum<E> & DataAttribute> implements Serializable {
private static final long serialVersionUID = 1L;
        Map<E, Object> data;
private boolean toBeCollected;

/**Data name is usually decided by the data attribute name or Data subclass name*/
private String dataName=this.getClass().getSimpleName();

public Data() {
        data=new HashMap<E, Object>();
        dataName=this.getClass().getSimpleName().replaceAll("Info", "");
        toBeCollected=false;
        }
/**
 * construct a Data object with the given keys and all null values
 * @param keys
 */
@SafeVarargs
public Data(E... keys) {
        data=new HashMap<E, Object>();
        for(E key:keys) {
        put(key,null);
        toBeCollected=true;
        }
        }

public Data(Set<E> keys) {
        data=new HashMap<E, Object>();
        for(E key:keys) {
        put(key,null);
        toBeCollected=true;
        }
        }

public void setToBeCollected(boolean value) {
        toBeCollected=value;
        }

public boolean toBeCollected() {
        return toBeCollected;
        }

/**
 * add a new key value pair to the data collection
 * @param key
 * @param value
 */
public void put(E key, Object value) {
        if(value!=null) {
        validateType(value,key.getType());

        }

        data.put(key, value);
        if(StringUtil.isEmpty(dataName) || dataName.matches("^(String)?Data$")) {
        dataName=key.getClass().getSimpleName().replaceAll("Attr", "");
        }

        }

/**
 * add a new key value pair to the data collection
 * @param key
 * @param value
 */
@SuppressWarnings("unchecked")
public void put(DataAttribute aKey, Object value) {
        put((E) aKey, value);
        }

/**
 * Save the given value to the existing aKey. If the key doesn't exists in the data collection, ItemNotFoundException will be thrown.
 * @param aKey -  an existing key in the data collection
 * @param value - the value to be saved
 */
public void save(E aKey, Object value) {
        if(!data.containsKey(aKey)){
        throw new ItemNotFoundException("Key "+aKey.toString()+" doesn't exists.");
        }

        put(aKey, value);
        }

/**
 * Save the given value to the existing aKey. If the key doesn't exists in the data collection, ItemNotFoundException will be thrown.
 * @param aKey -  an existing key in the data collection
 * @param value - the value to be saved
 */
@SuppressWarnings("unchecked")
public void save(DataAttribute aKey, Object value) {

        save((E)aKey, value);
        }

public Object get(E key) {
        return data.get(key);
        }

public String stringValue(DataAttribute key) {
        Object value = data.get(key);
        if(value!=null) {
        validateType(value,String.class);
        }
        return (String)value;
        }
public Boolean booleanValue(DataAttribute key) {
        Object value = data.get(key);
        if(value!=null) {
        validateType(value,Boolean.class);
        }
        return (Boolean)value;
        }

public Object getValue(DataAttribute key) {
        return data.get(key);
        }

public Set<E> getKeys() {
        return data.keySet();
        }

public boolean has(E key) {
        return data.containsKey(key);
        }

protected void validateType(Object obj, Class<?> expected) {
        if(expected !=obj.getClass() && !expected.isInstance(obj)) {
        throw new ItemNotFoundException("Actual type "+obj.getClass().getName()+" is not expected "+expected.getName());
        }
        }

/**
 * Data name is usually decided by the data attribute.
 * @return
 */
public String getDataName() {
        return dataName;
        }

/**
 * reset all values to null
 */
public void resetNull() {
        Set<E> keys=getKeys();

        for(E key:keys) {
        put(key,null);
        }
        toBeCollected=true;
        }

public Data<E> getEmptyData() {
        return new Data<E>(this.getKeys());
        }

        }

