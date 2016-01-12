package com.active.qa.automation.web.testapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tchen on 1/11/2016.
 */
public class RegularExpression {
    private Pattern p;
    private boolean caseSensitive;


    public RegularExpression(String pattern,boolean caseSensitive) {
        this.caseSensitive=caseSensitive;
        if(caseSensitive) {
            p=Pattern.compile(pattern);
        } else {
            p=Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
        }
    }

    public void set(String pattern, boolean isCaseSensitive) {
        this.p=Pattern.compile(pattern);
        this.caseSensitive=isCaseSensitive;
    }

    public String getPattern() {
        return p.pattern();
    }

    public String getJsoupPattern() {
        if(!isCaseSensitive()) {
            return "(?i)"+p.pattern();
        } else {
            return p.pattern();
        }
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Match the given text with the pattern.
     * @param text
     * @return true if the text contains a substring matches the pattern, false otherwise
     */
    public boolean match(String text) {
        if(text==null) {
            return false;
        }
        return p.matcher(text).find();
    }

    public String toString() {
        return getPattern();
    }

    /**
     * Get the substrings that match the given regular expression
     * @param input
     * @return array of strings matching given pattern
     */
    public String[] getMatches(String input) {
        Matcher m = p.matcher(input);
        List<String> list=new ArrayList<String>();

        while (m.find()) {
            list.add(m.group());
        }

        return list.toArray(new String[0]);
    }

    /**
     * Get the string match the given regular expression
     * @param input
     * @param regex
     * @return string array store string match given regex
     */
//	public static String[] getMatches(String input, String regex) {
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher(input);
//		String text = "";
//		String delimit = "#&#@#";
//
//		while (m.find())
//			text += m.group() + delimit;
//
//		String[] result = text.split(delimit);
//
//		return result;
//
//	}

    public static String[] getMatches(String input,String pattern) {
        return getMatches(input,pattern,false);
    }

    public static String[] getMatches(String input, String pattern, boolean caseSensitive) {
        RegularExpression regex=new RegularExpression(pattern,caseSensitive);
        return regex.getMatches(input);
    }

    public static RegularExpression convert(String text, boolean caseSensitive) {
        text=text.replaceAll("\\(", "\\\\(");
        text=text.replaceAll("\\)", "\\\\)");
        text=text.replaceAll("\\$", "\\\\\\$");
        text=text.replaceAll("\\^", "\\\\^");
        text=text.replaceAll("\\.", "\\\\.");
        text=text.replaceAll("\\[", "\\\\[");
        text=text.replaceAll("\\]", "\\\\]");
        text=text.replaceAll("\\|", "\\\\|");
        text=text.replaceAll("\\*", "\\\\*");
        text=text.replaceAll("\\?", "\\\\?");
        text=text.replaceAll("\\{", "\\\\{");
        text=text.replaceAll("\\}", "\\\\}");
        text=text.replaceAll("\\+", "\\\\+");
        return new RegularExpression(text,caseSensitive);
    }

    public static boolean contains(String input, String pattern, boolean isCaseSensitive) {
        RegularExpression reg=new RegularExpression(pattern,isCaseSensitive);
        return reg.getMatches(input).length>0;
    }
}

