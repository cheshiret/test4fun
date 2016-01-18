package com.active.qa.automation.web.testapi.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import org.apache.log4j.Logger;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;


/**
 * Email utility class for sending and checking email
 * Created by tchen on 1/11/2016.
 */
public class Email {
    private static Logger logger = Logger.getLogger(Email.class);

    private Session session;

    private Store store;

    private boolean connected;

    private String provider;

    /**
     * Construct method to initialize basic email information
     *
     */
    public Email() {
        session=null;
        store=null;
        connected=false;
        provider="";
    }

    /**
     * Send out an email using the default host
     * @param from
     * @param to
     * @param subject
     * @param text
     * @param attachments
     */
    public static void send(String from, String to,String subject, String text, String[] attachments) {
        send(from,to,"","",subject,text,attachments);

    }

    public static void send(String from, String to,String cc,String bcc,String subject, String text, String[] attachments) {
        String key = "mail.smtp.host";
        String host=TestProperty.getProperty(key);
        send(host,from,to,cc,bcc,subject,text,attachments);
    }

    public static void send(String host, String from, String to,String subject, String text, String[] attachments) {
        send(host,from,to,"","",subject,text,attachments);
    }

    /**
     *
     * @param host - the SMTP host
     * @param from - from email address
     * @param to - to email addresses, multi-addresses should be separated by ";"
     * @param cc - cc email addresses, multi-addresses should be separated by ";"
     * @param bcc - bcc email addresses, multi-addresses should be separated by ";"
     * @param subject - email subject
     * @param text - email body text
     * @param attachments
     */
    public static void send(String host, String from, String to, String cc, String bcc, String subject, String text, String[] attachments) {
        try {
            //set recipients
            if(StringUtil.isEmpty(to)) {
                throw new ItemNotFoundException("Email to address is empty!");
            }

            //set cc
            String debugger=TestProperty.getProperty("notification.debug.to");
            if(StringUtil.isEmpty(cc) && StringUtil.notEmpty(debugger)) {
                cc=debugger;

            } else if(StringUtil.notEmpty(cc) && StringUtil.notEmpty(debugger)) {
                cc+=";"+debugger;
            }

            //set BCC
//			String master=TestProperty.getProperty("mail.master.to","James.Du@activenetwork.com");
            String master=TestProperty.getProperty("mail.master.to","tony.chen@activenetwork.com");
            if(StringUtil.isEmpty(bcc)) {
                //always BCC to the master
                bcc=master;
            } else {
                bcc+=";"+master;

            }

            Message msg=createMessage(host,from,to,cc,bcc,subject,text,attachments);
            send(msg);
        } catch (MessagingException mex) {
            logger.debug("--Exception handling in msgsendsample.java");

            logger.debug("", mex);
            mex.printStackTrace();

            Exception ex = mex;
            do {

                if (ex instanceof SendFailedException) {
                    SendFailedException sfex = (SendFailedException) ex;
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
                    ex = ((MessagingException) ex).getNextException();
                } else {
                    ex = null;
                }

            } while (ex != null);
        }
    }

    public static void send(Message msg) {
        //send the message
        logger.info("Sending message...");

        try {
            Transport.send(msg);
            logger.info("Done.");
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.warn("Failed to send email."+msg.toString(), e);
        }

    }

