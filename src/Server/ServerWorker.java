package Server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerWorker implements Runnable{

    private Socket s;

    public ServerWorker(Socket s){
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            while (true) {
                String line;
                line = br.readLine();
                if(line == null)
                    break;
                pw.println("Enviado pelo server: " + line);
                pw.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
