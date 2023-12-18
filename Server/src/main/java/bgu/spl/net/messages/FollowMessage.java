package bgu.spl.net.messages;

public class FollowMessage implements Message{
    private final short opcode = 4;
    private short follow = -1;
    private String username;

    public FollowMessage(short follow, String username) {
        this.follow = follow;
        this.username = username;
    }

    @Override
    public short getOpcode() {return opcode;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public short getFollow() {return follow;}
    public void setFollow(short a) {this.follow = a;}
}