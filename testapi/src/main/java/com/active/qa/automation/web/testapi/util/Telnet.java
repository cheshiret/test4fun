package com.active.qa.automation.web.testapi.util;

import java.io.InputStream;
import java.io.PrintStream;
import org.apache.commons.net.telnet.TelnetClient;

/**
 * Provide functions to login to any servers via telnet
 * Created by tchen on 1/11/2016.
 */
public class Telnet {
    private TelnetClient telnet = new TelnetClient();

    private InputStream in;   //Input file

    private PrintStream out;  //Output file

    private char prompt = '>';

//	private char prompt1 = '$';

    /**
     * Write password into stream
     * @param password
     */
    public void su(String password) {
        try {
            write("su");
            readUntil("Password: ");
            write(password);
            prompt = '#';
            readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read all information from input stream to a string buffer
     * @param pattern
     * @return a string contain all information of the input stream
     */
    public String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuffer sb = new StringBuffer();
//			boolean found = false;
            char ch = (char) in.read();
            while (true) {
                System.out.print(ch);
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Write given string into Output Steam
     * @param value
     */
    public void write(String value) {
        try {
            out.println(value);
            out.flush();
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create Input/Output Steam connection with given IP
     * @param ip
     */
    public void connect(String ip) {
        try {
            telnet.connect(ip, 23);
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write command into stream
     * @param command
     * @return
     */
    public String sendCommand(String command) {
        try {
            write(command);
            return readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Disconnect connection
     *
     */
    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Login with given user and pwd
     * @param user
     * @param pw
     */
    public void login(String user, String pw) {
        try {
            this.readUntil("login: ");
            this.write(user);
            this.readUntil("Password: ");
            this.write(pw);
            this.readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

