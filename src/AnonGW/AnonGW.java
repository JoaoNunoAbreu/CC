package AnonGW;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class AnonGW {

    public static void main(String args[]) throws UnknownHostException {

        /**
         * Exemplo Geral: anonGW target-server 10.3.3.1 remoteport 80 overlay-peers 10.1.1.2 10.4.4.2 10.4.4.3
         */
        if(args.length < 6){
            System.out.println("Número de argumentos inválidos!");
        }
        else {
            InetAddress remoteHost = InetAddress.getByName(args[1]);
            int remotePort = Integer.parseInt(args[3]);
            System.out.println("Começando server proxy para o remote " + remoteHost+":"+remotePort);

            InetAddress[] peers = new InetAddress[args.length-5];
            for(int i = 5; i < args.length; i++) {
                peers[i - 5] = InetAddress.getByName(args[i]);
            }
            System.out.println("List of all peers = " + Arrays.toString(peers));

            AnonGWWorker anonGWWorker = new AnonGWWorker(remoteHost,remotePort,peers);
            anonGWWorker.listen();
        }
    }
}