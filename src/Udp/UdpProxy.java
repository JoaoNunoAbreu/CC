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
            Socket tcp;
            Ligacao session;
            DatagramSocket udp = new DatagramSocket();
            SecretKey secretKey;
            Cipher decriptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            Cipher encriptCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            boolean isAnswer = pdu.getIsResposta() > 0;
            /* Establish UDP connection with source AnonGW and TCP connection with target */
            if(isAnswer){
                session = new Ligacao(InetAddress.getByName(pdu.getTarget_response()), remoteIp); // client hoards data from target
                tcp = tcp_sockets.get(session);
            }
            /* Getting data from client here */
            else {
                session = new Ligacao(remoteIp, InetAddress.getByName(pdu.getTarget_response())); // target hoards data from client
                if(tcp_sockets.containsKey(session)) {
                    tcp = tcp_sockets.get(session);
                } else {
                    tcp = new Socket(remoteIp, remotePort);
                    tcp_sockets.put(session, tcp);
                    pdu_map.put(session, new ArrayList<PDU>());
                }
            }

            /* Open both ends for TCP communication */
            InputStream br = tcp.getInputStream();
            OutputStream pw = tcp.getOutputStream();

            /* Send the packet obtained to target (TEMPORARY) */
            if(pdu.getIsLast() == 0){
                System.out.println("Packet " + pdu.getSeqNumber() + " added to the list -> " + pdu.getFileData().length);
                pdu_map.get(session).add(pdu.clone());
                return ;
            } else {
                /* Decrypt key obtained from final packet */
                byte[] key = pdu.getFileData();
                //System.out.println("Hexadecimal key obtained: " + new String(pdu.getData()));
                System.out.println("Key used: " + Arrays.toString(key));
                secretKey = new SecretKeySpec(key, 0, key.length, "DES");

                /* Init the cypher */
                decriptCipher.init(Cipher.DECRYPT_MODE, secretKey);
                encriptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

                // Get the order right and send all packets
                int total_bytes = 0;

                if(isAnswer) System.out.println("Sending from session ---> (Host: " + remoteIp + ", Target: " + pdu.getTarget_response());
                else System.out.println("Sending from session ---> (Host: " + pdu.getTarget_response() + ", Target: " + remoteIp);
                // Sort the PDU's
                Collections.sort(pdu_map.get(session));

                // Send them
                for(PDU send : pdu_map.get(session)){
                    byte[] decryptedData = decriptCipher.doFinal(send.getFileData());
                    total_bytes += decryptedData.length;
                    pw.write(decryptedData);
                    pw.flush();
                }
                /* Clear the packets from the list since they have been sent */
                pdu_map.get(session).clear();
                System.out.println("Sending a total of " + total_bytes + " to the target.");
            }

            /* Keep getting data from target */
            byte[] data = new byte[1448];
            int packet_num = 0;
            int count, total = 0;
            while((count = br.read(data)) != -1) {
                total += count;
                System.out.println("Sent packet " + packet_num + " -> " + count);
                /* Wrap the data in a PDU */
                byte[] encryptedData = encriptCipher.doFinal(Arrays.copyOfRange(data, 0, count));
                PDU pdu = new PDU(encryptedData,encryptedData.length);
                pdu.setTarget_response(this.pdu.getTarget_response());
                pdu.setIsResposta(1);
                pdu.setSeqNumber(packet_num++);

                /* Send its to the other AnonGW */
                byte[] send_data = pdu.toBytes();
                DatagramPacket send = new DatagramPacket(send_data, send_data.length, ip_anterior, porta_anterior);
                udp.send(send);
            }
            System.out.println("SENT " + total + " BYTES!");

            Thread.sleep(100);

            if(packet_num > 0) {
                /* Send the terminating packet if any packets got send at all */
                PDU last = new PDU(secretKey.getEncoded(),secretKey.getEncoded().length);
                last.setTarget_response(pdu.getTarget_response());
                last.setIsResposta(1);
                last.setIsLast(1);
                last.setSeqNumber(packet_num);

                /* Send it to the other AnonGW */
                DatagramPacket send = new DatagramPacket(last.toBytes(), last.toBytes().length, ip_anterior, porta_anterior);
                udp.send(send);
                System.out.println("SENT LAST PACKET");
            }
            udp.close();
        } catch(BadPaddingException e){
            System.out.println("Key got corrupted! Try asking again.") ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
