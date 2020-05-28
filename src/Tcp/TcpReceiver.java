package Tcp;

import AnonGW.Ligacao;
import Udp.PDU;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class TcpReceiver implements Runnable {

    private ServerSocket ss;
    private InetAddress[] peers;
    private InetAddress ipTarget;
    private int port;
    private Map<Ligacao,Socket> tcp_sockets;
    private Hashtable<Ligacao, List<PDU>> pdu;

    public TcpReceiver(ServerSocket ss, InetAddress[] peers, InetAddress ipTarget, int port, Map<Ligacao, Socket> tcp_sockets, Hashtable<Ligacao, List<PDU>> pdu) {
        this.ss = ss;
        this.peers = peers;
        this.ipTarget = ipTarget;
        this.port = port;
        this.tcp_sockets = tcp_sockets;
        this.pdu = pdu;
    }

    /**
     * Está à escuta de ligações TCP. Dá inicio ao processo de comunicação entre o cliente e o AnonGW escolhido.
     */
    public void run() {
        try {
            while (true) {
                Socket s = ss.accept();
                Ligacao l = new Ligacao(s.getInetAddress(),ipTarget);

                tcp_sockets.remove(l);
                tcp_sockets.put(l,s);
                pdu.put(l,new ArrayList<PDU>());

                /* Gera a única chave */
                KeyGenerator keygen = KeyGenerator.getInstance("DES");
                SecretKey chave = keygen.generateKey();

                /* Cria a cifra e inicializa */
                Cipher cifra = Cipher.getInstance("DES/ECB/PKCS5Padding");
                cifra.init(Cipher.ENCRYPT_MODE, chave);

                new Thread(new TcpProxy(s,peers,port,chave.getEncoded(), cifra)).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}