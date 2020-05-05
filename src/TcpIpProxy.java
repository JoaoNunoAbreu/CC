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

    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("listening...");
            while (true) {
                Socket socket = serverSocket.accept();
                startThread(new Connection(socket, remoteIp, remotePort));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startThread(Connection connection) {
        Thread t = new Thread(connection);
        t.start();
    }
}