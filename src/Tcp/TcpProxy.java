package Tcp;

import Udp.PDU;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

public class TcpProxy implements Runnable {

    private Socket s;
    private InetAddress[] peers;
    private int port;

    public TcpProxy(Socket s, InetAddress[] peers, int port) {
        this.s = s;
        this.peers = peers;
        this.port = port;
    }

    /**
     * Recebe informação do cliente e manda para próximo anon escolhido aleatoriamente
     */
    @Override
    public void run() {
        try{
            byte[] buf = new byte[1480];
            int rnd = new Random().nextInt(peers.length);
            System.out.println("List of peers = " + Arrays.toString(peers) + ", however " + peers[rnd] + " was the chosen one.");
            DatagramSocket socket_udp = new DatagramSocket();

            InputStream in = s.getInputStream();
            in.read(buf);
            System.out.println("Linha recebida: " + Arrays.toString(buf));

            PDU pacote = PDU.fromBytes(buf);
            byte[] mensagem = PDU.toBytes(pacote);

            DatagramPacket sender = new DatagramPacket(mensagem,mensagem.length,peers[rnd],port);
            socket_udp.send(sender);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}