package com.active.qa.automation.web.testapi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * Created by tchen on 1/11/2016.
 */

public class DisplayMail {

    //Mail Server
    public static final String MAIL_POP3_SERVER = "raonmail.reserveamerica.com";

    public static final String MAIL_DOMAIN = "I2K-NET";

    public static final String MAIL_PASSWORD = "password123";

    public static final String MAIL_SUFFIX = "reserveamerica.com";

    /**
     * Method for retrieving messages from a POP3 mailbox
     *
     * @param mailUser   the user associated with the mailbox
     * @return  a List of Strings holding the information for each e-mail in the mailbox
     */
    public static List<String> fetchMail(String mailUser) {

        List<String> foundMessages = null;

        try {
            // connect on port 110 (POP3)
            System.out.println("Connect to " + MAIL_POP3_SERVER + ":110");
            Socket s = new Socket(MAIL_POP3_SERVER, 110);
            BufferedReader in = new BufferedReader(new InputStreamReader(s
                    .getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s
                    .getOutputStream()));
            DisplayMail mail = new DisplayMail();
            mail.login(in, out, mailUser, MAIL_PASSWORD); //MAIL_DOMAIN+"\\"+
            int i = mail.check(in, out);
            if (i == 0) {
                System.out.println("No mail waiting.");
            } else {

                foundMessages = new ArrayList<String>();

                for (int j = 1; j <= i; j++) {
                    String msg = mail.get(in, out, j);
                    System.out.println("*****");
                    System.out.println(msg);
                    System.out.println("*****");

                    foundMessages.add(msg);
                }
                //
                // If the mail was removed from the server
                // (see getMail()) then we must COMMIT with
                // the "QUIT" command :
                //   send(out, "QUIT");
                //
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundMessages;
    }

    /**
     *
     usage :
     DisplayMail [mailServer] [user] [password]
     (will not delete mail on the server)

     * @param arg
     */
    public static void main(String arg[]) {

        try {
            // connect on port 110 (POP3)
            System.out.println("Connect to " + arg[0] + ":110");
            Socket s = new Socket(arg[0], 110);
            BufferedReader in = new BufferedReader(new InputStreamReader(s
                    .getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s
                    .getOutputStream()));
            DisplayMail mail = new DisplayMail();
            mail.login(in, out, arg[1], arg[2]);
            int i = mail.check(in, out);
            if (i == 0) {
                System.out.println("No mail waiting.");
            } else {
                for (int j = 1; j <= i; j++) {
                    String msg = mail.get(in, out, j);
                    System.out.println("*****");
                    System.out.println(msg);
                    System.out.println("*****");
                }
                //
                // If the mail was removed from the server
                // (see getMail()) then we must COMMIT with
                // the "QUIT" command :
                //   send(out, "QUIT");
                //
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(BufferedReader in, BufferedWriter out, int i)
            throws IOException {
        String s = "";
        String t = "";
        send(out, "RETR " + i);
        while (((s = in.readLine()) != null) && (!(s.equals(".")))) {
            t += s + "\n";
        }
        //
        // To remove the mail on the server :
        //   send(out, "DELE "+i);
        //   receive(in);
        //
        return t;
    }

    /**
     * Write a string message to out stream
     * @param out output stream
     * @param s message
     * @throws IOException
     */
    private void send(BufferedWriter out, String s) throws IOException {
        System.out.println(s);
        out.write(s + "\r\n");
        out.flush();
    }

    /**
     * Read message from a input stream
     * @param in input stream
     * @return a line message in a inputStream
     * @throws IOException
     */
    private String receive(BufferedReader in) throws IOException {
        String s = in.readLine();
        System.out.println(s);
        return s;
    }

    private void login(BufferedReader in, BufferedWriter out, String user,
                       String pass) throws IOException {
        receive(in);
        //       send(out, "HELO theWorld");
        //       receive(in);
        send(out, "USER " + user);
        receive(in);
        send(out, "PASS " + pass);
        receive(in);
    }

    /**
     * Get messages number in InputStream
     * @param in
     * @param out
     * @return int number of messages
     * @throws IOException
     */
    private int check(BufferedReader in, BufferedWriter out) throws IOException {
        return getNumberOfMessages(in, out);
    }

    /**
     * Get messages number
     * @param in Input Stream
     * @param out Output Stream
     * @return int number of messages
     * @throws IOException
     */
    public int getNumberOfMessages(BufferedReader in, BufferedWriter out)
            throws IOException {
        int i = 0;
        String s;

        send(out, "LIST");
        receive(in);
        while ((s = receive(in)) != null) {
            if (!(s.equals("."))) {
                i++;
            } else {
                return i;
            }
        }
        return 0;
    }
}

