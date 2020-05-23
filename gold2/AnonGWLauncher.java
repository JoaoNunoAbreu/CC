import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class AnonGWLauncher {
    public static void main(String[] args) throws UnknownHostException {
        /* Parse cmd options */
        InetAddress target = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        InetAddress[] peers = new InetAddress[args.length - 2];
        for(int i = 2; i < args.length; i++) peers[i-2] = InetAddress.getByName(args[i]);

        /* Launch AnonGW */
        AnonGWRunnable anonGWRunnable = new AnonGWRunnable(port, 6666, peers, port, target);
        anonGWRunnable.run();
    }
}
