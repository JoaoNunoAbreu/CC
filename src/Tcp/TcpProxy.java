package Tcp;

import Udp.PDU;

import javax.crypto.Cipher;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

public class TcpProxy implements Runnable {

    private Socket s;
    private InetAddress[] peers;
    private int port;
    private byte[] encodedKey;
    private Cipher cipher;

    public TcpProxy(Socket s, InetAddress[] peers, int port, byte[] encodedKey, Cipher cipher) {
        this.s = s;
        this.peers = peers;
        this.port = port;
        this.encodedKey = encodedKey;
        this.cipher = cipher;
    }

    /**
     * Recebe a informação do cliente e envia para o AnonGW (escolhido aleatoriamente), juntamente com a key
     */
    @Override
    public void run() {
        try{
            byte[] buf = new byte[2048];
            int rnd = new Random().nextInt(peers.length);
            System.out.println("List of peers = " + Arrays.toString(peers) + ", however " + peers[rnd] + " was the chosen one.");
            DatagramSocket socket_udp = new DatagramSocket();

            /* À espera de dados do socket */
            InputStream in = s.getInputStream();
            int size = in.read(buf);
            System.out.println("Linha recebida do cliente: " + Arrays.toString(buf) + " com tamanho = " + size);

            /* Encriptação */
            byte[] dados_encriptados = cipher.doFinal(Arrays.copyOfRange(buf, 0, size));
            System.out.println("TCPProxy: dados_encriptados.length = " + dados_encriptados.length);

            /* Criação do PDU */
            PDU pacote = new PDU(dados_encriptados,dados_encriptados.length);
            pacote.setTarget_response(s.getInetAddress().getHostName());

            /* Envio do PDU */
            byte[] mensagem = pacote.toBytes();
            System.out.println("Mensagem a enviar para próximo anon: " + Arrays.toString(mensagem));
            DatagramPacket sender = new DatagramPacket(mensagem,mensagem.length,peers[rnd],port);
            socket_udp.send(sender);

            Thread.sleep(100);

            /* Envio do pacote final */
            PDU last = new PDU(encodedKey,encodedKey.length);
            last.setIsLast(1);
            last.setTarget_response(s.getInetAddress().getHostName());
            DatagramPacket last_packet = new DatagramPacket(last.toBytes(), last.toBytes().length, peers[rnd], port);
            socket_udp.send(last_packet);

            Arrays.fill(buf,(byte)0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}