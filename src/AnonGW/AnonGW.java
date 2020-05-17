package AnonGW;

import java.util.Arrays;

public class AnonGW {

    public static void main(String args[]) {

        /**
         * Exemplo Geral: anonGW target-server 10.3.3.1 remoteport 80 overlay-peers 10.1.1.2 10.4.4.2 10.4.4.3
         */
        if(args.length < 6){
            System.out.println("Número de argumentos inválidos!");
        }
        else {
            String remoteHost = args[1];
            int remotePort = Integer.parseInt(args[3]);
            System.out.println("Começando server proxy para o remote " + remoteHost+":"+remotePort);

            int[] peers = new int[args.length-5];
            for(int i = 5; i < args.length; i++) {
                peers[i - 5] = Integer.parseInt(args[i]);
            }
            System.out.println(Arrays.toString(peers));

            /**
             * P1 (10.1.1.1) -> Atena (10.4.4.3) -> Serv1 (10.3.3.1)
             * anonGW target-server 10.3.3.1 remoteport 80 overlay-peers 10.4.4.3
             *
             * P1 (10.1.1.1) -> Atena (10.4.4.3) -> Hermes (10.4.4.1) -> Serv1 (10.3.3.1)
             * anonGW target-server 10.3.3.1 remoteport 80 overlay-peers 10.4.4.3 10.4.4.1
             */
            new Thread(new AnonGWWorker(remoteHost, remotePort, peers, 12345, 6666)).start();
            new Thread(new AnonGWWorker(remoteHost, remotePort, peers, 12346, 6667)).start();

        }
    }
}