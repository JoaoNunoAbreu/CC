package AnonGW;

import Tcp.TcpReceiver;
import Udp.PDU;
import Udp.UdpReceiver;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class AnonGWWorker{

    private final InetAddress remoteIp;
    private final int remotePort;
    private InetAddress[] nextPeers;

    public AnonGWWorker(InetAddress remoteIp, int remotePort, InetAddress[] p) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.nextPeers = p.clone();
    }

    public void listen() {
        try {
            /* Sabendo o host->target, sabemos o correspondente Socket */
            Map<Ligacao, Socket> tcp_sockets = new HashMap<>();
            /* Guarda os PDU's de cada ligação */
            Hashtable<Ligacao, List<PDU>> pdu = new Hashtable<>();

            /* Cada um dos anons passará a ouvir conexões TCP e UDP na porta 80 (portaTCP) e 6666 (portaUDP), respetivamente */
            ServerSocket ss = new ServerSocket(80);
            DatagramSocket socket_udp = new DatagramSocket(6666);

            new Thread(new TcpReceiver(ss,nextPeers,remoteIp,6666,tcp_sockets,pdu)).start();
            new Thread(new UdpReceiver(socket_udp, remoteIp, remotePort,tcp_sockets,pdu)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}