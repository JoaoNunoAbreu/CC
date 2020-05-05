public class AnonGW {

    public static void main(String args[]) {

        if(args.length != 3){
            System.out.println("Número de argumentos inválidos!");
        }
        else {
            TcpIpProxy tcpIpProxy = new TcpIpProxy(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            tcpIpProxy.listen();
        }
    }
}