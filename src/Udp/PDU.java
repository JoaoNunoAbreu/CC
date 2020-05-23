package Udp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PDU implements Comparable<PDU>{

    private int seqNumber;
    private int isResposta;
    private String target_response;
    private byte[] fileData;

    /* ---------------------------------- Construtores ---------------------------------- */

    public PDU() {
        this.seqNumber = 0;
        this.isResposta = 0;
        this.target_response = "";
        this.fileData = new byte[2048];
    }

    public PDU(byte[] data, int size) {
        this.seqNumber = 0;
        this.isResposta = 0;
        this.target_response = "";
        this.fileData = new byte[size];
        this.fileData = Arrays.copyOf(data, size);
    }

    public PDU(PDU pdu){
        this.seqNumber = pdu.getSeqNumber();
        this.isResposta = pdu.getIsResposta();
        this.target_response = pdu.getTarget_response();
        this.fileData = pdu.getFileData().clone();
    }

    /* ---------------------------------- Get's ---------------------------------- */

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getIsResposta() { return isResposta; }

    public String getTarget_response() { return target_response; }

    public byte[] getFileData() {
        return Arrays.copyOfRange(this.fileData,0,this.fileData.length);
    }

    public int getSizeFileData(){
        return this.fileData.length;
    }

    /* ---------------------------------- Set's ---------------------------------- */

    public void setSeqNumber(int seqNumber) { this.seqNumber = seqNumber; }

    public void setIsResposta(int isResposta) { this.isResposta = isResposta; }

    public void setTarget_response(String target_response) { this.target_response = target_response; }

    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    /* -------------------------------------------------------------------------- */

    public byte[] toBytes() {
        byte[] utf8_host = this.target_response.getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(12 + this.target_response.getBytes().length + this.fileData.length);
        packet.put(ByteBuffer.allocate(4).putInt(this.seqNumber).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.isResposta).array());
        packet.put(ByteBuffer.allocate(4).putInt(utf8_host.length).array());
        packet.put(utf8_host);
        packet.put(this.fileData);

        return packet.array();
    }

    public void fromBytes(byte[] pdu, int length){
        seqNumber = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 0, 4)).getInt();
        isResposta = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 4, 8)).getInt();
        int answerToSize = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 8, 12)).getInt();
        target_response = new String(ByteBuffer.wrap(Arrays.copyOfRange(pdu, 12, answerToSize + 12)).array());
        fileData = ByteBuffer.wrap(Arrays.copyOfRange(pdu, answerToSize + 12, length)).array();
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