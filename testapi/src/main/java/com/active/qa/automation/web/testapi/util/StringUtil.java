package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.InvalidDataException;
import com.active.qa.automation.web.testapi.ItemNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wrap the convenient methods for manipulate Strings
 * Created by tchen on 1/11/2016.
 */
public class StringUtil {
    /**
     * For RFT, &nsbp; will be parsed as \\s
     * For Selenium, it will be parsed as \\xA0 (non-breaking space)
     */
    public static final String NBSP_REGEX = "(\\xA0|\\s)";

    public static final String EMPTY = "";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String SPACE = " ";

    public static boolean isEmpty(String str) {
        return str ==null || str.trim().length()<1;
    }

    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Get a String represents the content of the array of Objects with
     * delimiter as ","
     *
     * @param objects
     * @return
     */
    public static String arrayToString(Object[] objects) {
        String toString = "";

        for (int i = 0; objects != null && i < objects.length; i++) {
            toString += objects[i].toString();

            if (i != objects.length - 1) {
                toString += ",";
            }
        }

        return toString;
    }

    public static String arrayToString(String[] texts, boolean withSingleQuote, String delimit) {
        StringBuffer text=new StringBuffer();

        for(int i=0;i<texts.length;i++) {
            if(withSingleQuote) {
                text.append("'");
            }
            text.append(texts[i]);

            if(withSingleQuote) {
                text.append("'");
            }
            if(i<texts.length-1) {
                text.append(delimit);
            }
        }
        return text.toString();

    }

    public static String arrayToString(String[] texts, boolean withSingleQuote) {
        return arrayToString(texts,withSingleQuote,",");

    }

    public static String arrayToString(String[] texts) {
        return arrayToString(texts,false,",");

    }

    public static List<String> arrayToList(String[] texts){
        List<String> list = new ArrayList<String>();
        for(String tmp:texts){
            list.add(tmp);
        }
        return list;
    }

    public static List<String> convertListStrCase(List<String> beforeConvert,boolean toLowerCase){
        List<String> afterConvert = new ArrayList<String>();
        for(String str:beforeConvert){
            if(toLowerCase){
                afterConvert.add(str.toLowerCase());
            }else{
                afterConvert.add(str.toUpperCase());
            }
        }
        return afterConvert;
    }

    public static String listToString(List<String> texts, boolean withSingleQuote) {

        StringBuffer text=new StringBuffer();
        int size=texts.size();

        for(int i=0;i<size;i++) {
            if(withSingleQuote) {
                text.append("'");
            }
            text.append(texts.get(i));

            if(withSingleQuote) {
                text.append("'");
            }
            if(i<size-1) {
                text.append(",");
            }
        }
        return text.toString();
    }

