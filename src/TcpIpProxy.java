import java.net.ServerSocket;
import java.net.Socket;

public class TcpIpProxy {

    private final String remoteIp;
    private final int remotePort;
    private final int port;

    public TcpIpProxy(String remoteIp, int remotePort, int port) {
        this.remoteIp = remoteIp;     // localhost
        this.remotePort = remotePort; // 12346
        this.port = port;             // 12345
    }

    /**
     * Cria um socket (do cliente) para cada conexão efetuada na porta de entrada
     */
    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("listening...");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new Connection(socket, remoteIp, remotePort)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}