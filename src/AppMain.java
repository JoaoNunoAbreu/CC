package com.company;


public class AppMain {

    public static void main(String args[]) {
        if(System.getProperty("remoteHost") == null) System.out.println("remoteHost é nulo");
        else if(System.getProperty("remotePort") == null)  System.out.println("remotePort é nulo");
        else if(System.getProperty("port") == null)  System.out.println("port é nulo");
        else{
            String remoteHost = System.getProperty("remoteHost");
            int remotePort = Integer.parseInt(System.getProperty("remotePort"));
            int port = Integer.parseInt(System.getProperty("port"));
            TcpIpProxy tcpIpProxy = new TcpIpProxy(remoteHost, remotePort, port);
            tcpIpProxy.listen();
            }
    }


}