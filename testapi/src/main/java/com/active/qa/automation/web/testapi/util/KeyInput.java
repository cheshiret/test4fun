package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.NotSupportedException;

/**
 * Representations of the most useful pressable keys that aren't text in the framework
 * Created by tchen on 1/11/2016.
 */
public class KeyInput {
    public static final int BACKSPACE=0;
    public static final int ENTER=1;
    public static final int SHIFT=2;	//RIGHT SHIFT
    public static final int LEFT_SHIFT=3;
    public static final int CTRL=4;		//RIGHT CTRL
    public static final int LEFT_CTRL=5;
    public static final int ALT=6;
    public static final int LEFT_ALT=7;
    public static final int TAB=8;

    public static final int INSERT=9;
    public static final int DELETE=10;
    public static final int HOME=11;
    public static final int END=12;
    public static final int PAGE_UP=13;
    public static final int PAGE_DOWN=14;

    public static final int ARROW_UP=15;
    public static final int ARROW_DOWN=16;
    public static final int ARROW_LEFT=17;
    public static final int ARROW_RIGHT=18;

    public static final int ESC=19;

    protected static final int TEXT=100;
    protected static final int NON_TEXT=101;
    protected static final int CHORD=102;
    private String text;
    private int non_text_key;
    private int type;

    private KeyInput(String s) {
        type=TEXT;
        text=s;
        non_text_key=-1;
    }

    private KeyInput(int non_text_key) {
        verify(non_text_key);
        this.non_text_key=non_text_key;
        type=NON_TEXT;
        text=null;
    }

    private KeyInput(int non_text_key,String text) {
        verify(non_text_key);
        this.non_text_key=non_text_key;
        type=CHORD;
        this.text=text;
    }

    private void verify(int non_text_keycode) {
        if(non_text_keycode<0 || non_text_keycode>19) {
            throw new NotSupportedException("Key type code "+non_text_keycode+" is not supported.");
        }
    }

    public static KeyInput get(int non_text_key) {
        return new KeyInput(non_text_key);
    }

    public static KeyInput get(String text) {
        return new KeyInput(text);
    }

    public static KeyInput get(int non_text_key,String text) {
        return new KeyInput(non_text_key, text);
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getNonTextKey() {
        return non_text_key;
    }

}

