package Udp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PDU implements Comparable<PDU>{

    private int seqNumber;              /* Para sabermos a ordem de pacotes         */
    private int isResposta;             /* Para sabermos se é uma resposta          */
    private int isLast;                 /* Para sabermos se é o último pacote       */
    private String target_response;     /* Para sabermos a quem temos de responder  */
    private byte[] fileData;            /* Para sabermos o conteúdo do pacote       */

    /* ---------------------------------- Construtores ---------------------------------- */

    public PDU() {
        this.seqNumber = 0;
        this.isResposta = 0;
        this.target_response = "";
        this.isLast = 0;
        this.fileData = new byte[2048];
    }

    public PDU(byte[] data, int size) {
        this.seqNumber = 0;
        this.isResposta = 0;
        this.target_response = "";
        this.isLast = 0;
        this.fileData = new byte[size];
        this.fileData = Arrays.copyOf(data, size);
    }

    public PDU(PDU pdu){
        this.seqNumber = pdu.getSeqNumber();
        this.isResposta = pdu.getIsResposta();
        this.isLast = pdu.getIsLast();
        this.target_response = pdu.getTarget_response();
        this.fileData = pdu.getFileData().clone();
    }

    /* ---------------------------------- Get's ---------------------------------- */

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getIsResposta() { return isResposta; }

    public String getTarget_response() { return target_response; }

    public int getIsLast() { return isLast; }

    public byte[] getFileData() { return Arrays.copyOfRange(this.fileData,0,this.fileData.length); }

    /* ---------------------------------- Set's ---------------------------------- */

    public void setSeqNumber(int seqNumber) { this.seqNumber = seqNumber; }

    public void setIsResposta(int isResposta) { this.isResposta = isResposta; }

    public void setTarget_response(String target_response) { this.target_response = target_response; }

    public void setIsLast(int isLast) { this.isLast = isLast; }

    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    /* -------------------------------------------------------------------------- */

    public byte[] toBytes() {
        byte[] utf8_host = this.target_response.getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(20 + this.fileData.length);
        packet.put(ByteBuffer.allocate(4).putInt(this.seqNumber).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.isResposta).array());
        packet.put(ByteBuffer.allocate(4).putInt(this.isLast).array());
        packet.put(utf8_host); // sempre 8 bytes
        packet.put(this.fileData);

        return packet.array();
    }

    public void fromBytes(byte[] pdu, int length){
        seqNumber = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 0, 4)).getInt();
        isResposta = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 4, 8)).getInt();
        isLast = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 8, 12)).getInt();
        target_response = new String(ByteBuffer.wrap(Arrays.copyOfRange(pdu, 12,  20)).array());
        fileData = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 20, length)).array();
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