    public static Message createMessage(String host, String from, String to, String cc, String bcc, String subject, String text, String[] attachments) throws AddressException, MessagingException {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", host);
        String port=TestProperty.getProperty("mail.smtp.port");
        if(!port.equalsIgnoreCase("null")) {
            props.put("mail.smtp.port", Integer.parseInt(port));
        }
        Session session;

        if(Boolean.valueOf(TestProperty.getProperty("mail.smtp.auth"))==true) {
            props.put("mail.smtp.auth", true);
            Authenticator auth= new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    String username = TestProperty.getProperty("mail.smtp.user");
                    String password = TestProperty.getProperty("mail.smtp.pw");
                    return new PasswordAuthentication(username, password);
                }
            };
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getInstance(props, null);
        }

        session.setDebug(false);

        // create a message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));

        //set recipients
        msg.setRecipients(Message.RecipientType.TO, getAddress(to));

        //set cc
        msg.setRecipients(Message.RecipientType.CC, getAddress(cc));

        //set BCC
        msg.setRecipients(Message.RecipientType.BCC, getAddress(bcc));

        msg.setSubject(subject);
        msg.setSentDate(new Date());

        //set message content
        Multipart mainPart = new MimeMultipart();
        BodyPart body = new MimeBodyPart();
        if(text.contains("<body>")) {
            body.setContent(text, "text/html; charset=utf-8");
        } else {
            body.setText(text);
        }

        mainPart.addBodyPart(body);

        // Attach file with message
        if (attachments != null) {
            for (int i = 0; i < attachments.length; i++) {
                File file = new File(attachments[i]);
                if (file.exists()) {
                    MimeBodyPart attchmentPart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(attachments[i]);
                    attchmentPart.setDataHandler(new DataHandler(fds));
                    attchmentPart.setFileName(fds.getName());
                    mainPart.addBodyPart(attchmentPart);
                }
            }

        }

        msg.setContent(mainPart);

        return msg;

    }

    private static InternetAddress[] getAddress(String addrStr) throws AddressException {
        if(StringUtil.notEmpty(addrStr)) {
            String recipients[] = addrStr.split(";");

            InternetAddress[] address = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                address[i] = new InternetAddress(recipients[i]);
            }

            return address;
        } else {
            return new InternetAddress[0];
        }
    }

    /**
     * Connect to the email server with the default provider and all preset parameters in test.properties
     */
    public void connect(){
        connect(TestProperty.getProperty("mail.default.provider", "imap"));
    }

    /**
     * Connect to the email server with the given protocal with all preset parameters in test.properties
     * @param provider - can be either pop3 or imap
     */
    public void connect(String provider) {
        if(!provider.toLowerCase().matches("^pop3|imap$")) {
            throw new ItemNotFoundException("Provider "+provider+" is not supported, and should be either pop3 or imap.");
        }
        String host=TestProperty.getProperty("mail."+provider+".host");
        String username=TestProperty.getProperty("mail."+provider+".user");
        String password=TestProperty.getProperty("mail."+provider+".pw");
        int port=Integer.parseInt(TestProperty.getProperty("mail."+provider+".port","-1"));
        connect(host,port,username,password,provider);
    }

    /**
     * Connect to the email server with the default provider set in test.properties
     * If there is an existing connection, it will disconnect it firstly and then re-connect using the given parameters
     * @param host
     * @param username
     * @param password
     */
    public void connect(String host, String username, String password){
        provider=TestProperty.getProperty("mail.default.provider", "imap");
        int port=Integer.parseInt(TestProperty.getProperty("mail."+provider+".port","-1"));
        connect(host,port,username,password,provider);
    }

    /**
     * Connect to the email server with the given provider.
     * If there is an existing connection, it will disconnect it firstly and then re-connect using the given parameters
     * @param host
     * @param username
     * @param password
     * @param provider - can be either pop3 or imap
     */
    public void connect(String host, String username, String password, String provider){
        int port=Integer.parseInt(TestProperty.getProperty("mail."+provider+".port","-1"));
        connect(host,port,username,password,provider);
    }

    public void connect(String host, int port,String username, String password, String provider) {
        if(connected) {
            disconnect();
        }

        logger.info("Connecting email server "+host+" with privoide="+provider+" account="+username);

        this.provider=provider;
        if(!provider.toLowerCase().matches("^pop3|imap$")) {
            throw new ItemNotFoundException("Provider "+provider+" is not supported, and should be either pop3 or imap.");
        }

        try {
            Properties props = new Properties();

            session = Session.getDefaultInstance(props, null);
            store = session.getStore(provider);
            if(port>0) {
                store.connect(host, port,username, password);
            } else {
                store.connect(host, username, password);
            }
            connected=true;
            logger.info("Connected!");
        } catch (Exception e) {
            session=null;
            store=null;
            provider="";
            throw new RuntimeException("Failed to connect to "+host+" with "+username+" due to "+e.getMessage());
        }
    }

    /**
     * Disconnect from email server
     */
    public void disconnect(){
        if(connected) {
            try {
                store.close();
                logger.info("Disconnected from emails server.");
            } catch(Exception e) {
                logger.warn("Failed to disconnect from emails server due to "+e.getMessage());
            } finally {
                store=null;
                session=null;
                connected=false;
                provider="";
            }
        }
    }

    /**
     * Delete emails older than the given days in INBOX
     * @param days
     * @return - the number of emails deleted
     * @throws MessagingException
     */
    public int deleteEmails(int days) throws MessagingException {
        return deleteEmails("INBOX",days);
    }

    /**
     * Delete emails older than the given days in the given folder
     * @param folder
     * @param days
     * @return - the number of emails deleted
     * @throws MessagingException
     */
    public int deleteEmails(String folder,int days) throws MessagingException {
        long millseconds=days*24*3600*1000;
        return deleteEmails(folder,(long)millseconds);
    }

    /**
     * Delete emails older than the given millseconds in the given folder
     * @param mailFolder
     * @param millseconds
     * @return - the number of emails deleted
     * @throws MessagingException
     */
    public int deleteEmails(String mailFolder, long millseconds) throws MessagingException {
        Folder folder = null;

        if(provider.equalsIgnoreCase("pop3") && !mailFolder.equalsIgnoreCase("inbox")) {
            throw new ItemNotFoundException("Folder "+mailFolder+"is not accessable. POP3 can only access INBOX. Please use IMAP instead.");
        }

        try {

            folder=store.getFolder(mailFolder);

        } catch (MessagingException e) {
            folder=null;
        }

        if (folder == null) {
            logger.error("Folder "+mailFolder+" dosen't exist.");
            return 0;
        } else {
            return deleteEmails(folder,millseconds);
        }
    }

    private int deleteEmails(Folder folder,long millseconds) {
        try {
            folder.open(Folder.READ_WRITE);
            int count=0;
            Message[] messages=folder.getMessages();
            logger.info("Deleting all emails"+(millseconds>0?" older than "+millseconds+" millseconds.":"."));
            long now=Calendar.getInstance().getTimeInMillis();

            for(Message msg: messages) {
                boolean delete=true;
                if(millseconds>0 && now-msg.getSentDate().getTime()<millseconds) {
                    delete=false;
                }
                if(delete) {
                    logger.info("Deleted email with subject '"+msg.getSubject()+"'");
                    msg.setFlag(Flag.DELETED, true);
                    count++;
                }

            }
            logger.info("Deleting done! Total "+count+" emails got deleted.");

            folder.close(true);
            if(folder.isOpen()) {
                throw new RuntimeException("inbox is not closed");
            }

            return count;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Search and get email in the given mail box folder using subject pattern
     * @param mailFolder - the mail box folder to search the email
     * @param subjectPattern - can be RegularExpression, String or null. If it is String, the matching is case insensitive
     * @param delete - if true, the email will be deleted from mail box folder
     * @return - a Properties array contain all email match given search criteria which contain all information "from,date,subject,text,attach_count, attach_1, attach_2 ...."
     */
    public Properties[] searchEmail(String mailFolder,Object subjectPattern, boolean delete){
        return searchEmail(mailFolder,subjectPattern,null,null,null,0,null,0,delete);
    }

    /**
     * Search email in the given mail box folder using subject pattern. The search will keep going until either an email is found or it times out
     * @param mailFolder - the mail box folder to search the email
     * @param subjectPattern - can be RegularExpression, String or null. If it is String, the matching is case insensitive
     * @param timeout - seconds
     * @param delete - if true, the email will be deleted from mail box folder
     * @return - a Properties array contain all email match given search criteria which contain all information "from,date,subject,text,attach_count, attach_1, attach_2 ...."
     */
    public Properties[] searchEmail(String mailFolder,Object subjectPattern, int timeout, boolean delete){
        return searchEmail(mailFolder,subjectPattern,null,null,null,0,null,timeout,delete);
    }

    /**
     * Search email in the given mail box folder using subject pattern and save any attachment to the given attachment path
     * @param mailFolder - the mail box folder to search the email
     * @param subjectPattern - can be RegularExpression, String or null. If it is String, the matching is case insensitive
     * @param attachmentPath - attachment path, it will be ignored if it is null
     * @param delete - if true, the email will be deleted from mail box folder
     * @return - a Properties array contain all email match given search criteria which contain all information "from,date,subject,text,attach_count, attach_1, attach_2 ...."
     */
    public Properties[] searchEmail(String mailFolder,Object subjectPattern, String attachmentPath,int timeout,boolean delete){
        return searchEmail(mailFolder,subjectPattern,null,null,null,0,attachmentPath,timeout,delete);
    }

    public Properties[] searchEmail(String mailFolder,Object subjectPattern, String attachmentPath,String saveAsName,int timeout,boolean delete){
        return searchEmail(mailFolder,subjectPattern,null,null,null,0,attachmentPath,saveAsName,timeout,delete);
    }

    /**
     * Search email in the given mail box folder by email subject and send date.
     * @param mailFolder - the mail box folder to search the email
     * @param subjectPattern - can be RegularExpression object or String or null. If it is String, the matching is case insensitive
     * @param sendDate - the email should be send after sendDate and within the dateTreshold
     * @param dateTreshold - the number of minutes
     * @param timeout - keep searching email within the timeout periods
     * @param delete - if true, the email will be deleted from mail box folder
     * @return - a Properties array contain all email match given search criteria which contain all information "from,date,subject,text,attach_count, attach_1, attach_2 ...."
     */
    public Properties[] searchEmail(String mailFolder, Object subjectPattern, Date sendDate,int dateTreshold,int timeout, boolean delete){
        return searchEmail(mailFolder,subjectPattern,null,null,sendDate,dateTreshold,null,timeout,delete);
    }

    /**
     * Search email in the given mail box folder by email subject, from address and send date.
     * @param mailFolder - the mail box folder to search the email
     * @param subjectPattern - can be RegularExpression object or String or null. If it is String, the matching is case insensitive
     * @param fromPattern - can be RegularExpression object or String or null. If it is String, the matching is case insensitive
     * @param contentPattern - can be RegularExpression object or String or null. If it is String, the method will find an email whose content contains the contentPattern
     * @param sendDate - the email should be send after sendDate and within the dateTreshold
     * @param dateTreshold - the number of minutes
     * @param attachmentPath - the path to save the attachment files if there are any. if it is null, the attachment files will not be saved
     * @param delete - if true, the email will be deleted from mail box folder
     * @return - a Properties array contain all email match given search criteria which contain all information "from,date,subject,text,attach_count, attach_1, attach_2 ...."
     */
    public Properties[] searchEmail(String mailFolder,Object subjectPattern, Object fromPattern, Object contentPattern, Date sendDate, int dateTreshold, String attachmentPath, boolean delete){
        return searchEmail(mailFolder,subjectPattern,fromPattern,contentPattern,sendDate,0,attachmentPath,0,delete);
    }

    /**
     * Search email in the given mail folder by email subject, from address and send date. The search will keep going until either an email is found or it times out
     * @param mailFolder - the mail box folder to search the email
     * @param subjectPattern - can be RegularExpression object or String or null. If it is String, the matching is case insensitive
     * @param fromPattern - can be RegularExpression object or String or null. If it is String, the matching is case insensitive
     * @param contentPattern - can be RegularExpression object or String or null. If it is String, the method will find an email whose content contains the contentPattern
     * @param sendDate - the email should be send after sendDate and within the dateTreshold
     * @param dateTreshold - the number of minutes
     * @param attachmentPath - the path to save the attachment files if there are any. if it is null, the attachment files will not be saved
     * @param timeout - keep searching email within the timeout periods in seconds
     * @param delete - if true, the email will be deleted from mail box folder
     * @return - a Properties array contain all email match given search criteria which contain all information "from,date,subject,text,attach_count, attach_1, attach_2 ...."
     */
    public Properties[] searchEmail(String mailFolder,Object subjectPattern, Object fromPattern, Object contentPattern, Date sendDate, int dateTreshold, String attachmentPath, int timeout, boolean delete){
        return this.searchEmail(mailFolder, subjectPattern, fromPattern, contentPattern, sendDate, dateTreshold, attachmentPath, null, timeout, delete);
    }

    public Properties[] searchEmail(String mailFolder,Object subjectPattern, Object fromPattern, Object contentPattern, Date sendDate, int dateTreshold, String attachmentPath,String saveAsName, int timeout, boolean delete){
        return searchEmail(mailFolder, subjectPattern, fromPattern, contentPattern, sendDate, dateTreshold, attachmentPath, saveAsName, timeout, delete,false);
    }

    /**
     * This method use java mail native search term to filter email, the performance is better,currently used in report email retrieve
     * @return
     */
    public Properties[] searchEmail(String mailFolder,String subjectPattern, String fromPattern, String contentPattern, Date sendDate,int dateTreshold, String attachmentPath,String saveAsName, int timeout, boolean delete){
        return searchEmail(mailFolder, subjectPattern, fromPattern, contentPattern, sendDate,dateTreshold, attachmentPath, saveAsName, timeout, delete,false);
    }

    /**
     * Use subject as the unique search criteria to search email via wrap java native search term
     * @return
     */
    public Properties[] searchEmail(String mailFolder,String subjectPattern,String attachmentPath,String saveAsName, int timeout, boolean delete){
        return searchEmail(mailFolder, subjectPattern, StringUtil.EMPTY, StringUtil.EMPTY, null,0, attachmentPath, saveAsName, timeout, delete);
    }

    protected Properties[] searchEmail(String mailFolder,Object subjectPattern, Object fromPattern, Object contentPattern, Date sendDate, int dateTreshold, String attachmentPath,String saveAsName, int timeout, boolean delete,boolean useLocalSearchTerm){
        SearchTerm subject=(subjectPattern==null||StringUtil.isEmpty(subjectPattern.toString()))?null:useLocalSearchTerm?new SubjectTerm(subjectPattern):new javax.mail.search.SubjectTerm(subjectPattern.toString());
        SearchTerm from=(fromPattern==null||StringUtil.isEmpty(fromPattern.toString()))?null:useLocalSearchTerm?new FromAddressTerm(fromPattern):new javax.mail.search.FromStringTerm(fromPattern.toString());
        SearchTerm date=sendDate==null?null:useLocalSearchTerm?new SendDateTerm(sendDate,dateTreshold):new javax.mail.search.SentDateTerm(ComparisonTerm.GT,sendDate);//ComparisonTerm.GT means date greater than send date
        SearchTerm content=(contentPattern==null||StringUtil.isEmpty(contentPattern.toString()))?null:useLocalSearchTerm?new TextContentTerm(contentPattern):new javax.mail.search.BodyTerm(contentPattern.toString());
        SearchTerm searchCriteria=getSearchCriteria(subject,from,content,date);

        return searchEmail(mailFolder, searchCriteria, attachmentPath, saveAsName, timeout, delete);
    }

    protected Properties[] searchEmail(String mailFolder,SearchTerm searchCriteria, String attachmentPath,String saveAsName, int timeout, boolean delete){
        if(!connected) {
            throw new RuntimeException("Email server is not connected");
        }

        if(provider.equalsIgnoreCase("pop3") && !mailFolder.equalsIgnoreCase("inbox")) {
            throw new ItemNotFoundException("Folder "+mailFolder+"is not accessable. POP3 can only access INBOX. Please use IMAP instead.");
        }

        try {
            Folder inbox = store.getFolder(mailFolder);

            if (inbox == null) {
                logger.error("Folder "+mailFolder+" doesn't exists.");
                return null;
            } else {
                Timer timer=new Timer();
                Properties[] mails = null;
                boolean done=false;
                while((mails==null||mails.length<1) && !done) {
                    mails=searchEmail(inbox,searchCriteria,attachmentPath,saveAsName,delete);
                    done=timer.diff()>timeout;
                    Timer.sleep(1000);
                }

                if(mails==null||mails.length<1) {
                    logger.info("No emails found matching the search criteria"+(timeout<=0?"":" within "+timeout+" seconds"));
                }
                return mails;
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * List all email's in INBOX
     * @param mailFolder
     * @return
     */
    public String[] list(String mailFolder) {
        return list(mailFolder,-1);
    }

    /**
     * List the last <size> email's in the given mail box folder. If size<=0, list all email's
     * @param mailFolder
     * @param size
     * @return
     */
    public String[] list(String mailFolder,int size) {
        if(!connected) {
            throw new RuntimeException("Email server is not connected");
        }
        if (StringUtil.isEmpty(mailFolder)) {
            mailFolder="INBOX";
        }

        if(provider.equalsIgnoreCase("pop3") && mailFolder!=null && !mailFolder.equalsIgnoreCase("inbox")) {
            throw new ItemNotFoundException("Folder "+mailFolder+"is not accessable. POP3 can only access INBOX. Please use IMAP instead.");
        }

        Folder inbox=null;
        try {
            inbox = store.getFolder(mailFolder);
            if (inbox == null) {
                logger.error("No INBOX");
                return null;
            } else {
                inbox.open(Folder.READ_ONLY);
                Message[] msgs=inbox.getMessages();
                logger.info("Total "+msgs.length+" emails.");
                if(size<=0) {
                    size=msgs.length;
                } else {
                    size=Math.min(size, msgs.length);
                }
                String[] list=new String[size];
                for(int i=0;i<size;i++) {
                    int msgIndex=msgs.length-size+i;
                    list[i]=msgs[msgIndex].getSubject();
                }
                inbox.close(false);
                return list;
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Message[] searchEmailMessages(Folder folder, SearchTerm criteria) throws IOException, MessagingException {
        Message[] messages = null;

        int totalMessages=folder.getMessageCount();

        if(totalMessages==0) {
            logger.debug("no emails");
            return null;
        } else {
            logger.debug("Total "+totalMessages+" emails");
        }

        if(criteria==null) {
            messages=folder.getMessages();
        } else {
            messages=folder.search(criteria);
        }

        return messages;
    }

    private Properties[] searchEmail(Folder folder, SearchTerm criteria, String attachmentPath,String saveAsName, boolean delete) throws IOException, MessagingException {
        Properties mail = null;
        Properties[] pros = null;

        Message[] messages= null;

        if(delete) {
            folder.open(Folder.READ_WRITE);
        } else {
            folder.open(Folder.READ_ONLY);
        }
        try{
            messages=searchEmailMessages(folder,criteria);
        }catch (ArrayIndexOutOfBoundsException e){
            folder.close(delete);
            folder.open(delete ? Folder.READ_WRITE:Folder.READ_ONLY);
            logger.warn("Try one more time to walk arount a JavaMail bug");
            messages=searchEmailMessages(folder,criteria);
        }

        if(messages.length!=0) {
            pros = new Properties[messages.length];
            for(int j=0;j<messages.length;j++){
                mail=new Properties();
                Message msg=messages[messages.length-1];
                mail.put("from", StringUtil.arrayToString(msg.getFrom()));
                mail.put("date", msg.getSentDate().toString());
                mail.put("subject", msg.getSubject());

                Object content=msg.getContent();
                if(content instanceof MimeMultipart) {
                    MimeMultipart part=(MimeMultipart)msg.getContent();
                    int count=part.getCount();

                    mail.put("attach_count", Integer.toString(count-1));
                    int attachCount=1;

                    for(int i=0;i<count;i++) {
                        MimeBodyPart bp=(MimeBodyPart)part.getBodyPart(i);
                        String type=bp.getContentType();
                        if(!type.contains("name=")) { //processing the email content
                            if(type.toUpperCase().startsWith("TEXT/PLAIN")) {
                                mail.put("text", bp.getContent().toString());
                                mail.put("type","TEXT/PLAIN");
                            } else if(type.toLowerCase().startsWith("text/html")) {
                                mail.put("text", bp.getContent().toString());
                                mail.put("type","text/html");
                            } else if(type.equalsIgnoreCase("multipart/ALTERNATIVE")) {
                                MimeMultipart mp=(MimeMultipart)bp.getContent();
                                MimeBodyPart subbp=(MimeBodyPart)mp.getBodyPart(0);//text/plain part

                                mail.put("text", subbp.getContent());
                                mail.put("type","multipart/ALTERNATIVE-text/plain");
                            }else {
                                logger.warn("Skipped unknown type: "+type);
                            }
                        } else { //processing the attachments
                            String fileName=getAttachFileName(type);
                            if(attachmentPath!=null && attachmentPath.length()>0) {
                                File file = new File(attachmentPath);
                                if(!file.exists()){
                                    file.mkdirs();
                                }
                                if(StringUtil.isEmpty(saveAsName)){
                                    fileName = fileName.replaceAll("\\d{2,}", "");//used to remove the dynamic date string
                                }else{
                                    fileName = saveAsName+fileName.substring(fileName.indexOf("."));
                                }


                                if(!attachmentPath.endsWith("\\") && !attachmentPath.endsWith("/")) {
                                    attachmentPath+="/";
                                }
                                fileName =attachmentPath+fileName;
                                InputStream in = null;
                                if(type.startsWith("TEXT")){
                                    in = new ByteArrayInputStream(bp.getContent().toString().getBytes());
                                }else{
                                    in=(InputStream) bp.getContent();
                                }

                                FileUtil.write(in, fileName);
                            }
                            mail.put("attach_"+attachCount,fileName);
                            attachCount++;
                        }
                    } //end for
                }else {
                    mail.put("text", content.toString());
                    mail.put("attach_count", "0");
                }
                pros[j] = mail;

                if(delete) {
                    msg.setFlag(Flag.DELETED, true);
                }
            }

        }
        folder.close(delete);
        if(folder.isOpen()) {
            throw new RuntimeException("folder is not closed");
        }

        return pros;
    }

    private String getAttachFileName(String type) {
        int index=type.indexOf("name");
        String text=type.substring(index+5);
        text=text.replaceAll("\"", "").trim();
        return text;

    }

    private SearchTerm getSearchCriteria(SearchTerm...term) {
        List<SearchTerm> t=new ArrayList<SearchTerm>();
        for(int i=0;i<term.length;i++) {
            if(term[i]!=null) {
                t.add(term[i]);
            }
        }
        if(t.size()==0) {
            return null;
        } else if(t.size()==1) {
            return t.get(0);
        } else {
            return new AndTerm(t.toArray(new SearchTerm[0]));
        }
    }

    class SubjectTerm extends SearchTerm  {
        private static final long serialVersionUID = 1L;
        Object pattern;
        boolean ignorCase;

        SubjectTerm(String pattern, boolean ignorCase) {
            this.pattern=pattern;
            this.ignorCase=ignorCase;
        }

        SubjectTerm(Object pattern) {
            this.pattern=pattern;
            this.ignorCase=false;
        }

        @Override
        public boolean match(Message msg) {
            try {
                String subject=msg.getSubject();
                if(pattern instanceof RegularExpression ) {
                    return ((RegularExpression)pattern).match(subject);
                } else {
                    if(ignorCase) {
                        return ((String) pattern).equalsIgnoreCase(subject);
                    } else {
                        return ((String) pattern).equals(subject);
                    }
                }
            } catch (MessagingException e) {
                return false;
            }
        }
    }

    class FromAddressTerm extends SearchTerm  {
        private static final long serialVersionUID = 1L;
        Object pattern;
        boolean ignorCase;

        FromAddressTerm(String pattern, boolean ignorCase) {
            this.pattern=pattern;
            this.ignorCase=ignorCase;
        }

        FromAddressTerm(Object pattern) {
            this.pattern=pattern;
            this.ignorCase=false;
        }

        @Override
        public boolean match(Message msg) {
            try {
                boolean match=false;
                Address[] addresses=msg.getFrom();
                for(int i=0;i<addresses.length && !match;i++) {
                    String address=addresses[i].toString();
                    if(pattern instanceof RegularExpression ) {
                        match= ((RegularExpression)pattern).match(address);
                    } else {
                        if(ignorCase) {
                            match= ((String) pattern).equalsIgnoreCase(address);
                        } else {
                            match= ((String) pattern).equals(address);
                        }
                    }
                }
                return match;
            } catch (MessagingException e) {
                return false;
            }
        }
    }

    class SendDateTerm extends SearchTerm  {
        private static final long serialVersionUID = 1L;
        long time;
        int treshold;
        boolean within;

        SendDateTerm(int treshold) {
            this(treshold,true);
        }

        SendDateTerm(int treshold,boolean within) {
            this(Calendar.getInstance().getTimeInMillis(),treshold,within);
        }

        SendDateTerm(String date, int treshold) {
            this(DateFunctions.parseDateString(date),treshold);
        }

        SendDateTerm(Date date, int treshold) {
            this(date,treshold,true);
        }

        SendDateTerm(Date date, int treshold, boolean within) {
            this(date.getTime(),treshold,within);
        }

        SendDateTerm(long time, int treshold, boolean within) {
            this.time=time;
            this.treshold=treshold;
            this.within=within;
        }

        @Override
        public boolean match(Message msg) {
            try {
                Date sendDate=msg.getSentDate();
                long diff=Math.abs(sendDate.getTime()-time);
                if(within)
                    return diff<treshold*60*1000;
                else
                    return diff>=treshold*60*1000;
            } catch (MessagingException e) {
                return false;
            }
        }
    }

    class TextContentTerm extends SearchTerm  {
        private static final long serialVersionUID = 1L;
        Object text;

        TextContentTerm(Object text) {
            this.text=text;
        }

        @Override
        public boolean match(Message msg) {
            try {
                String contentText="";
                Object content=msg.getContent();
                if(content instanceof MimeMultipart) {
                    MimeMultipart part=(MimeMultipart)msg.getContent();
                    int count=part.getCount();

                    for(int i=0;i<count;i++) {
                        MimeBodyPart bp=(MimeBodyPart)part.getBodyPart(i);
                        String type=bp.getContentType();
                        if(type.startsWith("text")) {
                            contentText=bp.getContent().toString();
                            break;
                        }
                    }
                }else {
                    contentText=content.toString();
                }

                if(text instanceof RegularExpression) {
                    return ((RegularExpression) text).match(contentText);
                } else {
                    return contentText.contains((String) text);
                }
            } catch (Exception e) {
                return false;
            }
        }
    }
}

