package bgu.spl.net.messages;

public class LogoutMessage implements Message{
    private final short opcode = 3;
    public LogoutMessage(){}
    @Override
    public short getOpcode() {return opcode;}
}
