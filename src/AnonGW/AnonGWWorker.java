package AnonGW;

import Tcp.TcpReceiver;
import Udp.UdpReceiver;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class AnonGWWorker{

    private final InetAddress remoteIp;
    private final int remotePort;
    private InetAddress[] nextPeers;
    private int tcpPort;
    private int udpPort;

    public AnonGWWorker(InetAddress remoteIp, int remotePort, InetAddress[] p,int tcpPort, int udpPort) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.nextPeers = p.clone();
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }
    /**
     * Cria um socket (do cliente) para cada conexão efetuada na porta de entrada
     */
    public void listen() {
        try {
            Map<InetAddress, Socket> tcp_sockets = new HashMap<>();
            ServerSocket ss = new ServerSocket(tcpPort);
            DatagramSocket socket_udp = new DatagramSocket(udpPort);
            System.out.println("listening TCP in port: " + tcpPort + " and UDP in port:" + udpPort);
            System.out.println("UDP listening on port: " + socket_udp.getLocalPort() + " and on address " + socket_udp.getLocalAddress());
            new Thread(new TcpReceiver(ss,nextPeers,socket_udp.getLocalAddress(),udpPort,tcp_sockets)).start();
            new Thread(new UdpReceiver(socket_udp, remoteIp, remotePort,tcp_sockets)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}