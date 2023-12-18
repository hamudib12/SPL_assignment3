package bgu.spl.net.messages;

import java.util.List;

public class AckMessage implements Message{
    private final short opcode = 10;
    private short messageOpcode ;
    ////For Follow message:////
    private String username = null;
    ////For stat/logStat message:////
    private String age = null;
    private String numPosts= null;
    private String numFollowers = null;
    private String numFollowing = null;

    public AckMessage(){}
    public AckMessage(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }


    public AckMessage(short messageOpcode, String username){
        this.messageOpcode = messageOpcode;
        this.username= username;
    }
    public AckMessage(short messageOpcode, String age, String numPosts, String numFollowers, String numFollowing){
        this.messageOpcode = messageOpcode;
        this.age = age;
        this.numPosts = numPosts;
        this.numFollowers = numFollowers;
        this.numFollowing = numFollowing;
    }


    @Override
    public short getOpcode() {return opcode;}

    public short getMessageOpcode() {return messageOpcode;}

    public String getUsername() {return username;}

    public String getAge() {return age;}

    public String getNumPosts() {return numPosts;}

    public String getNumFollowers() {return numFollowers;}


    public String getNumFollowing() {return numFollowing;}

    public void setAge(String age) {
        this.age = age;
    }

    public void setMessageOpcode(short messageOpcode) {
        this.messageOpcode = messageOpcode;
    }

    public void setNumFollowers(String numFollowers) {
        this.numFollowers = numFollowers;
    }

    public void setNumFollowing(String numFollowing) {
        this.numFollowing = numFollowing;
    }

    public void setNumPosts(String numPosts) {
        this.numPosts = numPosts;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}