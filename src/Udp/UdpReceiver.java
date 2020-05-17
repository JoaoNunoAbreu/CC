package Udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpReceiver implements Runnable{
    private DatagramSocket socket_udp;
    private String remoteIp;
    private int remotePort;
    private byte[] buf = new byte[4096];

    public UdpReceiver(DatagramSocket socket_udp, String remoteIp, int remotePort) {
        this.socket_udp = socket_udp;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
    }

    /**
     * Recebe informação de um Anon e dá início ao processo de comunicação entre anon e servidor
     */
    public void run(){
        try{
            while (true){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket_udp.receive(packet);
                PDU pacote = PDU.fromBytes(packet.getData());

                new Thread(new UdpProxy(packet.getAddress(),packet.getPort(),remoteIp,remotePort,pacote)).start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
