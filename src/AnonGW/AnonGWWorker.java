package AnonGW;

import Tcp.TcpReceiver;
import Udp.UdpReceiver;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Arrays;

public class AnonGWWorker implements Runnable{

    private final String remoteIp;
    private final int remotePort;
    private int[] nextPeers;
    private int tcpPort;
    private int udpPort;

    public AnonGWWorker(String remoteIp, int remotePort, int[] p,int tcpPort, int udpPort) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.nextPeers = removeElements(p,udpPort);
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public static int[] removeElements(int[] arr, int key) {
        int index = 0;
        for (int i=0; i<arr.length; i++)
            if (arr[i] != key)
                arr[index++] = arr[i];
        return Arrays.copyOf(arr, index);
    }

    /**
     * Cria um socket (do cliente) para cada conexão efetuada na porta de entrada
     */
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(tcpPort);
            DatagramSocket socket_udp = new DatagramSocket(udpPort);
            System.out.println("listening...");
            /**
             * Falta aqui garantir que tiramos o peer que representa esta instância do AnonGW
             */
            new Thread(new TcpReceiver(ss,nextPeers)).start();
            new Thread(new UdpReceiver(socket_udp, remoteIp, remotePort)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}