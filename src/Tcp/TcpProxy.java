package Tcp;

import Encryption.AESencrp;
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
            byte[] buf = new byte[2048];
            int rnd = new Random().nextInt(peers.length);
            System.out.println("List of peers = " + Arrays.toString(peers) + ", however " + peers[rnd] + " was the chosen one.");
            DatagramSocket socket_udp = new DatagramSocket();

            /* À espera de dados do socket */
            InputStream in = s.getInputStream();
            int size = in.read(buf);
            System.out.println("Linha recebida do cliente: " + Arrays.toString(buf) + " com tamanho = " + size);

            /* Encriptação */
            byte[] dados_encriptados = AESencrp.encryptData("pass", buf);

            /* Criação do PDU */
            PDU pacote = new PDU(dados_encriptados,dados_encriptados.length);
            pacote.setTarget_response(s.getInetAddress().getHostName());

            /* Envio do PDU */
            byte[] mensagem = pacote.toBytes();
            System.out.println("Mensagem a enviar para próximo anon: " + Arrays.toString(mensagem));
            DatagramPacket sender = new DatagramPacket(mensagem,mensagem.length,peers[rnd],port);
            socket_udp.send(sender);

            Arrays.fill(buf,(byte)0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}