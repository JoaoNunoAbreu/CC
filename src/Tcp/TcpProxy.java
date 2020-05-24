package Tcp;

import Encryption.AESencrp;
import Udp.PDU;

import javax.crypto.Cipher;
import javax.xml.crypto.Data;
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
            byte[] buf = new byte[2068];
            int rnd = new Random().nextInt(peers.length);
            System.out.println("List of peers = " + Arrays.toString(peers) + ", however " + peers[rnd] + " was the chosen one.");
            DatagramSocket socket_udp = new DatagramSocket();

            /* À espera de dados do socket */
            InputStream in = s.getInputStream();
            int size = in.read(buf);
            System.out.println("Linha recebida do cliente: " + Arrays.toString(buf) + " com tamanho = " + size);

            buf = Arrays.copyOfRange(buf, 0, size);

            /* Encriptação */
            byte[] dados_encriptados = AESencrp.encrypt(buf);
            System.out.println("TCPProxy: dados_encriptados.length = " + dados_encriptados.length);

            /* Criação do PDU */
            PDU pacote = new PDU(dados_encriptados,dados_encriptados.length);
            pacote.setTarget_response(s.getInetAddress().getHostName());

            /* Envio do PDU */
            byte[] mensagem = pacote.toBytes();
            System.out.println("Mensagem a enviar para próximo anon: " + Arrays.toString(mensagem));
            DatagramPacket sender = new DatagramPacket(mensagem,mensagem.length,peers[rnd],port);
            socket_udp.send(sender);

            //Thread.sleep(100);

            /* Envio do pacote final
            PDU last = new PDU(encodedKey,encodedKey.length);
            last.setIsLast(1);
            last.setTarget_response(s.getInetAddress().getHostName());
            DatagramPacket last_packet = new DatagramPacket(last.toBytes(), last.toBytes().length, peers[rnd], port);
            socket_udp.send(last_packet);*/

            Arrays.fill(buf,(byte)0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}