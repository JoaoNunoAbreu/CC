import java.nio.ByteBuffer;
import java.util.Arrays;

public class PDU {

    //private int inc = 0;
    private byte version;
    private byte security;
    private byte tipo;
    private byte op1;
    private byte op2;
    private byte op3;
    private byte op4;
    private byte[] sizebytes;
    private byte[] data;

    public byte[] getSizebytes() {
        return sizebytes;
    }

    public int getTotal(){
        Byte b  = this.op4;
        return b.intValue();
    }

    public int getNUM(){
        Byte b  = this.op3;
        return b.intValue();
    }
    public void setSizebytes(byte[] sizebytes) {
        this.sizebytes = sizebytes;
    }

    public static final byte REGISTER =0x00;
    public static final byte CONSULT_REQUEST =0x01;
    public static final byte CONSULT_RESPONSE =0x02;
    public static final byte PROBE_REQUEST =0x03;
    public static final byte PROBE_RESPONSE =0x04;
    public static final byte REQUEST =0x05;
    public static final byte DATA =0x06;

    //Nossos
    public static final byte ARE_YOU_THERE =0x07;
    public static final byte I_AM_HERE =0x08;
    public static final byte CONFIRM =0x09;
    public static final byte ACK =0x0A;
    public static final byte REGISTER_RESPONSE =0x0B;
    public static final byte SREJ=0x0C;

    public static final int MAXDATASIZE= (48*1024)-(7+4);

    public int getTotalSize(){
        int dataSize = PDU.intfromByte(this.getSizebytes());
        return dataSize+4+7;
    }

    static public byte[] toBytes(PDU pdu){
        byte[] obj=new byte[pdu.getTotalSize()];
        int objpos=0;
        obj[objpos++]=pdu.getVersion();
        obj[objpos++]=pdu.getSecurity();
        obj[objpos++]=pdu.getTipo();
        byte[] tmp = pdu.getOptions();
        for (int i = 0; i < tmp.length; i++) { //4
            obj[objpos++]=tmp[i];
        }

        tmp=pdu.getSizebytes();
        for(int i =0;i<4;i++){
            obj[objpos++]=tmp[i];
        }

        // calcular tamanho do  dados a partir do byte[]
        int tamanho  = intfromByte(pdu.getSizebytes());
        tmp=pdu.getData();
        for (int i = 0; i < tamanho; i++) { //49145
            obj[objpos++]=tmp[i];
        }
        return obj;
    }

    static public int intfromByte(byte[] sizebytes){
        ByteBuffer wrapped = ByteBuffer.wrap(sizebytes);
        return wrapped.getInt();
    }

    static public byte[] bytefromInt(int integer){
        ByteBuffer dbuf = ByteBuffer.allocate(4);
        dbuf.putInt(integer);
        return  dbuf.array();
    }

    static public PDU fromBytes(byte[] data){
        int objpos = 0;
        byte ver = data[objpos++];
        byte sec = data[objpos++];
        byte tip = data[objpos++];
        //obj[1]=pdu.getSecurity();
        //obj[2]=pdu.getTipo();
        //byte[] tmp = pdu.getOptions();
        byte[] ops = new byte[4];

        for (int i = 0; i < 4; i++) { //4
            ops[i]=data[objpos++];
        }

        byte[] sizebytes = new byte[4];
        for(int i =0 ; i<4;i++){
            sizebytes[i]=data[objpos++];
        }
        // apartirn do byte[ calcular  o tamanho
        int tamanho  = intfromByte(sizebytes);
        byte[] datai = new byte[tamanho];
        for (int i = 0; i < tamanho; i++) {
            datai[i]=data[objpos++];
        }
        PDU p = new PDU(ver, sec, tip, ops[0], ops[1], ops[2], ops[3], datai);
        return p;
    }



    public PDU (byte version, byte security,byte tipo, byte op1, byte op2, byte op3, byte op4, byte[] data){
        this.version=version;
        this.security=security;
        this.tipo=tipo;
        this.op1=op1;
        this.op2=op2; // tamanho dados
        this.op3=op3; // numeracao atual
        this.op4=op4; // numeraçao total
        int tamanhodados = data.length;
        this.sizebytes = bytefromInt(tamanhodados);
        this.data = new byte[tamanhodados];
        for (int i = 0;  i< tamanhodados; i++) {
            this.data[i]=data[i];
        }
    }


    public byte[] getOptions() {
        byte[] ops = new byte[4];
        ops[0]=this.op1;
        ops[1]=this.op2;
        ops[2]=this.op3;
        ops[3]=this.op4;
        return ops;
    }
    public void setOptions(byte[] ops) {
        this.op1=ops[0];
        this.op2=ops[1];
        this.op3=ops[2];
        this.op4=ops[3];
    }

    public byte getVersion() {
        return version;
    }



    public void setVersion(byte version) {
        this.version = version;
    }



    public byte getSecurity() {
        return security;
    }



    public void setSecurity(byte security) {
        this.security = security;
    }



    public byte getTipo() {
        return tipo;
    }



    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + op1;
        result = prime * result + op2;
        result = prime * result + op3;
        result = prime * result + op4;
        result = prime * result + security;
        result = prime * result + Arrays.hashCode(sizebytes);
        result = prime * result + tipo;
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PDU other = (PDU) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        if (op1 != other.op1)
            return false;
        if (op2 != other.op2)
            return false;
        if (op3 != other.op3)
            return false;
        if (op4 != other.op4)
            return false;
        if (security != other.security)
            return false;
        if (!Arrays.equals(sizebytes, other.sizebytes))
            return false;
        if (tipo != other.tipo)
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    public byte[] getData() {
        return data;
    }



    public void setData(byte[] data) {
        this.data = data;
    }


    @Override
    public String toString() {
        Byte num=op3;
        Byte tot=op4;
        return "AA->PDU [version=" + version + ", security=" + security + ", tipo=" + tipo + ", op1=" + op1 + ", op2=" + op2
                + ", op3=" + num.intValue()+ ", op4=" + tot.intValue() + ", sizebytes=" + Arrays.toString(sizebytes) + ", data="
                + Arrays.toString(data) + "]";
    }



}