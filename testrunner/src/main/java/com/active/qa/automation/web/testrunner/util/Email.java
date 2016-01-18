package com.active.qa.automation.web.testrunner.util;


import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * Created by tchen on 1/18/2016.
 */
public class Email {
    private static Logger logger = Logger.getLogger(Email.class);
    public String from,to,cc,bcc,subject,text,bodyFormat;
    public String[] attachments;

    public Email() {
        from=TestProperty.get("mail.from","noreply@reserveamerica.com");
        to=TestProperty.get("mail.to","James.Du@activenetwork.com");
        cc="";
        bcc="";
        subject="";
        text="";
        attachments=null;
        bodyFormat="html";
    }

    public Email(String from, String to,String subject,String msg,String[] attachments) {
        this.from=from;
        this.to=to;
        this.subject=subject;
        this.text=msg;
        this.attachments=attachments;
        this.bodyFormat="html";
    }

    public void send() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", TestProperty.get("mail.smtp.host"));
        String port=TestProperty.get("mail.smtp.port");
        if(port!=null) {
            props.put("mail.smtp.port", Integer.parseInt(port));
        }
        Session session;

        if(Boolean.valueOf(TestProperty.get("mail.smtp.auth"))==true) {
            props.put("mail.smtp.auth", true);
            Authenticator auth= new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    String username = TestProperty.get("mail.smtp.user");
                    String password = CryptoUtil.decrypt(TestProperty.get("mail.smtp.pw"));
                    return new PasswordAuthentication(username, password);
                }
            };
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getInstance(props, null);
        }

        session.setDebug(false);

        try {
            // create a message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));

            msg.setRecipients(Message.RecipientType.TO, getAddress(to));

            if(cc.length()>0) {
                msg.setRecipients(Message.RecipientType.CC, getAddress(cc));
            }

            //always BCC to the master.to
            String master=TestProperty.get("mail.master.to","James.Du@activenetwork.com");
            if(bcc.length()<1) {
                bcc=master;
            } else if (!bcc.contains(master)) {
                bcc +=";"+master;
            }
            msg.setRecipients(Message.RecipientType.BCC, getAddress(bcc));

            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // Attach file with message
            if(attachments != null)
            {	MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(text);
                MimeMultipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);

                for(int i = 0; i <attachments.length; i++)
                {	File file = new File(attachments[i]);
                    if(file.exists())
                    {	MimeBodyPart mbp2 = new MimeBodyPart();
                        FileDataSource fds = new FileDataSource(attachments[i]);
                        mbp2.setDataHandler(new DataHandler(fds));
                        mbp2.setFileName(fds.getName());
                        mp.addBodyPart(mbp2);
                    }
                }
                msg.setContent(mp);
            }
            else{
                if(!text.contains("<body>")) {
                    bodyFormat="text";
                }

                if(bodyFormat.equalsIgnoreCase("html")) {
                    Multipart mainPart = new MimeMultipart();
                    BodyPart html = new MimeBodyPart();
                    html.setContent(text, "text/html; charset=utf-8");
                    mainPart.addBodyPart(html);
                    msg.setContent(mainPart);
                } else if(bodyFormat.equalsIgnoreCase("text")) {
                    msg.setText(text);
                } else {
                    msg.setText(text);
                }
            }

            //send the email
            logger.info("Sending email...");
            try {
                Transport.send(msg);

            } catch(Exception e) {
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TestProperty.get("mail.to")));
                Transport.send(msg);
            }
            logger.info("Done.");
        } catch (MessagingException mex) {
            logger.debug("--Exception handling in msgsendsample.java");

            logger.debug("", mex);
            mex.printStackTrace();

            Exception ex = mex;
            do {

                if (ex instanceof SendFailedException) {
                    SendFailedException sfex = (SendFailedException)ex;
                    Address[] invalid = sfex.getInvalidAddresses();
                    if (invalid != null) {
                        logger.debug("    ** Invalid Addresses");
                        if (invalid != null) {
                            for (int i = 0; i < invalid.length; i++) {
                                logger.debug("         " + invalid[i]);
                            }
                        }
                    }
                    Address[] validUnsent = sfex.getValidUnsentAddresses();
                    if (validUnsent != null) {
                        logger.debug("    ** ValidUnsent Addresses");
                        if (validUnsent != null) {
                            for (int i = 0; i < validUnsent.length; i++) {
                                logger.debug("         " + validUnsent[i]);
                            }
                        }
                    }
                    Address[] validSent = sfex.getValidSentAddresses();
                    if (validSent != null) {
                        logger.debug("    ** ValidSent Addresses");
                        if (validSent != null) {
                            for (int i = 0; i < validSent.length; i++) {
                                logger.debug("         " + validSent[i]);
                            }
                        }
                    }
                }

                if (ex instanceof MessagingException) {
                    ex = ((MessagingException)ex).getNextException();
                }
                else {
                    ex = null;
                }

            } while (ex != null);
        }
    }

    public void asyncSend() {
        new AsyncEmail(this).start();
    }

    private static InternetAddress[] getAddress(String address) throws AddressException {
        String recipients[] =address.split(";");

        InternetAddress[] addresses = new InternetAddress[recipients.length];
        for (int i=0; i<recipients.length; i++) {
            addresses[i] = new InternetAddress(recipients[i]);
        }

        return addresses;
    }

    public static void sendErrorMessageToMaster(String subject,String text) {
        Email mail=new Email();
        mail.to=TestProperty.get("mail.master.to","James.Du@activenetwork.com");
        mail.subject=subject;
        mail.text=text;
        mail.bodyFormat="text";
        mail.send();
    }

    private class AsyncEmail extends Thread {
        Email e;
        AsyncEmail(Email e) {
            this.e=e;
        }

        public void run() {
            e.send();
        }
    }

    public static void main(String[] args) {
        TestProperty.load();
        String s = "ok, enough joke. I will bring some ice cream?";
        Email e=new Email("test", "lll@reserveamerica.com", "next week", s, null);
        //	MailNotification.sendNotification("cguodranalli@reserveamerica.com", "development@reserveamerica.com;qa@reserveamerica.com", "next week", s, null);
        e.send();
    }
}