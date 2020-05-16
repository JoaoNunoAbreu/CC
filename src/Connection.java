import java.io.IOException;
import java.net.Socket;


public class Connection implements Runnable {

    private final Socket clientsocket;
    private final String remoteIp;
    private final int remotePort;
    private Socket serverConnection = null;

    public Connection(Socket clientsocket, String remoteIp, int remotePort) {
        this.clientsocket = clientsocket;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
    }

    /**
     * Cria um novo socket (destino)
     * Estabelece ligação entre o socket criado anteriormente este (nos 2 sentidos)
     */
    @Override
    public void run() {
        System.out.println("new connection " + clientsocket.getInetAddress().getHostName()+":" + clientsocket.getPort());
        try {
            System.out.println("remoteIp = " + remoteIp);
            System.out.println("remotePort = " + remotePort);
            serverConnection = new Socket(remoteIp, remotePort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Proxy " + clientsocket.getInetAddress().getHostName() + ":" + clientsocket.getPort() + " <-> " + serverConnection.getInetAddress().getHostName()+":"+serverConnection.getPort());

        new Thread(new Proxy(clientsocket, serverConnection)).start();
        new Thread(new Proxy(serverConnection, clientsocket)).start();
    }
}