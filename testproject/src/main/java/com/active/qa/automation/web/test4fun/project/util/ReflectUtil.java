package com.active.qa.automation.web.test4fun.project.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by tchen on 1/18/2016.
 */
public class ReflectUtil {
  public static void reflectClass(Object model) throws NoSuchMethodException,
      IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    // Get all property as a list
    Field[] field = model.getClass().getDeclaredFields();
    for (int j = 0; j < field.length; j++) {
      String name = field[j].getName();
//			name = name.substring(0, 1).toUpperCase() + name.substring(1);
      // Get type

      //Get rid of final   System.out.println(field.getName() + " -> static:" + Modifier.isStatic(field.getModifiers()) + " final:" + Modifier.isFinal(field.getModifiers()));
      //get value

      //need fix java.lang.NoSuchMethodException: com.activenetwork.qa.awo.datacollection.legacy.QueryCustomer.getseq()
      String type = field[j].getGenericType().toString();
      System.out.println("Property: " + name);
      if (type.equals("class java.lang.String")) {
        Method m = model.getClass().getMethod("get" + name);
        String value = (String) m.invoke(model);
        System.out.println("Data Type: String");
        if (value != null) {
          System.out.println("Property value: " + value);
        } else {
          System.out.println("Property value: Null!");
        }
      }
      if (type.equals("class java.lang.Integer")) {
        Method m = model.getClass().getMethod("get" + name);
        Integer value = (Integer) m.invoke(model);
        System.out.println("Data Type: Integer");
        if (value != null) {
          System.out.println("Property value: " + value);
        } else {
          System.out.println("Property value: Null!");
        }
      }
      if (type.equals("class java.lang.Short")) {
        Method m = model.getClass().getMethod("get" + name);
        Short value = (Short) m.invoke(model);
        System.out.println("Data Type: Short");
        if (value != null) {
          System.out.println("Property value: " + value);
        } else {
          System.out.println("Property value: Null!");
        }
      }
      if (type.equals("class java.lang.Double")) {
        Method m = model.getClass().getMethod("get" + name);
        Double value = (Double) m.invoke(model);
        System.out.println("Data Type: Double");
        if (value != null) {
          System.out.println("Property value: " + value);
        } else {
          System.out.println("Property value: Null!");
        }
      }
      if (type.equals("class java.lang.Boolean")) {
        Method m = model.getClass().getMethod("get" + name);
        Boolean value = (Boolean) m.invoke(model);
        System.out.println("Data Type: Boolean");
        if (value != null) {
          System.out.println("Property value: " + value);
        } else {
          System.out.println("Property value: Null!");
        }
      }
      if (type.equals("class java.util.Date")) {
        Method m = model.getClass().getMethod("get" + name);
        Date value = (Date) m.invoke(model);
        System.out.println("Data Type: Date");
        if (value != null) {
          System.out.println("Property value: " + value);
        } else {
          System.out.println("Property value: Null!");
        }
      }
    }
  }
}

