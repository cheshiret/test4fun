package com.active.qa.automation.web.testrunner.util;

import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

/**
 * Created by tchen on 1/18/2016.
 */
public class RPCClient {
    public static Logger logger = Logger.getLogger(RPCClient.class);
    public static Socket socket=null;

    /**
     * @param args
     */
    public static void rpc(int port, String cmd, List<String> ips) {
        if(!cmd.matches("startrunner|stoprunner|reboot")) {
            logger.warn("Command '"+cmd+"' is invalid.");
            return;
        }

        for(String ip:ips) {
            try {
                socket=new Socket();
                InetAddress addr=InetAddress.getByName(ip);
                SocketAddress socketAddr=new InetSocketAddress(addr,port);
                socket.connect(socketAddr);
                OutputStream outs=socket.getOutputStream();

                PrintWriter out =new PrintWriter(outs,true);
                out.println(cmd);
                out.close();

            } catch (Exception e) {
                logger.warn(e);
            }
            try {
                socket.close();
                socket=null;
            } catch(Exception e) {}
        }
    }

}

