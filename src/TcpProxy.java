import java.io.*;
import java.net.*;
import java.util.Random;

public class TcpProxy implements Runnable {

    private Socket s;
    private int[] peers;

    public TcpProxy(Socket s, int[] peers) {
        this.s = s;
        this.peers = peers;
    }

    /**
     * Lê do cliente e manda para próximo anon escolhido aleatoriamente
     */
    @Override
    public void run() {
        try{
            int rnd = new Random().nextInt(peers.length);
            DatagramSocket socket_udp = new DatagramSocket(peers[rnd], InetAddress.getLocalHost());

            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while(true){
                String line = br.readLine();
                if(line == null)
                    break;

                byte[] mensagem = line.getBytes();
                DatagramPacket sender = new DatagramPacket(mensagem,mensagem.length,InetAddress.getLocalHost(),peers[rnd]);
                socket_udp.send(sender);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}