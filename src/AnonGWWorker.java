import java.net.DatagramSocket;
import java.net.ServerSocket;

public class AnonGWWorker implements Runnable{

    private final String remoteIp;
    private final int remotePort;
    private int[] peers;
    private int tcpPort;
    private int udpPort;

    public AnonGWWorker(String remoteIp, int remotePort, int[] p,int tcpPort, int udpPort) {
        this.remoteIp = remoteIp;     // localhost
        this.remotePort = remotePort; // 80
        this.peers = p;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    /**
     * Cria um socket (do cliente) para cada conexão efetuada na porta de entrada
     */
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(tcpPort);
            DatagramSocket socket_udp = new DatagramSocket(udpPort);
            System.out.println("listening...");
            new Thread(new TcpReceiver(ss,peers)).start();
            new Thread(new UdpReceiver(socket_udp, remoteIp, remotePort)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}