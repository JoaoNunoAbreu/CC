import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost",12345); // Únicos valores a alterar são estes

        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        pw.println("Hello World!");
        pw.flush();
        System.out.println(br.readLine());

        s.shutdownOutput();
        s.shutdownInput();
        s.close();
    }
}
