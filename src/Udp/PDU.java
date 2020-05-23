package Udp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PDU implements Comparable<PDU>{

    private int seqNumber;
    private int isResposta;
    private int ultimo;
    private String target_response;
    private byte[] fileData;

    /* ---------------------------------- Construtores ---------------------------------- */

    public PDU() {
        this.seqNumber = 0;
        this.isResposta = 0;
        this.ultimo = 0;
        this.target_response = "";
        this.fileData = new byte[2048];
    }

    public PDU(byte[] data, int size) {
        this.seqNumber = 0;
        this.isResposta = 0;
        this.ultimo = 0;
        this.target_response = "";
        this.fileData = new byte[size];
        this.fileData = Arrays.copyOf(data, size);
    }

    public PDU(PDU pdu){
        this.seqNumber = pdu.getSeqNumber();
        this.ultimo = pdu.getUltimo();
        this.isResposta = pdu.getIsResposta();
        this.target_response = pdu.getTarget_response();
        this.fileData = pdu.getFileData().clone();
    }

    /* ---------------------------------- Get's ---------------------------------- */

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getIsResposta() { return isResposta; }

    public int getUltimo() { return ultimo; }

    public String getTarget_response() { return target_response; }

    public byte[] getFileData() {
        return this.fileData;
    }

    /* ---------------------------------- Set's ---------------------------------- */

    public void setSeqNumber(int seqNumber) { this.seqNumber = seqNumber; }

    public void setIsResposta(int isResposta) { this.isResposta = isResposta; }

    public void setUltimo(int ultimo) { this.ultimo = ultimo; }

    public void setTarget_response(String target_response) { this.target_response = target_response; }

    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    /* -------------------------------------------------------------------------- */

    public byte[] toBytes() {
        byte[] utf8_host = this.target_response.getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(16 + this.target_response.getBytes().length + this.fileData.length);
        packet.put(ByteBuffer.allocate(4).putInt(this.seqNumber).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.ultimo).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.isResposta).array());
        packet.put(ByteBuffer.allocate(4).putInt(utf8_host.length).array());
        packet.put(utf8_host);
        packet.put(this.fileData);

        return packet.array();
    }

    public void fromBytes(byte[] pdu, int length){
        seqNumber = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 0, 4)).getInt();
        ultimo = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 4, 8)).getInt();
        isResposta = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 8, 12)).getInt();
        int answerToSize = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 12, 16)).getInt();
        target_response = new String(ByteBuffer.wrap(Arrays.copyOfRange(pdu, 16, answerToSize + 16)).array());
        fileData = ByteBuffer.wrap(Arrays.copyOfRange(pdu, answerToSize + 16, length)).array();
    }

    @Override
    public int compareTo(PDU o) {
        return this.getSeqNumber() - o.getSeqNumber();
    }

    @Override
    public PDU clone(){
        return new PDU(this);
    }

}