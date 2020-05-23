package Tcp;

import AnonGW.Ligacao;
import Udp.PDU;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class TcpReceiver implements Runnable {

    private ServerSocket ss;
    private InetAddress[] peers;
    private InetAddress ipTarget;
    private int port;
    private Map<Ligacao,Socket> tcp_sockets;
    private Hashtable<Ligacao, List<PDU>> pdu;

    public TcpReceiver(ServerSocket ss, InetAddress[] peers, InetAddress ipTarget, int port, Map<Ligacao, Socket> tcp_sockets, Hashtable<Ligacao, List<PDU>> pdu) {
        this.ss = ss;
        this.peers = peers;
        this.ipTarget = ipTarget;
        this.port = port;
        this.tcp_sockets = tcp_sockets;
        this.pdu = pdu;
    }

    /**
     * Recebe as conexões TCP e dá início ao processo de comunicação entre cliente e próximo Anon
     */
    public void run() {
        try {
            while (true) {
                Socket s = ss.accept();
                Ligacao l = new Ligacao(s.getInetAddress(),ipTarget);

                if(tcp_sockets.containsKey(l))
                    tcp_sockets.replace(l,s);
                else
                    tcp_sockets.put(l,s);

                new Thread(new TcpProxy(s,peers,port)).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}