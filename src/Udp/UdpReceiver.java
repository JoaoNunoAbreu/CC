package Udp;

import AnonGW.Ligacao;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class UdpReceiver implements Runnable{
    private DatagramSocket socket_udp;
    private InetAddress remoteIp;
    private int remotePort;
    private Map<Ligacao, Socket> tcp_sockets;
    private Hashtable<Ligacao,List<PDU>> pdu;

    public UdpReceiver(DatagramSocket socket_udp, InetAddress remoteIp, int remotePort, Map<Ligacao, Socket> tcp_sockets, Hashtable<Ligacao, List<PDU>> pdu) {
        this.socket_udp = socket_udp;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.tcp_sockets = tcp_sockets;
        this.pdu = pdu;
    }

    public void run(){
        try{
            PDU pacote = new PDU();
            while (true) {
                byte[] buf = new byte[2068];
                /* Recebe um pacote através de uma ligação UDP */
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket_udp.receive(packet);

                /* Cria e preenche um PDU */
                pacote.fromBytes(packet.getData(), packet.getLength());
                System.out.println("pacote.getFileData() = " + Arrays.toString(pacote.getFileData()));

                /* Tratamento do PDU */
                new Thread(new UdpProxy(packet.getAddress(),6666, remoteIp, remotePort,pacote,tcp_sockets, pdu)).start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
