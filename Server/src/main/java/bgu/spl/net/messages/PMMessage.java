package bgu.spl.net.messages;

public class PMMessage implements Message{
    private final short opcode = 6;
    private String username;
    private String content;
    private String date;

    public PMMessage(String username, String content) {
        this.username = username;
        this.content = content;
        this.date = null;
    }


    @Override
    public short getOpcode() {return opcode;}

    public String getContent() {return content;}
    public String getUsername() {return username;}

    public String getDate() {
        return date;
    }

    public void setContent(String content) {this.content = content;}
    public void setUsername(String username) {this.username = username;}

    public void setDate(String date) {
        this.date = date;
    }
}