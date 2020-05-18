package Udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

public class UdpReceiver implements Runnable{
    private DatagramSocket socket_udp;
    private InetAddress remoteIp;
    private int remotePort;
    private Map<InetAddress, Socket> tcp_sockets;

    public UdpReceiver(DatagramSocket socket_udp, InetAddress remoteIp, int remotePort,Map<InetAddress, Socket> tcp_sockets) {
        this.socket_udp = socket_udp;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.tcp_sockets = tcp_sockets;
    }

    /**
     * Recebe informação de um Anon e dá início ao processo de comunicação entre anon e servidor
     */
    public void run(){
        try{
            while (true){
                byte[] buf = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket_udp.receive(packet);
                PDU pacote = new PDU();
                pacote.setFileData(packet.getData());

                new Thread(new UdpProxy(packet.getAddress(),6666,remoteIp,remotePort,pacote,tcp_sockets)).start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
