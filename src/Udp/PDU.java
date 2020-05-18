package Udp;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PDU {

    private int seqNumber;
    private int ackNumber;
    private int flagType;
    private long checksum;
    private byte[] fileData;
    private int sizeData;

    public PDU(int size) {
        this.seqNumber = 0;
        this.ackNumber = 0;
        this.flagType = 0;
        this.checksum = 0;
        this.fileData = new byte[size];
        this.sizeData = size;
    }

    public PDU(byte[] data, int size) {
        this.seqNumber = 0;
        this.ackNumber = 0;
        this.flagType = 0;
        this.checksum = 0;
        this.fileData = new byte[size];
        this.fileData = Arrays.copyOf(data, size);
        this.sizeData = size;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    public int getFlagType() {
        return flagType;
    }

    public long getChecksum() {
        return checksum;
    }

    public byte[] getFileData() {
        return Arrays.copyOf(this.fileData, this.sizeData);
    }

    public int getSizeData(){
        return this.sizeData;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(this.getSizeData()+4+4+4+8);
        buffer.put(ByteBuffer.allocate(4).putInt(this.getSeqNumber()).array());
        buffer.put(ByteBuffer.allocate(4).putInt(this.getAckNumber()).array());
        buffer.put(ByteBuffer.allocate(4).putInt(this.getFlagType()).array());
        buffer.put(ByteBuffer.allocate(8).putLong(this.getChecksum()).array());
        buffer.put(ByteBuffer.allocate(this.getSizeData()).put(this.getFileData()).array());

        return buffer.array();
    }

    public void fromBytes(byte[] pdu, int length){
        seqNumber = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 0, 4)).getInt();
        ackNumber = (ByteBuffer.wrap(Arrays.copyOfRange(pdu, 4, 8)).getInt());
        flagType = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 8, 12)).getInt();
        checksum = ByteBuffer.wrap(Arrays.copyOfRange(pdu,12, 20)).getLong();
        fileData = ByteBuffer.wrap(Arrays.copyOfRange(pdu, 20, length)).array();
    }
}