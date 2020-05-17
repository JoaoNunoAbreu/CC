package Udp;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class UdpProxy implements Runnable {

    private InetAddress ip_anterior;
    private int porta_anterior;
    private String remoteIp;
    private int remotePort;
    private PDU pacote;

    public UdpProxy(InetAddress ip_anterior, int porta_anterior, String remoteIp, int remotePort, PDU pdu) {
        this.ip_anterior = ip_anterior;
        this.porta_anterior = porta_anterior;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.pacote = pdu;
    }

    /**
     * Troca mensagens entre um socket UDP e um socket TCP
     */
    @Override
    public void run() {
        try{
            DatagramSocket socket_udp = new DatagramSocket(porta_anterior,ip_anterior);
            Socket tcp_final = new Socket(remoteIp,remotePort);

            PrintWriter pw = new PrintWriter(tcp_final.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(tcp_final.getInputStream()));

            while(true){

                pw.println(Arrays.toString(PDU.toBytes(pacote)));

                String line = br.readLine();
                if(line == null)
                    break;

                byte[] mensagem = line.getBytes();
                DatagramPacket resposta = new DatagramPacket(mensagem,mensagem.length,ip_anterior,porta_anterior);
                socket_udp.send(resposta);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}