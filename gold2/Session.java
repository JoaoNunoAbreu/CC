import java.net.InetAddress;
import java.util.Objects;

public class Session {
    private InetAddress host;
    private InetAddress target;
    private InetAddress randomPeer;

    public Session(InetAddress host, InetAddress target){
        this.host = host;
        this.target = target;
    }

    public Session(InetAddress host, InetAddress target, InetAddress randomPeer){
        this.host = host;
        this.target = target;
        this.randomPeer = randomPeer;
    }

    public InetAddress getHost() {
        return host;
    }

    public InetAddress getTarget() {
        return target;
    }

    public InetAddress getRandomPeer(){
        return randomPeer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return host.getHostName().equals(session.getHost().getHostName()) &&
                target.getHostName().equals(session.getTarget().getHostName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, target);
    }
}
