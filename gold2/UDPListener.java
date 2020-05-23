import PDU.PDU;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class UDPListener implements Runnable {
    private DatagramSocket udp_socket;
    private byte[] receivedData;
    private PDU packet;
    private int redirectToPort;
    private InetAddress redirectToAdress;
    private Map<Session, Socket> tcp_sockets;
    private Hashtable<Session,List<PDU>> pdu_map;

    public UDPListener(DatagramSocket udp_socket, int redirectToPort, InetAddress redirectToAdress, Map<Session, Socket> tcp_sockets, Hashtable<Session, List<PDU>> pdu_map){
        this.udp_socket = udp_socket;
        this.receivedData = new byte[2000];
        packet = new PDU();
        this.redirectToPort = redirectToPort;
        this.redirectToAdress = redirectToAdress;
        this.tcp_sockets = tcp_sockets;
        this.pdu_map = pdu_map;
    }


    @Override
    public void run() {
        try {
            while (true) {
                /* Receive a packet from the other AnonGW */
                DatagramPacket receive = new DatagramPacket(receivedData, receivedData.length);
                udp_socket.receive(receive);
                packet.fromBytes(receive.getData(), receive.getLength());
                /* Launch a thread to process the packet */
                new Thread(new UDPRunnable(6666, receive.getAddress(), packet, redirectToPort, redirectToAdress, tcp_sockets, pdu_map)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
