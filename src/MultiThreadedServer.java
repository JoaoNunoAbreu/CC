import java.io.*;
import java.net.Socket;

public class MultiThreadedServer implements Runnable{

    private Socket s;

    public MultiThreadedServer(Socket s){
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            String line;
            while (true) {
                line = br.readLine();
                if(line == null)
                    break;
                pw.println(line);
                pw.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
