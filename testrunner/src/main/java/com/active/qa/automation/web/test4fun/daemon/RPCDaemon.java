package com.active.qa.automation.web.test4fun.daemon;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


/**
 * Created by tchen on 1/15/2016.
 */
public class RPCDaemon {
    public static void main(String[] args) throws IOException {
        ServerSocket socket=null;
        Socket incoming=null;
        Runtime rt=Runtime.getRuntime();
        //Change the port # if needed
        int port=4445;
        if(args.length>0 && args[0].matches("\\d+")) {
            port=Integer.parseInt(args[0]);
        }

        try {
            //replace the ip from test properties file
            rt.exec("net use x: \\\\8.8.8.8\\TestFarm_Shared");
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (true) {
            try {
                socket = new ServerSocket(port);
                incoming=socket.accept();

                InputStream inps=incoming.getInputStream();
                OutputStream outs=incoming.getOutputStream();

                Scanner in = new Scanner(inps);
                PrintWriter out =new PrintWriter(outs,true);
                out.println("Welcome to RPC Daemon! Available commands: startrunner, stoprunner, reboot");

                if(in.hasNextLine()) { //only accept one command for one connection
                    String line=in.nextLine().trim();
                    if(line.endsWith("startrunner")) {
                        rt.exec("TASKKILL /F /IM java.exe /FI \"MEMUSAGE gt 20000\" /FI \"SESSION eq 1\"");
                        rt.exec("cmd /k start java -jar x:/TestDriver/testdriver.jar com.active.qa.testdriver.TestAgent");
                        out.println("done.");
                    } else if(line.endsWith("stoprunner")) {
                        rt.exec("TASKKILL /F /IM java.exe /FI \"MEMUSAGE gt 20000\" /FI \"SESSION eq 1\"");
                        out.println("done.");
                    } else if(line.endsWith("reboot")) {
                        rt.exec("SHUTDOWN -r -t 05");
                        out.println("done.");
                    } else {
                        out.println("Command '"+line+"' is undefined");
                        Thread.sleep(1000);
                    }

                }

            } catch(java.net.BindException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
                if (incoming != null) {
                    incoming.close();
                }
                incoming=null;
 //           }catch(Exception e) {}
 //           try {
                if (socket != null) {
                    socket.close();
                }
                socket=null;
 //           } catch(Exception e) {}
        }
    }

}

