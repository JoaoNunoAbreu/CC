package Udp;

import AnonGW.Ligacao;
import Encryption.AESencrp;

import java.io.*;
import java.net.*;
import java.util.*;

public class UdpProxy implements Runnable {

    private InetAddress ip_anterior;
    private int porta_anterior;
    private InetAddress remoteIp;
    private int remotePort;
    private PDU pacote;
    private Map<Ligacao,Socket> tcp_sockets;
    private Hashtable<Ligacao, List<PDU>> pdu;

    public UdpProxy(InetAddress ip_anterior, int porta_anterior, InetAddress remoteIp, int remotePort, PDU pacote, Map<Ligacao, Socket> tcp_sockets, Hashtable<Ligacao, List<PDU>> pdu) {
        this.ip_anterior = ip_anterior;
        this.porta_anterior = porta_anterior;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.pacote = pacote;
        this.tcp_sockets = tcp_sockets;
        this.pdu = pdu;
    }

    /**
     * Troca mensagens entre um socket UDP e um socket TCP
     */
    @Override
    public void run() {
        try{
            DatagramSocket socket_udp = new DatagramSocket();
            Socket tcp_final;
            Ligacao l;
            int isResposta = pacote.getIsResposta();

            /* Já obteve resposta do servidor, está a voltar para trás */
            if(isResposta == 1){
                l = new Ligacao(InetAddress.getByName(pacote.getTarget_response()),remoteIp);
                tcp_final = tcp_sockets.get(l);
            }
            /* A obter resposta do cliente */
            else{
                l = new Ligacao(remoteIp,InetAddress.getByName(pacote.getTarget_response()));
                if(tcp_sockets.containsKey(l)){
                    tcp_final = tcp_sockets.get(l);
                }
                else{
                    tcp_final = new Socket(remoteIp,remotePort);
                    tcp_sockets.put(l,tcp_final);
                    pdu.put(l,new ArrayList<PDU>());
                }
            }

            /* Ligação TCP */
            InputStream in = tcp_final.getInputStream();
            OutputStream out = tcp_final.getOutputStream();
            System.out.println("A enviar através de tcp: " + Arrays.toString(pacote.toBytes()));

            if(isResposta == 1)
                System.out.println("Resposta: (IPHost -> IPTarget) ------> (" + remoteIp + " -> " + pacote.getTarget_response() + ")");
            else System.out.println("Não resposta: (IPHost -> IPTarget) ------> (" + pacote.getTarget_response() + " -> " + remoteIp + ")");

            Collections.sort(pdu.get(l));

            /* Decifrar cada PDU da ligação que foi establecida */
            for(PDU sender: pdu.get(l)){
                byte[] dados_decifrados = AESencrp.decrypt(sender.getFileData());
                out.write(dados_decifrados);
                out.flush();
            }
            pdu.get(l).clear();

            byte[] info = new byte[2048];
            int seqNumber = 0;
            int size, total_size = 0;
            while((size = in.read(info)) != -1){
                byte[] dados_encriptados = AESencrp.encrypt(info);
                PDU pacote_sender = new PDU(dados_encriptados,dados_encriptados.length);
                pacote_sender.setTarget_response(pacote.getTarget_response());
                pacote_sender.setIsResposta(1);
                pacote_sender.setSeqNumber(seqNumber);
                /* Envia para o próximo Anon */

                byte[] mensagem = pacote_sender.toBytes();
                DatagramPacket sender = new DatagramPacket(mensagem,mensagem.length,remoteIp,remotePort);
                socket_udp.send(sender);

                seqNumber++;
                total_size += size;
            }
            socket_udp.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}