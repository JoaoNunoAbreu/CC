package Udp;

import AnonGW.Ligacao;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.*;

public class UdpProxy implements Runnable {
    private InetAddress ip_anterior;
    private int porta_anterior;
    private InetAddress remoteIp;
    private int remotePort;
    private PDU pdu;
    private Map<Ligacao, Socket> tcp_sockets;
    private Hashtable<Ligacao, List<PDU>> pdu_map;

    public UdpProxy(InetAddress ip_anterior, int porta_anterior, InetAddress remoteIp, int remotePort, PDU pdu, Map<Ligacao, Socket> tcp_sockets, Hashtable<Ligacao, List<PDU>> pdu_map){
        this.ip_anterior = ip_anterior;
        this.porta_anterior = porta_anterior;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.pdu = pdu.clone();
        this.tcp_sockets = tcp_sockets;
        this.pdu_map = pdu_map;
    }

    /**
     * Envia pacotes no sentido Anon --> Server, Server --> Anon e Anon --> Anon anterior
     */
    @Override
    public void run() {
        try {
            DatagramSocket udp = new DatagramSocket();
            Socket tcp_final;
            Ligacao l;

            SecretKey chave;
            Cipher decifrar = Cipher.getInstance("DES/ECB/PKCS5Padding");
            Cipher encriptar = Cipher.getInstance("DES/ECB/PKCS5Padding");

            boolean isResposta = pdu.getIsResposta() > 0;
            /* Reestablece ligação TCP mas na ordem contrária, visto que é uma resposta */
            if(isResposta){
                l = new Ligacao(InetAddress.getByName(pdu.getTarget_response()), remoteIp);
                tcp_final = tcp_sockets.get(l);
            }
            /* Obter dados do cliente */
            else {
                l = new Ligacao(remoteIp, InetAddress.getByName(pdu.getTarget_response()));
                if(tcp_sockets.containsKey(l)) {
                    tcp_final = tcp_sockets.get(l);
                } else {
                    tcp_final = new Socket(remoteIp, remotePort);
                    tcp_sockets.put(l, tcp_final);
                    pdu_map.put(l, new ArrayList<PDU>());
                }
            }

            System.out.println("Establecida ligação " + tcp_final.toString());

            /* Ligação TCP */
            InputStream br = tcp_final.getInputStream();
            OutputStream pw = tcp_final.getOutputStream();

            if(pdu.getIsLast() == 0){
                System.out.println("Pacote com seqNumber = " + pdu.getSeqNumber() + " foi adicionado -> " + pdu.getFileData().length);
                pdu_map.get(l).add(pdu.clone());
                return ;
            } else {
                /* Decifra a chave que vem no último pacote */
                byte[] key = pdu.getFileData();
                chave = new SecretKeySpec(key, 0, key.length, "DES");

                if (isResposta)
                    System.out.println("Resposta: (IPHost -> IPTarget) ------> (" + remoteIp + " -> " + pdu.getTarget_response() + ")");
                else
                    System.out.println("Não resposta: (IPHost -> IPTarget) ------> (" + pdu.getTarget_response() + " -> " + remoteIp + ")");

                /* Reorganiza os pacotes pela ordem do seqNumber */
                Collections.sort(pdu_map.get(l));

                /* Inicializa a decifragem com a chave obtida */
                decifrar.init(Cipher.DECRYPT_MODE, chave);

                int total_bytes = 0;
                /* Envia todos os pacotes recolhidos, por ordem */
                for(PDU send : pdu_map.get(l)){
                    byte[] decryptedData = decifrar.doFinal(send.getFileData());
                    total_bytes += decryptedData.length;
                    pw.write(decryptedData);
                    pw.flush();
                }
                /* Uma vez enviados, são apagados da lista */
                pdu_map.get(l).clear();
                System.out.println("A enviar " + total_bytes + " bytes para o target.");
            }

            /* Leitura de dados vindos do target */
            byte[] data = new byte[1448];
            int packet_num = 0;
            int count, total = 0;

            /* Inicializa a encriptagem com a chave obtida */
            encriptar.init(Cipher.ENCRYPT_MODE, chave);

            while((count = br.read(data)) != -1) {
                total += count;
                System.out.println("Pacote com seqNumber = " + packet_num + " foi adicionado -> bytes = " + count);

                /* Criação do PDU com os dados encriptados */
                byte[] encryptedData = encriptar.doFinal(Arrays.copyOfRange(data, 0, count));
                PDU pdu = new PDU(encryptedData,encryptedData.length);
                pdu.setTarget_response(this.pdu.getTarget_response());
                pdu.setIsResposta(1);
                pdu.setSeqNumber(packet_num++);

                /* Envia para o Anon anterior */
                byte[] send_data = pdu.toBytes();
                DatagramPacket send = new DatagramPacket(send_data, send_data.length, ip_anterior, porta_anterior);
                udp.send(send);
            }
            System.out.println("Nº de bytes enviados: " + total);

            Thread.sleep(100);

            if(packet_num > 0) {
                /* A enviar o último pacote, com a key */
                PDU last = new PDU(chave.getEncoded(),chave.getEncoded().length);
                last.setTarget_response(pdu.getTarget_response());
                last.setIsResposta(1);
                last.setIsLast(1);
                last.setSeqNumber(packet_num);

                /* Envia para o Anon anterior */
                DatagramPacket send = new DatagramPacket(last.toBytes(), last.toBytes().length, ip_anterior, porta_anterior);
                udp.send(send);
                System.out.println("Pacote com a key foi enviado");
            }
            udp.close();
        } catch(BadPaddingException e){
            System.out.println("Chave inválida.") ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
