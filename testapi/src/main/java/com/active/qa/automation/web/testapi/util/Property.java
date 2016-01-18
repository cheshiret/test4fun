package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.exception.NotSupportedException;

import java.util.*;

/**
 * A Property object maintain a pair of key-values. It is primarily used for keep the Html Object properties
 * It also wrapped convenient methods for manipulating Html object properties
 * Created by tchen on 1/11/2016.
 */
public class Property {
  private String name;
  private Object value;

  public Property() {
    this(null, null);
  }

  public Property(String name, Object value) {
    this.name = name;
    this.value = value;
  }

  public String getPropertyName() {
    return name;
  }

  public Object getPropertyValue() {
    return value;
  }

  public String toString() {
    String toReturn = "";

    if (name != null && name.length() > 0) {
      toReturn += name;
    } else {
      return "Null";
    }

    if (value == null) {
      value = "Null";

    }
    toReturn += "=\"" + value + "\"";

    return toReturn;
  }

  /**
   * Get a String description all test object property values provided
   *
   * @param properties
   * @return
   */
  public static String propertyToString(Property[] properties) {
    String toString = "";

    for (int i = 0; properties != null && i < properties.length; i++) {
      if (i == 0) {
        toString = "TestObject with property: \"";
      }
      toString += properties[i].getPropertyName() + "=";
      toString += properties[i].getPropertyValue().toString();
      if (i != properties.length - 1) {
        toString += ",";
      }
    }
    toString += "\"";

    return toString;

  }

  public static Property[] getPropertyArray(String propertyKey1, Object value1, String propertyKey2, Object value2) {
    Property[] p = new Property[2];
    p[0] = new Property(propertyKey1, value1);
    p[1] = new Property(propertyKey2, value2);

    return p;
  }

  public static Property[] toPropertyArray(Object... keyvalues) {
    if (!NumberUtil.isEven(keyvalues.length)) {
      throw new NotSupportedException("The array's length is not even.");
    }

    List<Property> p = new ArrayList<Property>();
    for (int i = 0; i < keyvalues.length; i = i + 2) {
      if (keyvalues[i] != null && keyvalues[i + 1] != null) {
        p.add(new Property((String) keyvalues[i], keyvalues[i + 1]));
      }
    }

    if (p.size() > 0) {
      return p.toArray(new Property[0]);
    } else {
      return null;
    }
  }

  /**
   * concat the 2 property array into one. If there are duplicated properties, the later one will overwrite the old one
   *
   * @param first
   * @param others
   * @return
   */
  public static Property[] concatPropertyArray(Property[] first, Property... others) {
    Map<String, Object> map = propertyArrayToMap(first);

    for (Property p : others) {
      map.put(p.getPropertyName(), p.getPropertyValue());
    }
    return mapToPropertyArray(map);
  }

  public static Property[] concatPropertyArray(Property[] first, Object... others) {
    return concatPropertyArray(first, toPropertyArray(others));
  }

  public static Map<String, Object> propertyArrayToMap(Property[] props) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (Property p : props) {
      map.put(p.getPropertyName(), p.getPropertyValue());
    }

    return map;
  }

  public static Property[] mapToPropertyArray(Map<String, Object> map) {
    List<Property> list = new ArrayList<Property>();

    Set<String> keys = map.keySet();
    for (String k : keys) {
      list.add(new Property(k, map.get(k)));
    }

    return list.toArray(new Property[0]);
  }

  public static Property[] distinc(Property[] props) {
    return mapToPropertyArray(propertyArrayToMap(props));
  }

  public static Property[] addToPropertyArray(Property[] properties, String key, Object value) {
    boolean exists = false;
    for (Property p : properties) {
      if (p.getPropertyName().equalsIgnoreCase(key)) {
        AutomationLogger.getInstance().debug("key " + key + " exists: " + Property.propertyArrayToString(properties));
        p.value = value;//overwrite the value
        exists = true;
        break;
      }
    }
    if (exists) {
      return properties;
    } else {
      Property[] newProperties = new Property[properties.length + 1];
      System.arraycopy(properties, 0, newProperties, 0, properties.length);
      newProperties[properties.length] = new Property(key, value);
      return newProperties;
    }
  }

  public static String propertyArrayToString(Property... properties) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < properties.length; i++) {
      if (i > 0) {
        buf.append(",");
      }
      buf.append(properties[i].getPropertyName() + "=" + properties[i].getPropertyValue());
    }

    return buf.toString();
  }

  public static List<Property[]> atList(Property[]... properties) {
    List<Property[]> list = new ArrayList<Property[]>();

    for (Property[] p : properties) {
      list.add(p);
    }
    return list;
  }

  public static List<Property[]> combineToList(Property[] p, List<Property[]> list) {
    List<Property[]> newList = new ArrayList<Property[]>();
    newList.add(p);
    newList.addAll(list);
    return newList;
  }

  public static List<Property[]> combineToList(List<Property[]> list, Property[] p) {
    list.add(p);
    return list;
  }

  @Override
  public boolean equals(Object obj) {
    boolean equals = false;
    if (obj instanceof Property) {
      equals = name == ((Property) obj).name && value.equals(((Property) obj).value);
    }

    return equals;
  }

  public static String propertyToString(List<Property[]> pList) {
    String toString = "";

    for (int i = 0; pList != null && i < pList.size(); i++) {
      if (i == 0) {
        toString = "TestObject with property: \"";
      }

      toString += propertyToString(pList.get(i));
      if (i != pList.size() - 1) {
        toString += "|";
      }
    }
    toString += "\"";

    return toString;

  }

  public static Object getValue(Property[] properties, String key) {
    for (Property p : properties) {
      if (p.name.equals(key)) {
        return p.value;
      }
    }

    return null;
  }
}

