import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String [] args) throws Exception {

        ServerSocket ss = new ServerSocket(12345); // Alterar número da porta

        while(true){
            Socket s = ss.accept();
            new Thread(new MultiThreadedServer(s)).start();
        }
    }
}
