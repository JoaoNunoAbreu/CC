package Udp;

import AnonGW.Ligacao;
import Encryption.AESencrp;

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

    public UdpProxy(InetAddress ip_anterior, int porta_anterior, InetAddress remoteIp, int remotePort, PDU pdu, Map<Ligacao, Socket> tcp_sockets, Hashtable<Ligacao, List<PDU>> pdu_map) {
        this.ip_anterior = ip_anterior;
        this.porta_anterior = porta_anterior;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.pdu = pdu.clone();
        this.tcp_sockets = tcp_sockets;
        this.pdu_map = pdu_map;
    }

    @Override
    public void run() {
        try {
            DatagramSocket udp = new DatagramSocket();
            Socket tcp_final;
            Ligacao l;
            // FIXME checkar se é por causa de ser boolean
            boolean isResposta = pdu.getIsResposta() > 0;            /* Establish UDP connection with source AnonGW and TCP connection with target */
            if(isResposta){
                l = new Ligacao(InetAddress.getByName(pdu.getTarget_response()), remoteIp); // client hoards data from target
                tcp_final = tcp_sockets.get(l);
            }
            /* Getting data from client here */
            else {
                l = new Ligacao(remoteIp, InetAddress.getByName(pdu.getTarget_response())); // target hoards data from client
                if(tcp_sockets.containsKey(l)) {
                    tcp_final = tcp_sockets.get(l);
                } else {
                    tcp_final = new Socket(remoteIp, remotePort);
                    tcp_sockets.put(l, tcp_final);
                    pdu_map.put(l, new ArrayList<PDU>());
                }
            }

            /* Open both ends for TCP communication */
            InputStream br = tcp_final.getInputStream();
            OutputStream pw = tcp_final.getOutputStream();

            /* Send the packet obtained to target (TEMPORARY) */
            if(pdu.getIsLast() == 0){
                System.out.println("Packet " + pdu.getSeqNumber() + " added to the list -> " + pdu.getFileData().length);
                pdu_map.get(l).add(pdu.clone());
                return ;
            } else {
                /* Decrypt key obtained from final packet
                byte[] key = pdu.getFileData();*/
                /*System.out.println("Key used: " + Arrays.toString(key));
                secretKey = new SecretKeySpec(key, 0, key.length, "DES");*/

                /* Init the cypher
                decriptCipher.init(Cipher.DECRYPT_MODE, secretKey);
                encriptCipher.init(Cipher.ENCRYPT_MODE, secretKey);*/

                // Get the order right and send all packets
                int total_bytes = 0;

                if (isResposta)
                    System.out.println("Resposta: (IPHost -> IPTarget) ------> (" + remoteIp + " -> " + pdu.getTarget_response() + ")");
                else
                    System.out.println("Não resposta: (IPHost -> IPTarget) ------> (" + pdu.getTarget_response() + " -> " + remoteIp + ")");

                // Sort the PDU's
                Collections.sort(pdu_map.get(l));

                // Send them
                for(PDU sender : pdu_map.get(l)){
                    byte[] dados_decifrados = AESencrp.decrypt(sender.getFileData());
                    total_bytes += dados_decifrados.length;
                    pw.write(dados_decifrados);
                    pw.flush();
                    System.out.println("Estou na decifração do pdu");
                }
                /* Clear the packets from the list since they have been sent */
                pdu_map.get(l).clear();
                System.out.println("Sending a total of " + total_bytes + " to the target.");
            }

            /* Keep getting data from target */
            byte[] info = new byte[1448];
            int size, seqNumber = 0;
            int total = 0;
            while((size = br.read(info)) != -1) {
                total += size;
                System.out.println("Sent packet " + seqNumber + " -> " + size);

                /* Wrap the data in a PDU */

                info = Arrays.copyOfRange(info, 0, size);
                byte[] dados_encriptados = AESencrp.encrypt(info);
                PDU pdu = new PDU(dados_encriptados,dados_encriptados.length);
                pdu.setTarget_response(this.pdu.getTarget_response());
                pdu.setIsLast(1);
                pdu.setIsResposta(1);
                pdu.setSeqNumber(seqNumber++);

                /* Send its to the other AnonGW */
                byte[] send_data = pdu.toBytes();
                DatagramPacket send = new DatagramPacket(send_data, send_data.length, ip_anterior, porta_anterior);
                udp.send(send);
                System.out.println("Estou a ler do socket: " + tcp_final.getInetAddress());
            }
            System.out.println("SENT " + total + " BYTES!");

            /*Thread.sleep(100);

            if(seqNumber > 0) {
                /* Send the terminating packet if any packets got send at all
                PDU last = new PDU("111".getBytes(),"111".getBytes().length);
                last.setTarget_response(pdu.getTarget_response());
                last.setIsResposta(1);
                last.setIsLast(1);
                last.setSeqNumber(seqNumber);*/

                /* Send it to the other AnonGW
                DatagramPacket send = new DatagramPacket(last.toBytes(), last.toBytes().length, ip_anterior, porta_anterior);
                udp.send(send);
                System.out.println("SENT LAST PACKET");
            }*/
            udp.close();
        } catch(BadPaddingException e){
            System.out.println("Key got corrupted! Try asking again.") ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
