package bgu.spl.net.messages;

public class NotificationMessage implements Message{
    private final short opcode = 9;
    private short type = -1;
    private String postingUser= null;
    private String content= null;
    private String time = null;
    public NotificationMessage(){
    }
    public NotificationMessage(short type, String postingUser, String content){//post1
        this.type = type;
        this.postingUser = postingUser;
        this.content = content;
        this.time = null;
    }
    public NotificationMessage(short type, String postingUser, String content, String time){// pm0
        this.type = type;
        this.postingUser = postingUser;
        this.content = content;
        this.time = time;
    }

    @Override
    public short getOpcode() {return opcode;}
    public short getType() {return type;}
    public String getContent() {return content;}
    public String getPostingUser() {return postingUser;}
    public String getTime() {return time;}

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPostingUser(String postingUser) {
        this.postingUser = postingUser;
    }

    public void setType(short type) {
        this.type = type;
    }
}