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

    @Override
    public void run() {
        System.out.println("new connection " + clientsocket.getInetAddress().getHostName()+":" + clientsocket.getPort());
        try {
            serverConnection = new Socket(remoteIp, remotePort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Proxy " + clientsocket.getInetAddress().getHostName() + ":" + clientsocket.getPort() + " <-> " + serverConnection.getInetAddress().getHostName()+":"+serverConnection.getPort());

        new Thread(new Proxy(clientsocket, serverConnection)).start();
        new Thread(new Proxy(serverConnection, clientsocket)).start();
        new Thread(() -> {
            while (true) {
                if (clientsocket.isClosed()) {
                    System.out.println("client socket ( " + clientsocket.getInetAddress().getHostName()+":" + clientsocket.getPort());
                    closeServerConnection();
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void closeServerConnection() {
        if (serverConnection != null && !serverConnection.isClosed()) {
            try {
                System.out.println("closing remote host connection " + serverConnection.getInetAddress().getHostName() + ":" + serverConnection.getPort());
                serverConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}