package Tcp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class TcpReceiver implements Runnable {

    private ServerSocket ss;
    private InetAddress[] peers;
    private InetAddress ip_local;
    private int port;
    private Map<InetAddress,Socket> tcp_sockets;

    public TcpReceiver(ServerSocket ss, InetAddress[] peers, InetAddress ip_local, int port, Map<InetAddress, Socket> tcp_sockets) {
        this.ss = ss;
        this.peers = peers;
        this.ip_local = ip_local;
        this.port = port;
        this.tcp_sockets = tcp_sockets;
    }

    /**
     * Recebe as conexões TCP e dá início ao processo de comunicação entre cliente e próximo Anon
     */
    public void run() {
        try {
            while (true) {
                Socket s = ss.accept();
                tcp_sockets.put(ip_local,s);
                new Thread(new TcpProxy(s,peers,port)).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}