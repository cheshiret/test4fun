package com.active.qa.automation.web.testapi.util;

import javax.swing.ProgressMonitor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 * Wrap the functions for access FTP server.
 * Created by tchen on 1/11/2016.
 */
public class Ftp {
    String user;

    String host;

    String pwd;

    int port;

    static ChannelSftp c = null;

    static Session session = null;

    /**
     * Construct method used to initialize with given parameters
     * @param user
     * @param host
     * @param pwd
     * @param port
     */
    public Ftp(String user, String host, String pwd, int port) {
        this.user = user;
        this.host = host;
        this.pwd = pwd;
        this.port = port;

    }

    public Ftp() {
        this.user = "jboss2";
        this.host = "10.10.80.57";
        this.pwd = "fry";
        this.port = 22;

    }

    public static void main(String[] arg) {

        Ftp.connect("jboss2", "10.10.80.57", "fry", 22);

        Ftp.executeFTP("ls");

        System.err.println(" ------");

        Ftp.executeFTP("cd /tmp");

        Ftp.executeFTP("rm *.XML");

        Ftp.executeFTP("get *.XML C:/0_0/parkSeason.xml");

        Ftp.disconnect();

    }

    /**
     * This method is used to connect FTP server with given parameters
     * @param host
     * @param user
     * @param pwd
     * @param port
     */
    public static void connect(String host, String user, String pwd, int port) {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(pwd);

            java.util.Properties config1 = new java.util.Properties();
            config1.put("StrictHostKeyChecking", "no");
            session.setConfig(config1);

            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * This method is used to disconnect Session
     *
     */
    public static void disconnect() {
        if (session!=null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * This method is used to execute FTP Command
     * @param command
     * @return
     */
    public static int executeFTP(String command) {

        try {

            //	java.io.InputStream in=System.in;
            java.io.PrintStream out = System.out;

            //		byte[] buf=new byte[1024];
            //		int i;
            String str;
            int level = 0;

            out.print("sftp> ");
            String[] cmds = null;

            if (command != null && command != "") {
                cmds = command.split(" ");
                command = "";
            }

            String cmd = cmds[0];

            if (cmd.equals("quit")) {
                c.quit();
            }
            if (cmd.equals("exit")) {
                c.exit();
            }
            if (cmd.equals("rekey")) {
                session.rekey();
            }
            if (cmd.equals("compression")) {
                if (cmds.length < 2) {
                    out.println("compression level: " + level);
                }
                try {
                    level = Integer.parseInt(cmds[1]);
                    java.util.Hashtable<String, String> config = new java.util.Hashtable<String, String>();
                    if (level == 0) {
                        config.put("compression.s2c", "none");
                        config.put("compression.c2s", "none");
                    } else {
                        config.put("compression.s2c", "zlib,none");
                        config.put("compression.c2s", "zlib,none");
                    }
                    session.setConfig(config);
                } catch (Exception e) {
                }
            }
            if (cmd.equals("cd") || cmd.equals("lcd")) {
                if (cmds.length < 2) {
                    return -1;
                }

                String path = cmds[1];
                try {
                    if (cmd.equals("cd"))
                        c.cd(path);
                    else
                        c.lcd(path);
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (cmd.equals("rm") || cmd.equals("rmdir") || cmd.equals("mkdir")) {
                if (cmds.length < 2) {
                    return -1;
                }
                String path = cmds[1];
                try {
                    if (cmd.equals("rm"))
                        c.rm(path);
                    else if (cmd.equals("rmdir"))
                        c.rmdir(path);
                    else
                        c.mkdir(path);
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }

            }
            if (cmd.equals("chgrp") || cmd.equals("chown")
                    || cmd.equals("chmod")) {
                if (cmds.length != 3) {
                    return -1;
                }
                String path = cmds[2];
                int foo = 0;
                if (cmd.equals("chmod")) {
                    byte[] bar = cmds[1].getBytes();
                    int k;
                    for (int j = 0; j < bar.length; j++) {
                        k = bar[j];
                        if (k < '0' || k > '7') {
                            foo = -1;
                            return -1;
                        }
                        foo <<= 3;
                        foo |= (k - '0');
                    }
                    if (foo == -1) {
                        return -1;
                    }
                } else {
                    try {
                        foo = Integer.parseInt(cmds[1]);
                    } catch (Exception e) {
                        return -1;
                    }
                }
                try {
                    if (cmd.equals("chgrp")) {
                        c.chgrp(foo, path);
                    } else if (cmd.equals("chown")) {
                        c.chown(foo, path);
                    } else if (cmd.equals("chmod")) {
                        c.chmod(foo, path);
                    }
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (cmd.equals("pwd") || cmd.equals("lpwd")) {
                str = (cmd.equals("pwd") ? "Remote" : "Local");
                str += " working directory: ";
                if (cmd.equals("pwd"))
                    str += c.pwd();
                else
                    str += c.lpwd();
                out.println(str);
            }
            if (cmd.equals("ls") || cmd.equals("dir")) {
                String path = ".";
                if (cmds.length == 2)
                    path = cmds[1];
                try {
                    java.util.Vector<?> vv = c.ls(path);
                    if (vv != null) {
                        for (int ii = 0; ii < vv.size(); ii++) {
                            //		out.println(vv.elementAt(ii).toString());

                            Object obj = vv.elementAt(ii);
                            if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                                out
                                        .println(((com.jcraft.jsch.ChannelSftp.LsEntry) obj)
                                                .getLongname());
                            }

                        }
                    }
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }

            }
            if (cmd.equals("lls") || cmd.equals("ldir")) {
                String path = ".";
                if (cmds.length == 2)
                    path = cmds[1];
                try {
                    java.io.File file = new java.io.File(path);
                    if (!file.exists()) {
                        out.println(path + ": No such file or directory");
                        return -1;
                    }
                    if (file.isDirectory()) {
                        String[] list = file.list();
                        for (int ii = 0; ii < list.length; ii++) {
                            out.println(list[ii]);
                        }
                    }
                    out.println(path);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            if (cmd.equals("get") || cmd.equals("get-resume")
                    || cmd.equals("get-append") || cmd.equals("put")
                    || cmd.equals("put-resume") || cmd.equals("put-append")) {
                if (cmds.length != 2 && cmds.length != 3) {
                    return -1;
                }
                String p1 = cmds[1];
                //	  String p2=p1;
                String p2 = ".";
                if (cmds.length == 3)
                    p2 = cmds[2];
                try {
                    SftpProgressMonitor monitor = new MyProgressMonitor();
                    if (cmd.startsWith("get")) {
                        int mode = ChannelSftp.OVERWRITE;
                        if (cmd.equals("get-resume")) {
                            mode = ChannelSftp.RESUME;
                        } else if (cmd.equals("get-append")) {
                            mode = ChannelSftp.APPEND;
                        }
                        c.get(p1, p2, monitor, mode);
                    } else {
                        int mode = ChannelSftp.OVERWRITE;
                        if (cmd.equals("put-resume")) {
                            mode = ChannelSftp.RESUME;
                        } else if (cmd.equals("put-append")) {
                            mode = ChannelSftp.APPEND;
                        }
                        c.put(p1, p2, monitor, mode);
                    }
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (cmd.equals("ln") || cmd.equals("symlink")
                    || cmd.equals("rename")) {
                if (cmds.length != 3) {
                    return -1;
                }
                String p1 = cmds[1];
                String p2 = cmds[2];
                try {
                    if (cmd.equals("rename"))
                        c.rename(p1, p2);
                    else
                        c.symlink(p1, p2);
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (cmd.equals("stat") || cmd.equals("lstat")) {
                if (cmds.length != 2) {
                    return -1;
                }
                String p1 = cmds[1];
                SftpATTRS attrs = null;
                try {
                    if (cmd.equals("stat"))
                        attrs = c.stat(p1);
                    else
                        attrs = c.lstat(p1);
                } catch (SftpException e) {
                    System.out.println(e.getMessage());
                }
                if (attrs != null) {
                    out.println(attrs);
                } else {
                }
            }
            if (cmd.equals("version")) {
                out.println("SFTP protocol version " + c.version());
            }
            if (cmd.equals("help") || cmd.equals("?")) {
                out.println(help);
            }

            //			session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    /**
     * Static Inner class used to scrutiny progress
     * @author QA
     */
    public static class MyProgressMonitor implements SftpProgressMonitor {
        ProgressMonitor monitor;

        long count = 0;

        long max = 0;

        public void init(int op, String src, String dest, long max) {
            this.max = max;
            monitor = new ProgressMonitor(null,
                    ((op == SftpProgressMonitor.PUT) ? "put" : "get") + ": "
                            + src, "", 0, (int) max);
            count = 0;
            percent = -1;
            monitor.setProgress((int) this.count);
            monitor.setMillisToDecideToPopup(1000);
        }

        private long percent = -1;

        public boolean count(long count) {
            this.count += count;

            if (percent >= this.count * 100 / max) {
                return true;
            }
            percent = this.count * 100 / max;

            monitor.setNote("Completed " + this.count + "(" + percent
                    + "%) out of " + max + ".");
            monitor.setProgress((int) this.count);

            return !(monitor.isCanceled());
        }

        public void end() {
            monitor.close();
        }
    }

    private static String help = "      Available commands:\n"
            + "      * means unimplemented command.\n"
            + "cd path                       Change remote directory to 'path'\n"
            + "lcd path                      Change local directory to 'path'\n"
            + "chgrp grp path                Change group of file 'path' to 'grp'\n"
            + "chmod mode path               Change permissions of file 'path' to 'mode'\n"
            + "chown own path                Change owner of file 'path' to 'own'\n"
            + "help                          Display this help text\n"
            + "get remote-path [local-path]  Download file\n"
            + "get-resume remote-path [local-path]  Resume to download file.\n"
            + "get-append remote-path [local-path]  Append remote file to local file\n"
            + "*lls [ls-options [path]]      Display local directory listing\n"
            + "ln oldpath newpath            Symlink remote file\n"
            + "*lmkdir path                  Create local directory\n"
            + "lpwd                          Print local working directory\n"
            + "ls [path]                     Display remote directory listing\n"
            + "*lumask umask                 Set local umask to 'umask'\n"
            + "mkdir path                    Create remote directory\n"
            + "put local-path [remote-path]  Upload file\n"
            + "put-resume local-path [remote-path]  Resume to upload file\n"
            + "put-append local-path [remote-path]  Append local file to remote file.\n"
            + "pwd                           Display remote working directory\n"
            + "stat path                     Display info about path\n"
            + "exit                          Quit sftp\n"
            + "quit                          Quit sftp\n"
            + "rename oldpath newpath        Rename remote file\n"
            + "rmdir path                    Remove remote directory\n"
            + "rm path                       Delete remote file\n"
            + "symlink oldpath newpath       Symlink remote file\n"
            + "rekey                         Key re-exchanging\n"
            + "compression level             Packet compression will be enabled\n"
            + "version                       Show SFTP version\n"
            + "?                             Synonym for help";
}

