package Udp;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;

public class UdpProxy implements Runnable {

    private InetAddress ip_anterior;
    private int porta_anterior;
    private InetAddress remoteIp;
    private int remotePort;
    private PDU pacote;
    private Map<InetAddress,Socket> tcp_sockets;

    public UdpProxy(InetAddress ip_anterior, int porta_anterior, InetAddress remoteIp, int remotePort, PDU pacote, Map<InetAddress, Socket> tcp_sockets) {
        this.ip_anterior = ip_anterior;
        this.porta_anterior = porta_anterior;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.pacote = pacote;
        this.tcp_sockets = tcp_sockets;
    }

    /**
     * Troca mensagens entre um socket UDP e um socket TCP
     */
    @Override
    public void run() {
        try{
            DatagramSocket socket_udp = new DatagramSocket();
            Socket tcp_final;

            if(tcp_sockets.containsKey(socket_udp.getLocalAddress())){
                tcp_final = tcp_sockets.get(socket_udp.getLocalAddress());
            }
            else{
                tcp_final = new Socket(remoteIp,remotePort);
                tcp_sockets.put(socket_udp.getLocalAddress(),tcp_final);
            }

            InputStream in = tcp_final.getInputStream();
            OutputStream out = tcp_final.getOutputStream();

            System.out.println("A enviar: " + Arrays.toString(pacote.getFileData()));

            out.write(pacote.getFileData());
            out.flush();

            byte[] mensagem = new byte[1024];
            int size = in.read(mensagem);
            byte[] shorten_buf = new byte[size];
            System.arraycopy(mensagem, 0, shorten_buf, 0, size);
            System.out.println("Linha recebida: " + Arrays.toString(shorten_buf));

            PDU pacote_sender = new PDU();
            pacote_sender.setFileData(shorten_buf);
            DatagramPacket sender = new DatagramPacket(pacote_sender.getFileData(),pacote_sender.getFileData().length,ip_anterior,porta_anterior);
            socket_udp.send(sender);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}