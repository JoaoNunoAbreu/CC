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

    public void run(){
        try{
            while (true){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket_udp.receive(packet);
                PDU pacote = PDU.fromBytes(packet.getData());

                UdpProxy udp_proxy = new UdpProxy(packet.getAddress(),packet.getPort(),remoteIp,remotePort,pacote);
                new Thread(udp_proxy).start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
