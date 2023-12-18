package bgu.spl.net.messages;

public class LoginMessage implements Message{
    private final short opcode = 2;
    private String username;
    private String password;
    private Short captcha;

    public LoginMessage(String username, String password, String captcha) {
        this.username = username;
        this.password = password;
        this.captcha = Short.parseShort(captcha);

    }

    @Override
    public short getOpcode() {return opcode;}

    public String getUsername(){return username;}
    public String getPassword() {return password;}
    public Short getCaptcha() {return captcha;}


    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setCaptcha(Short captcha) {this.captcha = captcha;}
}