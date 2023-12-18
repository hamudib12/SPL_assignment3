package bgu.spl.net.messages;

public class IllegalMessage implements Message{
    private short messageOpcode ;

    public IllegalMessage(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }
    @Override
    public short getOpcode() {
        return -1;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }
}
