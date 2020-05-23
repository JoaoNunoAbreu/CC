JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		AnonGW/AnonGW.java \
		AnonGW/AnonGWWorker.java \
		AnonGW/Ligacao.java \
		Encryption/AESencrp \
		Tcp/TcpProxy.java \
		Tcp/TcpReceiver.java \
		Udp/PDU.java \
		Udp/UdpProxy.java \
		Udp/UdpReceiver.java
		
default: classes

classes: $(CLASSES:.java=.class)

clean:
		rm */*.class