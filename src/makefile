JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		AnonGW/AnonGW.java \
		AnonGW/AnonGWWorker.java \
		Client/Cliente.java \
		Server/Server.java \
		Server/ServerWorker.java \
		Tcp/TcpProxy.java \
		Tcp/TcpReceiver.java \
		Udp/PDU.java \
		Udp/UdpProxy.java \
		Udp/UdpReceiver.java
		
default: classes

classes: $(CLASSES:.java=.class)

clean:
		rm AnonGW/*.class
		rm Client/*.class
		rm Server/*.class
		rm Tcp/*.class
		rm Udp/*.class