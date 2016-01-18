package com.active.qa.automation.web.testapi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *  * Script Name   : SSH2
 * Generated     : Feb 11, 2005 9:47:51 AM
 * Original Host : WinNT Version 5.1  Build 2600 (Service Pack 2)
 *
 * Provide functions to login to any server with SSH
 * Created by tchen on 1/11/2016.
 */
public class SSH2 {
    /**
     * Script Name   : SSH2
     * Generated     : Feb 11, 2005 9:47:51 AM
     * Original Host : WinNT Version 5.1  Build 2600 (Service Pack 2)
     *
     * @since  2005/02/11
     */

    private static SSH2 _instance = null;

    private JSch jsch;

    private Session session;

    private Channel channel;

    private boolean connected;    //mark is connected

    //Cache user,password and host in the last connection
    private String user;

    private String password;

    private String host;

    private Auth auth;

    //	private static RALogger logger=RALogger.getInstance();

    /**
     * Single pattern to get a unique instance of this class
     */
    public static SSH2 getInstance() {
        if (null == _instance)
            _instance = new SSH2();

        return _instance;
    }

    protected SSH2() {
        jsch = new JSch();
        session = null;
        channel = null;
        connected = false;
        auth=null;
    }

    /**
     * Use the given user,pwd and host to connect server
     * @param user
     * @param password
     * @param host
     * @throws JSchException
     */
    public void connect(String user, String password, String host)	{
        disconnect();
        this.user=user;
        this.password=password;
        this.host=host;
        this.auth=null;
        AutomationLogger.getInstance().info("SSH to "+host);
        connect();

    }

    public void connect(Auth auth,String host)	{
        disconnect();
        this.user=null;
        this.password=null;
        this.host=host;
        this.auth=auth;
        AutomationLogger.getInstance().info("SSH to "+host);
        connect();

    }

    protected void connect() {
        connected=false;
        int count=0;
        while(!connected) { //while try 5 times
            if(count!=0) {
                AutomationLogger.getInstance().info("Try to re-connect, time#"+count);
            }
            try {
                connectSession();
                connected=true;
            } catch(ItemNotFoundException infe) {
                throw infe;
            } catch(Exception e) {
                if(count<5) {
                    AutomationLogger.getInstance().warn("Failed to connect due to "+e);
                } else {
                    throw new ActionFailedException(e);
                }
                count++;
                Timer.sleep(2000); //sleep 2 seconds before re-connect
            }
        }
    }

    protected void connectSession() throws JSchException {
        if(session !=null ) {
            if(session.isConnected()) {
                session.disconnect();
            }
            session=null;
        }

        AutomationLogger.getInstance().info("Connectting...");

        if(StringUtil.notEmpty(user)) {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
        } else if(auth!=null) {
            session = auth.getSession(host);
        } else {
            throw new ItemNotFoundException("Authentication is missing!");
        }

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        AutomationLogger.getInstance().info("Connected");
    }

    public void disconnect() {
        if(connected) {
            if(channel !=null) {
                channel.disconnect();
                channel=null;
            }

            if(session !=null) {
                session.disconnect();
                session=null;
            }

            connected = false;
        }
    }

