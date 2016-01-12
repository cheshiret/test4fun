package com.active.qa.automation.web.testapi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Wrap the methods for retrieving and manipulating keyboard input
 * Created by tchen on 1/11/2016.
 */
public class Keyboard {
    private static BufferedReader inputStream = new BufferedReader(
            new InputStreamReader(System.in));

    /**
     *  Get an integer from the user and return it
     */
    public static int getInteger() {
        try {
            String in=inputStream.readLine();
            if(in==null) {
                return 0;
            } else {
                return (Integer.valueOf(in.trim()).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *  Get a double from the user and return it
     */
    public static double getDouble() {
        try {
            String in=inputStream.readLine();
            if(in==null) {
                return 0.0;
            } else {
                return (Double.valueOf(in.trim()).doubleValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     *  Get a float from the user and return it
     */
    public static float getFloat() {
        try {
            String in=inputStream.readLine();
            if(in==null) {
                return 0.0f;
            } else {
                return (Float.valueOf(in.trim()).floatValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    /**
     *  Get a string of text from the user and return it
     */
    public static String getString() {
        try {
            return inputStream.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Get a char from the user and return it
     */
    public static char getCharacter() {
        try {

            String in = inputStream.readLine();
            if(in==null) {
                return (char) 0;
            }else if (in.trim().length() == 0)
                return (char) 0;
            else
                return (in.trim().charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return (char) 0;
        }
    }

    /**
     * Get a boolean from the user and return it
     */
    public static boolean getBoolean() {
        try {
            String aLine=inputStream.readLine();
            if(aLine==null) {
                return false;
            } else {
                return (Boolean.valueOf(aLine.trim()).booleanValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

