package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.exception.InvalidDataException;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used as a plugin to process String with the defined logic.
 * Created by tchen on 1/18/2016.
 */
public class StringProcessor {
  private static final int REPLACE = 0;
  private static final int MATCHES = 1;
  private static final int BETWEEN = 2;
  private static final int BOOLEAN = 3;
  private static final int SUBSTRING = 4;

  private int processType;
  private List<String> data;

  private StringProcessor(int type, List<String> data) {
    this.processType = type;
    this.data = data;
  }

  public static StringProcessor replacor(String[] replacePairs) {
    if (replacePairs.length % 2 != 0) {
      throw new InvalidDataException("The size " + replacePairs.length + " of replacePairs array is not even number.");
    }
    return new StringProcessor(REPLACE, Arrays.asList(replacePairs));
  }

  public static StringProcessor matchor(String matchesPattern) {
    return new StringProcessor(MATCHES, Arrays.asList(matchesPattern));
  }

  public static StringProcessor betweenor(String before, String after) {
    return new StringProcessor(BETWEEN, Arrays.asList(before, after));
  }

  public static StringProcessor booleanor(boolean toValue, String... matchStrings) {
    List<String> data = new ArrayList<String>();
    data.addAll(Arrays.asList(matchStrings));
    data.add(Boolean.toString(toValue));
    return new StringProcessor(BOOLEAN, data);
  }

  public static StringProcessor subor(int start, int end) {
    return new StringProcessor(SUBSTRING, Arrays.asList(Integer.toString(start), Integer.toString(end)));
  }

  public static String pipe(String text, StringProcessor... processors) {
    for (StringProcessor p : processors) {
      if (p != null) {
        text = p.process(text);
      }
    }

    return text;
  }

  public String process(String text) {
    switch (processType) {
      case REPLACE:
        return replace(text);
      case MATCHES:
        return matches(text);
      case BETWEEN:
        return between(text);
      case BOOLEAN:
        return transferToBooleanString(text);
      case SUBSTRING:
        return subString(text);
      default:
        return text;
    }
  }

  private String replace(String text) {
    for (int i = 0; i < data.size(); i = i + 2) {
      text = text.replace(data.get(i), data.get(i + 1));
    }
    if (text.endsWith(",")) {
      text = text.substring(0, text.lastIndexOf(","));
    }
    return text;
  }

  private String matches(String text) {
    String[] tokens = new RegularExpression(data.get(0), false).getMatches(text);

    if (tokens == null || tokens.length < 1) {
      return "";
    } else {
      return tokens[0];
    }
  }

  private String between(String text) {
    String before = data.get(0);
    String after = data.get(1);
    if (!StringUtil.isEmpty(before)) {
      int i = text.indexOf(before);
      text = text.substring(i + before.length());
    }

    if (!StringUtil.isEmpty(after)) {
      int i = text.indexOf(after);
      text = text.substring(0, i);
    }

    return text;
  }

  private String transferToBooleanString(String text) {
    String toValue = data.get(data.size() - 1);
    List<String> toMatch = data.subList(0, data.size() - 1);//2
    if (toMatch.contains(text)) {
//		if(StringUtil.notEmpty(text) && toMatch.contains(text)) {//james[20140428] this logic is invalid, comment out because empty string may be matched to "true"
      return toValue;
    } else {
      return Boolean.toString(toValue.equalsIgnoreCase("false"));
    }
  }

  private String subString(String text) {
    int start = Integer.parseInt(data.get(0));
    int end = Integer.parseInt(data.get(1));

    return text.substring(start, end);
  }

}