    public static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }

    /**
     * Get the first digit num string in a string
     * @param s
     * @return
     */
    public static int getDigits(String s) {
        return StringUtil.getAllDigits(s)[0];
    }

    /**
     * Get all digits in a string
     * @param s
     * @return int array
     */
    public static int[] getAllDigits(String s) {
        String[] d = RegularExpression.getMatches(s, "[0-9]+");
        if (d.length > 0) {
            int[] toReturn = new int[d.length];
            for (int i = 0; i < d.length; i++)
                toReturn[i] = Integer.parseInt(d[i]);
            return toReturn;
        } else
            throw new ItemNotFoundException("There is no digits in String " + s);
    }

    /**
     * Get subString start from index after subString1 and end before subString2 in originalString
     * @param originalString
     * @param subString1
     * @param subString2
     * @return
     */
    public static String getSubstring(String originalString, String subString1,
                                      String subString2) {

        return originalString.substring(
                originalString.indexOf(subString1) + subString1.length(),
                originalString.indexOf(subString2)).trim();
    }

    public static String convertToRegex(String text) {
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
        text="^(\\W)*"+text+"(\\W)*$";

        return text;
    }

    public static String[] distinctFilter(String[] items) {
        List<String> list=new ArrayList<String>();
        for(int i=0;i<items.length;i++) {
            if(!list.contains(items[i])) {
                list.add(items[i]);
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * Merge any count of String Array. (use when we upgrade to Java 5.0/6.0)
     *
     * @author mvantuyl    (http://forum.java.sun.com/thread.jspa?threadID=202127&messageID=676603)
     * @param arrays many arrays
     * @return merged array
     */
    public static String[] mergeArrays(String[] array1, String[] array2) {
        String[] merged = new String[array1.length + array2.length];
        for (int x = 0; x < array1.length; x++) {
            merged[x] = array1[x];
        }
        for (int x = 0; x < array2.length; x++) {
            merged[x + array1.length] = array2[x];
        }

        return merged;
    }

    /**
     * Check given toCheck string contain given tokenString
     * @param tokenString
     * @param toCheck
     * @return if contain return true,else false
     */
    public static boolean checkTokensExist(String tokenString, String toCheck) {
        if (tokenString == null || tokenString.equals("")) {
            return true;
        }

        String[] tokens = tokenString.split("\\|");
        for (int i = 0; i < tokens.length; i++) {
            if (toCheck.indexOf(tokens[i]) == -1) {
                return false;
            }
        }
        return true;
    }

    public static InputStream stringToInputStream(String str) {
        byte[] bytes=str.getBytes();
        return new ByteArrayInputStream(bytes);
    }

    public static String autoitxBytesToString(byte[] bytes) {
        StringBuffer sb=new StringBuffer();
        for(byte b:bytes) {
            if(b==0) {
                continue;
            } else {
                sb.append((char)b);
            }
        }
        return sb.toString();
    }

    /**
     * Generate random string
     * @param digit
     * @param isUpperCase
     * @return
     */
    public static String getRandomString(int digit, boolean isUpperCase) {
        StringBuffer sb = new StringBuffer();
        String baseString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        int position = -1;
        for(int i = 0; i < digit; i ++) {
            position = random.nextInt(26);//0-25
            sb.append(baseString.charAt(position));
        }

        return isUpperCase ? sb.toString() : sb.toString().toLowerCase();
    }

    public static List<String[]> arraySplit(String[] strs, int limit) {
        List<String[]> list=new ArrayList<String[]>();
        if(strs.length>limit) {
            int size=strs.length/limit;
            if(strs.length%limit!=0) {
                size++;
            }
            for(int i=0;i<size;i++) {
                int start=i*5;
                int end=start+limit;
                end = Math.min(end, strs.length);
                String[] s=Arrays.copyOfRange(strs, start, end);
                list.add(s);
            }
        } else {
            list.add(strs);
        }

        return list;
    }

    public static String[] arrayExtend(String[] strs, String toAppend) {
        String[] newArray=new String[strs.length+1];
        System.arraycopy(strs, 0, newArray, 0, strs.length);
        newArray[strs.length]=toAppend;

        return newArray;
    }

    /**
     * normalize the space characters in the given text
     * @param text
     * @return
     */
    public static String normalize_space(String text) {
        StringBuilder str=new StringBuilder();
        int pre_state=-1;
        char[] chars=text.toCharArray();
        for(int i=0;i<chars.length;i++) {
            if(!Character.isSpaceChar(chars[i])) {
                if(pre_state==0) {
                    str.append(" ");
                }
                str.append(chars[i]);
                pre_state=1;

            } else if(pre_state!=-1) {
                pre_state=0;
            }
        }

        return str.toString();
    }

    /**
     * convert any non-ASCII space characters in to ASCII space
     * @param text
     * @return
     */
    public static String convertSpaceUnicode2ASCII(String text) {
        StringBuilder str=new StringBuilder();
        char[] chars=text.toCharArray();
        for(int i=0;i<chars.length;i++) {
            if(Character.isSpaceChar(chars[i])) {
                str.append(" ");
            } else {
                str.append(chars[i]);
            }
        }

        return str.toString();
    }

    public static String getMax(String[] values) {
        if(values.length<1) {
            return null;
        }

        int max=Integer.parseInt(values[0]);;

        for(int i=1;i<values.length;i++) {
            max=Math.max(max, Integer.parseInt(values[i]));
        }

        return Integer.toString(max);
    }

    public static String[] splitByComma(String input) {
        return splitBy(input, COMMA);
    }

    public static String[] splitBySemicolon(String input) {
        return splitBy(input, SEMICOLON);
    }

    public static String[] splitBy(String input, String separator) {
        return Pattern.compile(separator).split(input, -1);
    }

    /**
     * Get the first numbers in the strings, set the scale as two decimal and compare them
     * @param s1
     * @param s2
     * @return
     * 		0 - the two numbers are equal
     * 		1 - the number in the first string is greater than the one in the second string
     * 		-1 - the number in the first string is less than the one in the second string
     * @author Lesley Wang
     * @date May 10, 2012
     */
    public static int compareNumStrings(String s1, String s2) {
        String reg = "\\-?\\d+\\.?\\d*";
        String num1[] = RegularExpression.getMatches(s1, reg);
        String num2[] = RegularExpression.getMatches(s2, reg);
        if (num1.length > 0 && num2.length > 0) {
            return Double.compare(Double.parseDouble(num1[0]), Double.parseDouble(num2[0]));
        } else {
            throw new InvalidDataException("The string " + s1 + " or " + s2 + " doesn't contain any numbers. Please check!");
        }
    }

    /**
     * Find given pattern string and return in given string content
     * @param content
     * @param givenPattern
     * @return
     */
    public static String[] findString(String content,String strPattern){
        return RegularExpression.getMatches(content,strPattern);
    }

    /* refactor "$7.00" to "$7."
     * refactor "$7.50" to "$7.5"
     * */
    public static String refactAmountValue(String amount)
    {

        int len = amount.length();
        for(int j=(len-1); j>=0;j--)
        {
            String s= amount.substring(j, j+1);

            if(!(java.lang.Character.isDigit(s.charAt(0)))||(!s.equalsIgnoreCase("0")))
            {
                break;
            }else{
                amount = amount.substring(0, j);
                AutomationLogger.getInstance().info(amount);
            }

        }

        return amount;
    }

    /**
     * Get the substring after subStr in OriStr. If the oriStr doesn't contain the subStr, return null
     * @param oriStr
     * @param subStr
     * @return
     * @author Lesley Wang
     * @Date  Jun 11, 2012
     */
    public static String getSubString(String oriStr, String subStr) {
        if (oriStr.indexOf(subStr) >= 0) {
            return oriStr.substring(
                    oriStr.indexOf(subStr) + subStr.length()
            ).trim();
        } else {
            AutomationLogger.getInstance().info("The string '" + oriStr + "' doesn't contain '" + subStr + "'!");
            return null;
        }
    }


    /**
     * Convert a String to BigDecimal By scale and then return as String
     * If the return String was d+\\.0+, will keep it as d+\\.00
     * @param scale
     * @param value
     * @return
     */
    public static String convertStringToBigDecimalByScale(String value, int scale){
        BigDecimal decimal = new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP);
        String str = decimal.toString();
        int index = str.indexOf('.');
        if(str.substring(index).matches(".0+")){
            str = str.replaceAll("\\.0+", ".00");
        }
        return str;
    }

    /**
     *
     * @param html source text
     * @return String which is deleted all Html tag content.
     */
    public static String replaceHtml(String html){
        String regEx="<.+?>";
        Pattern p=Pattern.compile(regEx);
        Matcher m=p.matcher(html);
        String s=m.replaceAll("");
        return s;
    }

    /**
     * Convert all variable of Object to String.(except final)
     * @param: obj, of which variables you want to convert...
     * result: [var1=value1][var2=value2].....
     * */
    public static String ObjToString(Object obj){

        String result="";
        try {

            for(Field f : obj.getClass().getDeclaredFields()) {

                if(Modifier.isFinal(f.getModifiers()))
                    continue;

                f.setAccessible(true);
                result +="["+f.getName()+"="+f.get(obj)+"]";
            }

            for(Field sf : obj.getClass().getSuperclass().getDeclaredFields()) {

                if(Modifier.isFinal(sf.getModifiers()))
                    continue;

                sf.setAccessible(true);
                result +="["+sf.getName()+"="+sf.get(obj)+"]";
            }


        } catch (IllegalAccessException e) {

            e.printStackTrace();
        }
        return result;

    }

    public static String inputStreamToString(InputStream input) throws IOException {
        StringWriter  writer=new StringWriter();
        InputStreamReader reader=new InputStreamReader(input);

        char[] buffer = new char[1024*4];
        int n = 0;
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
        }
        return writer.toString();

    }

    /**
     * refactor "9638527410" to "9638******"
     * @param original
     * @param plaintextLength
     * @return
     */
    public static String encryptString(String original, int plaintextLength){
        String encryption = original.substring(0, plaintextLength);
        String temp = original.substring(plaintextLength, original.length()).replaceAll(".", "*");
        encryption += temp;
        return encryption;
    }

    /**
     * Encrypt String with the replacement, keeping the plain text from plaintextBeginIndex to plaintextEndIndex - 1
     * @param original
     * @param plaintextBeginIndex
     * @param plaintextEndIndex
     * @param replacement
     * @return
     * @author Lesley Wang
     * Apr 15, 2013
     */
    public static String encryptString(String original, int plaintextBeginIndex, int plaintextEndIndex, String replacement) {
        String encryption = original.substring(plaintextBeginIndex, plaintextEndIndex);
        String temp1 = original.substring(0, plaintextBeginIndex).replaceAll(".", replacement);
        String temp2 = original.substring(plaintextEndIndex, original.length()).replaceAll(".", replacement);
        encryption = temp1 + encryption + temp2;
        return encryption;
    }

    /**
     * Encrypt string with the following rule:
     * x = 'number of character to show'; y = 'number of character to mask'; z = 'number of character in the identifier number'
     * when y < z <= x + y, 'number of character to mask' will be  masked from the beginning
     * when z > x + y, 'number of character to show' will be displayed from the end
     * @param num
     * @return
     * @author Lesley
     * @date Apr 24, 2013
     */
    public static String encryptString(String oriNum, String mask, int hideNum, int showNum) {
        int length = oriNum.length();
        if (length <= hideNum + showNum) {
            return StringUtil.encryptString(oriNum, hideNum, length, mask);
        } else {
            return StringUtil.encryptString(oriNum, length - showNum, length, mask);
        }
    }

    public static String formatPhoneNumToJustNumbers(String phone){
        return phone.replace("(", "").replace(") ", "").replace("-", "");
    }

    public static String formatPhoneNumWithDash(String phone){
        if(isEmpty(phone)){
            return "";
        }
        String part1 = phone.substring(0, 3);
        String part2 = phone.substring(3,6);
        String part3 = phone.substring(6);
        StringBuffer sb = new StringBuffer();
        sb.append(part1).append("-").append(part2).append("-").append(part3);
        return sb.toString();
    }

    /**
     * Reverse String split by given mark,
     * @param beforeReverse--'AAA-12345'
     * @param mark-'-'
     * @return-'12345-AAA'
     */
    public static String[] reverseStringBy(String[] beforeReverse,String mark){
        String[] afterReverse = new String[beforeReverse.length];
        for(int i=0;i<beforeReverse.length;i++){
            afterReverse[i] = beforeReverse[i].split(mark)[1]+mark+beforeReverse[i].split(mark)[0];
        }
        return afterReverse;
    }

    /**
     * Verify if the string list sort by alphabetically
     * @param oriList
     * @return
     * @author Lesley Wang
     * @date Dec 13, 2012
     */
    public static boolean verifyStringListSortByAlphabetically(List<String> oriList) {
        List<String> afterSorting = new ArrayList<String>();
        boolean result = true;
        afterSorting.addAll(oriList);
        Collections.sort(afterSorting);
        if (!oriList.equals(afterSorting)) { // compare the two list
            result = false;
            AutomationLogger.getInstance().error("The string list does NOT sort by alphabetically ascending!");
        } else {
            AutomationLogger.getInstance().info("The string list sorts by alphabetically ascending correctly!");
        }
        return result;
    }

    public static boolean verifyStringListSortByReverse(List<String> oriList) {
        List<String> afterSorting = new ArrayList<String>();
        afterSorting.addAll(oriList);

        String[] afterSortingArr = new String[afterSorting.size()];
        afterSorting.toArray(afterSortingArr);

        boolean result = true;
        Arrays.sort(afterSortingArr, Collections.reverseOrder());

        afterSorting.clear();
        afterSorting = Arrays.asList(afterSortingArr);

        if (!oriList.equals(afterSorting)) { // compare the two list
            result = false;
            AutomationLogger.getInstance().error("The string list does NOT sort by Reverse Order!");
        } else {
            AutomationLogger.getInstance().info("The string list sorts by Reverse Order correctly!");
        }
        return result;
    }

    /**
     * Escapes XML reserved characters (assumes UTF-8 or UTF-16 as encoding)
     *
     * @param content	The content to be escaped
     * @return			The escaped string
     */
    public static String escapeXMLReserved(String content) {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '<') {
                buffer.append(";&lt;");
            } else if (c == '>') {
                buffer.append(";&gt;");
            } else if (c == '&') {
                buffer.append(";&amp;");
            } else if (c == '"') {
                buffer.append(";&quot;");
            } else if (c == '\'') {
                buffer.append(";&apos;");
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }



    public static String unNull( String tagName ) {
        return tagName == null ? "" : tagName;
    }

    public static boolean matchOrEqual(Object value, String text) {
        if (value instanceof RegularExpression) {
            return ((RegularExpression) value).match(text);
        } else {
            return ((String) value).equalsIgnoreCase(text);
        }
    }

    public static String[] splitStringToArray(String value, String replacedSeparator, String separator) {
        ArrayList<String> list = new ArrayList<String>();
        if (value.contains(replacedSeparator)) {
            value = value.replace(replacedSeparator, "");
            String[] tmp = value.split(separator);
            int arrayLen = tmp.length;
            for (int i = 0; i < arrayLen; i++) {
                String tmp_value = tmp[i];
                list.add(tmp_value);
            }
        } else {
            // this is for single string.
            list.add(value);
        }

        return list.toArray(new String[0]);
    }
}

