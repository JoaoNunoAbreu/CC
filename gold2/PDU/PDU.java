package PDU;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PDU implements Comparable<PDU> {
    private int seq_Number;
    private int isLast;
    private int isAnswer;
    private String answerTo; // InetAdress
    private byte[] data;

    public PDU(){
        this.seq_Number = 0;
        this.isLast = 0;
        this.isAnswer = 0;
        this.answerTo = "";
        data = new byte[1448];
    }

    public PDU(byte[] data){
        this.seq_Number = 0;
        this.isLast = 0;
        this.isAnswer = 0;
        this.answerTo = "";
        this.data = data.clone();
    }

    public PDU(PDU pdu){
        this.seq_Number = pdu.getSeq_Number();
        this.isLast = pdu.isLast();
        this.isAnswer = pdu.isAnswer();
        this.answerTo = pdu.getAnswerTo();
        this.data = pdu.getData().clone();
    }

    public String getAnswerTo(){
        return answerTo;
    }

    public void setAnswerTo(String answerTo) {
        this.answerTo = answerTo;
    }

    public int isAnswer(){
        return isAnswer;
    }

    public void markAsAnswer() {
        this.isAnswer = 1;
    }

    public int getSeq_Number() {
        return seq_Number;
    }

    public void setSeq_Number(int seq_Number){
        this.seq_Number = seq_Number;
    }

    public int isLast() {
        return isLast;
    }

    public void setIsLast(int isLast){
        this.isLast = isLast;
    }

    public byte[] getData() {
        return data;
    }

    /* Transforms the PDU into a byte array ready to be sent to other AnonGW */
    public byte[] toBytes() {
        byte[] utf8_host = this.answerTo.getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(16 + this.answerTo.getBytes().length + this.data.length);
        packet.put(ByteBuffer.allocate(4).putInt(this.seq_Number).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.isLast).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.isAnswer).array());
        packet.put(ByteBuffer.allocate(4).putInt(utf8_host.length).array());
        packet.put(utf8_host);
        packet.put(this.data);

        return packet.array();
    }

    /* Obtains a PDU from a byte array that another AnonGW sent */
    public void fromBytes(byte[] pdu, int length){
        seq_Number = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 0, 4)).getInt();
        isLast = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 4, 8)).getInt();
        isAnswer = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 8, 12)).getInt();
        int answerToSize = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 12, 16)).getInt();
        answerTo = new String(ByteBuffer.wrap(Arrays.copyOfRange(pdu, 16, answerToSize + 16)).array());
        data = ByteBuffer.wrap(Arrays.copyOfRange(pdu, answerToSize + 16, length)).array();
    }

    public PDU clone(){
        return new PDU(this);
    }

    @Override
    public int compareTo(PDU o) {
        return this.getSeq_Number() - o.getSeq_Number();
    }
}
