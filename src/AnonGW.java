import java.util.Arrays;

public class AnonGW {

    public static void main(String args[]) {

        /**
         * Exemplo: anonGW target-server 10.3.3.1 remoteport 80 overlay-peers 10.1.1.2 10.4.4.2 10.4.4.3
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

            for(int i = 0; i < 2; i++) {

                AnonGWWorker anonGWWorker = new AnonGWWorker(remoteHost, remotePort, peers, 12345+i, 6666+i);
                new Thread(anonGWWorker).start();
            }
        }
    }
}