import javax.print.attribute.standard.ReferenceUriSchemesSupported;
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
     * Cria um socket (do cliente) para cada conexão efetuada na porta de entrada
     */
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("listening...");
            while (true) {
                Socket socket = serverSocket.accept();
                // Falta aqui o código que chama o TcpProxy.java
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}