import PDU.PDU;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class AnonGWRunnable implements Runnable {
    private int tcp_port;
    private int udp_port;
    private InetAddress[] peers;
    private int redirectToPort;
    private InetAddress redirectToAdress;

    public AnonGWRunnable(int tcp_port, int udp_port, InetAddress[] peers, int redirectToPort, InetAddress redirectToAdress){
        this.tcp_port = tcp_port;
        this.udp_port = udp_port;
        this.peers = peers.clone();
        this.redirectToPort = redirectToPort;
        this.redirectToAdress = redirectToAdress;
    }

    @Override
    public void run() {
        /* Initialize the shared data */
        Map<Session, Socket> tcp_sockets = new HashMap<>();
        Hashtable<Session, List<PDU>> pdu_map = new Hashtable<>();
        try {
            ServerSocket tcp_ss = new ServerSocket(tcp_port);
            DatagramSocket udp_socket = new DatagramSocket(udp_port);
            System.out.println("AnonGW is listening to TCP on port " + tcp_port + " and to UDP on port " + udp_port + "!");
	        //System.out.println("UDP listening on port " + udp_socket.getLocalPort() + " and on adress " + InetAddress.getLocalHost().getHostAddress() + "!");
            /* Throw thread to listen to UDP */
            new Thread(new UDPListener(udp_socket, redirectToPort, redirectToAdress, tcp_sockets, pdu_map)).start();
            /* Throw thread to listen to TCP */
            new Thread(new TCPListener(tcp_ss, peers, udp_port, tcp_sockets, pdu_map, redirectToAdress)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
