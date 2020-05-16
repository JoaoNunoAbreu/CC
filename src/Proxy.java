import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Proxy implements Runnable {

    private final Socket in;
    private final Socket out;

    public Proxy(Socket in, Socket out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Troca mensagens entre 2 sockets
     */
    @Override
    public void run() {
        System.out.println("Proxy " + in.getInetAddress().getHostName()+":"+in.getPort() +"-->" +out.getInetAddress().getHostName()+":"+out.getPort());
        try {
            InputStream inputStream = in.getInputStream();
            OutputStream outputStream = out.getOutputStream();

            if (inputStream == null || outputStream == null) {
                System.out.println("InputStream ou outputStream é nulo");
                return;
            }

            byte[] reply = new byte[4096];
            int bytesRead;
            while (-1 != (bytesRead = inputStream.read(reply))) {
                outputStream.write(reply, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}