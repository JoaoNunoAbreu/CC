import PDU.PDU;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class TCPRunnable implements Runnable {
    private Socket s;
    private InetAddress[] peers;
    private int random_peer;
    private int port;
    private byte[] encodedKey;
    private Cipher cipher;

    public TCPRunnable(Socket s, InetAddress[] peers, int port, int random_peer, byte[] encodedKey, Cipher cipher){
        this.s = s;
        this.peers = peers;
    	this.random_peer = random_peer;
        this.port = port;
        this.encodedKey = encodedKey;
        this.cipher = cipher;
    }

    public void run(){
        try {
            DatagramSocket socket = new DatagramSocket();

            /* Get the input stream and declare an array of bytes to get info */
            InputStream brc = s.getInputStream();
            byte[] data = new byte[1448];

            /* While the customer sends info keep sending packets */
            int count = brc.read(data);
            /* Create PDU with the encrypted data */
            byte[] encryptedData = cipher.doFinal(Arrays.copyOfRange(data, 0, count));
            PDU pdu = new PDU(encryptedData);
            pdu.setAnswerTo(s.getInetAddress().getHostName());
            byte[] pdu_send = pdu.toBytes();

            /* Create datagram packet with the info */
            DatagramPacket send = new DatagramPacket(pdu_send, pdu_send.length, peers[random_peer], port);
            socket.send(send);
            System.out.println("Sent packet with client request to" + peers[random_peer] + "!");
            Thread.sleep(100);

            /* Send the terminating packet */
            PDU last = new PDU(encodedKey);
            last.setIsLast(1);
            last.setAnswerTo(s.getInetAddress().getHostName());
            DatagramPacket last_packet = new DatagramPacket(last.toBytes(), last.toBytes().length, peers[random_peer], port);
            socket.send(last_packet);

            /* Sent it to the chosen AnonGW */
            Arrays.fill(data, (byte) 0);
        }
        catch (IOException | InterruptedException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
