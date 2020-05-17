package Tcp;

import java.net.ServerSocket;
import java.net.Socket;

public class TcpReceiver implements Runnable {

    private ServerSocket ss;
    private int[] peers;

    public TcpReceiver(ServerSocket ss, int[] peers) {
        this.ss = ss;
        this.peers = peers;
    }

    /**
     * Recebe as conexões TCP e dá início ao processo de comunicação entre cliente e próximo Anon
     */
    public void run() {
        try {
            while (true) {
                Socket s = ss.accept();
                new Thread(new TcpProxy(s, peers)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}