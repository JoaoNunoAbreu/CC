import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            System.out.println("Número de argumentos inválidos!");
        }
        else {
            Socket s = new Socket("localhost", Integer.parseInt(args[0])); // Únicos valores a alterar são estes

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
}
