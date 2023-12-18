package bgu.spl.net.messages;

import java.util.LinkedList;
import java.util.List;

public class StatMessage implements Message{
    private final short opcode = 8;
    private List<String> usersList = new LinkedList<String>();

    public StatMessage(List<String> usersList) {
        this.usersList = usersList;
    }

    public StatMessage(){}
    @Override
    public short getOpcode() {return opcode;}

    public List<String> getUsersList() { return usersList; }
    public void addUserToList(String user) { usersList.add(user); }



}