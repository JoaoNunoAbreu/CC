import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String [] args) throws Exception {

        /**
         * Recebe a porta que estará aberta a conexões
         */
        if(args.length != 1){
            System.out.println("Número de argumentos inválidos!");
        }
        else {
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));

            while (true) {
                Socket s = ss.accept();
                new Thread(new ServerWorker(s)).start();
            }
        }
    }
}
