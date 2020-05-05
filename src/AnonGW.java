public class AnonGW {

    public static void main(String args[]) {

        if(args.length != 3){
            System.out.println("Número de argumentos inválidos!");
        }
        else {
            String remoteHost = args[0];
            Integer remotePort = Integer.parseInt(args[1]);
            Integer port = Integer.parseInt(args[2]);
            System.out.println("Começando server proxy na porta " + port + " para o remote " + remoteHost+":"+remotePort);
            TcpIpProxy tcpIpProxy = new TcpIpProxy(remoteHost, remotePort, port);
            tcpIpProxy.listen();
        }
    }
}