    /**
     * Copy a file to specific destination with WinSCP command
     * @param lfile file you want to transfer
     * @param rfile destination file
     */
    public synchronized void scpTo(String lfile,String rfile) {
        if(!connected) {
            throw new ActionFailedException("There is no session!");
        }
        try {
            // exec 'scp -t rfile' remotely
            String command = "scp -p -t " + rfile;

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            //byte[] tmp = new byte[1];
            checkAck(in);

            // send "C0644 filesize filename", where filename should not include '/'
            int filesize = (int) (new File(lfile)).length();
            command = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                command += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                command += lfile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            checkAck(in);

            // send a content of lfile
            FileInputStream fis = new FileInputStream(lfile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0)
                    break;
                out.write(buf, 0, len);
                out.flush();
            }
            fis.close();

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            checkAck(in);
        } catch (Exception e) {
            throw new ItemNotFoundException("Failed to copy file: "
                    + e.getMessage());
        } finally {
            if(channel!=null) {
                channel.disconnect();
            }
        }
    }

    /**
     * copy a file from remote host to local
     * @param rfile
     * @param lfile
     */
    public String scpFrom(String rfile, String lfile) {
        if(!connected) {
            throw new ActionFailedException("There is no session!");
        }
        FileOutputStream fos=null;
        //When get a rfile which with regular expression, to save the file with the same name as it get from the remote server[add by phoebe]
        String completeLfile = lfile;

        try {
            // exec 'scp -f rfile' remotely
            String command="scp -f "+rfile;
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out=channel.getOutputStream();
            InputStream in=channel.getInputStream();

            channel.connect();

            byte[] buf=new byte[1024];

            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();

            while(true){
                int c=checkAck(in);
                if(c!='C'){
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize=0L;
                while(true){
                    if(in.read(buf, 0, 1)<0){
                        // error
                        break;
                    }
                    if(buf[0]==' ')break;
                    filesize=filesize*10L+(long)(buf[0]-'0');
                }

                String file=null;
                for(int i=0;;i++){
                    in.read(buf, i, 1);
                    if(buf[i]==(byte)0x0a){
                        file=new String(buf, 0, i);
                        break;
                    }
                }

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();

                //When get a rfile which with regular expression, to save the file with the same name as it get from the remote server[add by phoebe]
                if(completeLfile.contains("*")){
                    completeLfile = completeLfile.substring(0, completeLfile.lastIndexOf("/")) + "/" + file;
                }

                // read a content of lfile
                fos=new FileOutputStream(completeLfile);
                int foo;
                while(true){
                    if(buf.length<filesize) foo=buf.length;
                    else foo=(int)filesize;
                    foo=in.read(buf, 0, foo);
                    if(foo<0){
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize-=foo;
                    if(filesize==0L) break;
                }
                fos.close();
                fos=null;

                if(checkAck(in)!=0){
                    throw new ActionFailedException("Failed to get Ack, Copy may fail!");
                }

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if(channel!=null) {channel.disconnect();}
        }
        return completeLfile;
    }

    /**
     * read a remote text file into String
     * @param rfile
     * @return
     */
    public String readFile(String rfile) {
        String command="cat "+rfile;
        return exec(command);

    }

    public String removeFile(String rfile){
        String command = "rm "+rfile;
        return exec(command);
    }

    /**
     * execute the given command in the remote host
     * @param command
     * @return - command output in the remote host
     */
    public String exec(String command) {
        if(!connected) {
            throw new ActionFailedException("There is no session!");
        }
        StringBuffer data=new StringBuffer();
        OutputStream out=null;
        InputStream in=null;
        try {
            Channel channel;

            boolean channel_connected=false;
            int count=0;
            while(!channel_connected) {

                try {
                    channel=session.openChannel("exec");
                    ((ChannelExec)channel).setCommand(command);

                    out=channel.getOutputStream();
                    in=channel.getInputStream();
                    channel.connect();
                    channel_connected=true;
                } catch(Exception e) {
                    count++;
                    String msg=e.getMessage();
                    if(count<5) {
                        AutomationLogger.getInstance().warn("Failed to connect to SSH server due to "+msg+". Will try again in 1 second");
                        if(msg.startsWith("session is down")) {
                            AutomationLogger.getInstance().info("Try to re-connect session");
                            connect();
                        }
                        Timer.sleep(1000);
                    } else {
                        throw new ActionFailedException("Failed to connect to SSH server due to "+e);
                    }


                }
            }


            byte[] buf=new byte[1024];

            //read
            count=0;
            while((count=in.read(buf))>0) {
                data.append(new String(buf,0,count));
            }


        } catch(Exception e) {
            AutomationLogger.getInstance().warn(e);
        } finally {
            try {in.close();} catch (Exception e) {}
            try {out.close();} catch (Exception e) {}

            if(channel!=null) {channel.disconnect();}
        }
        return data.toString();
    }

    /**
     * Check the command execute status
     * @param in
     * @return
     * @throws IOException
     */
    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0)
            return b;
        if (b == -1)
            return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                throw new RuntimeException("InputStream error: "
                        + sb.toString());
            }
            if (b == 2) { // fatal error
                throw new RuntimeException("InputStream fatal error: "
                        + sb.toString());
            }
        }
        return b;
    }

    public interface Auth {
        Session getSession(String host);
    }

}

