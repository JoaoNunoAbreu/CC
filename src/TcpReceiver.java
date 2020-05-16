import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.net.DatagramPacket;
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
     *
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