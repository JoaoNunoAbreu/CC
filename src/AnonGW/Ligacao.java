package AnonGW;

import java.net.InetAddress;
import java.util.Objects;

public class Ligacao {
    private InetAddress iphost;
    private InetAddress iptarget;

    public Ligacao(InetAddress iphost, InetAddress iptarget){
        this.iphost = iphost;
        this.iptarget = iptarget;
    }

    public InetAddress getIPHost() {
        return iphost;
    }

    public InetAddress getIPTarget() {
        return iptarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ligacao ligacao = (Ligacao) o;
        return iphost.getHostName().equals(ligacao.getIPHost().getHostName()) && iptarget.getHostName().equals(ligacao.getIPTarget().getHostName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(iphost, iptarget);
    }
}
