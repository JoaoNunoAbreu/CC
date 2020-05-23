import PDU.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TCPListener implements Runnable {
    private ServerSocket ss;
    private InetAddress[] peers;
    private int port;
    private Map<Session, Socket> tcp_sockets;
    private Hashtable<Session, List<PDU>> map;
    private InetAddress targetAddress;
    private Random random;

    public TCPListener(ServerSocket ss, InetAddress[] peers, int port, Map<Session, Socket> tcp_sockets, Hashtable<Session, List<PDU>> map, InetAddress targetAddress){
        this.ss = ss;
        this.peers = peers;
        this.port = port;
        this.tcp_sockets = tcp_sockets;
        this.targetAddress = targetAddress;
        this.map = map;
        random = new Random();
    }

    @Override
    public void run() {
        try {
            while (true) {
                /* Open socket */
                Socket s = ss.accept();
                /* Create new session and announce new session */
                System.out.println("New connection from " + s.getInetAddress());
                Session session = new Session(s.getInetAddress(), targetAddress); // client hoards data from target
                /* Store the session in the shared data structures (remove it if it already exists) */
                tcp_sockets.remove(session);
                tcp_sockets.put(session, s);
                map.put(session, new ArrayList<PDU>());
                /* Choose a random AnonGW to connect to */
                int random_peer = random.nextInt(peers.length);

                /* Generate key */
                KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
                SecretKey chaveDES = keygenerator.generateKey();
                System.out.println("Key used: " + Arrays.toString(chaveDES.getEncoded()));

                /* Create the cypher */
                Cipher cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

                /* Init the cypher */
                cifraDES.init(Cipher.ENCRYPT_MODE, chaveDES);

                /* Launch thread to work */
                new Thread(new TCPRunnable(s, peers, port, random_peer, chaveDES.getEncoded(), cifraDES)).start();
            }
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
