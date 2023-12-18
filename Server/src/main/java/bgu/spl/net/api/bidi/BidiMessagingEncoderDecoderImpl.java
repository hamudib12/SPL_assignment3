package bgu.spl.net.api.bidi;

import bgu.spl.net.messages.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class BidiMessagingEncoderDecoderImpl implements BidiMessagingEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    Message msg = null;
    private boolean lineHasEnded = false;

    // String result = new String(bytes, StandardCharsets.UTF_8);
//------------------------------------DECODE----------------------------------------------------------------
    public Message decodeNextByte(byte nextByte) {
        if(len != 0 && nextByte == '\0'){
            nextByte = ' ';
        }
        if(nextByte == ';'){
            lineHasEnded = true;
        }
        if (lineHasEnded){
            String c = popString();
            String[] line = c.split(" ");
            buildMsg(line);
            if(msg != null){
                return popMessage();
            }
        }
        if(nextByte != ';'){
            pushByte(nextByte);
        }
        len++;
        return null;
    }
//---------------------------------buildMSG-----------------------------------
    private void buildMsg(String[] line) {
        try{
        switch (Short.parseShort(line[0])){
            case 1:
                buildRegisterMsg(line);
                break;
            case 2:
                buildLoginMsg(line);
                break;
            case 3:
                buildLogOutMsg(line);
                break;
            case 4:
                buildFollowMsg(line);
                break;
            case 5:
                buildPostMsg(line);
                break;
            case 6:
                buildPMMsg(line);
                break;
            case 7:
                buildLogStatMsg(line);
                break;
            case 8:
                buildStatMsg(line);
                break;
            case 12:
                buildBlockMsg(line);
                break;
        }
        }
        catch (Exception e){
            System.out.println("none opcode");
        }

    }

    private void buildBlockMsg(String[] line) {
        msg = new BlockMessage(line[1]);
    }
    private void buildStatMsg(String[] line) {
        List<String> userNameList = new LinkedList<>();
        String str = "";
        for (int i = 0 ; i < line[1].length() ; i++){
            if (line[1].charAt(i) != '|'){
                str += line[1].charAt(i);
            }
            else{
                userNameList.add(str);
                str = "";
            }
        }
        userNameList.add(str);
        msg = new StatMessage(userNameList);
    }

    private void buildLogStatMsg(String[] line) {
        msg = new LogStatMessage();
    }

    private void buildPMMsg(String[] line) {
            String content = "";
            for(int i = 2 ; i < line.length - 1 ; i++){
                content += line[i] + " ";
            }
            content += line[line.length - 1];
            msg = new PMMessage(line[1],content);

    }

    private void buildPostMsg(String[] line) {
        String str = "";
        for(int i = 1 ; i < line.length - 1 ; i++){
            str += line[i] + " ";
        }
        str += line[line.length - 1];
        msg = new PostMessage(str);

    }

    private void buildFollowMsg(String[] line) {
        if(line.length == 3) {
            if(line[1].equals("0")){
                msg = new FollowMessage((short) 0, line[2]);
            }
            else if(line[1].equals("1")){
                msg = new FollowMessage((short) 1, line[2]);
            }
        }
        else{
            msg = new IllegalMessage((short) 5);
        }
    }

    private void buildLogOutMsg(String[] line) {
            msg = new LogoutMessage();
    }

    private void buildLoginMsg(String[] line) {
        if(line.length == 4) {
            msg = new LoginMessage(line[1], line[2], line[3]);
        }
        else{
            msg = new IllegalMessage((short) 2);
        }
    }



    private void buildRegisterMsg(String [] line) {
        if(line.length == 4) {
            msg = new RegisterMessage(line[1], line[2], line[3]);
        }
        else{
            msg = new IllegalMessage((short) 1);
        }
    }

//-------------------------endOfDecode-------------------------------


//-------------------------Encode------------------------------------
    public byte[] encode(Message message){
        ArrayList<Byte> arrayList = new ArrayList<>(); // filling arrayList to know bytes array size.
        short opCode = message.getOpcode(); // getting the opCode for what server send(9,10,11).
        byte[] bytes = shortToBytes(opCode);
        String nullTerm = "\0";
        addingBytes(arrayList,bytes); // adding the bytes of the opCode.
        if(opCode == 9){ // "NOIFICATION"
            short type = ((NotificationMessage)message).getType(); // "messageType.
            byte[]bytes1 = shortToBytes(type);
            if(type == 0) {
                addingBytes(arrayList, bytes1); // sending messageBytes
                addingBytes(arrayList, nullTerm.getBytes());
                String postingUser = ((NotificationMessage) message).getPostingUser() + "\0";
                byte[] bytes3 = postingUser.getBytes();
                addingBytes(arrayList, bytes3);
                String content = ((NotificationMessage) message).getContent() + " " + ((NotificationMessage) message).getTime() + ";";
                byte[] bytes2 = content.getBytes();
                addingBytes(arrayList, bytes2);
            }
            if(type == 1){
                addingBytes(arrayList, bytes1); // sending messageBytes
                addingBytes(arrayList, nullTerm.getBytes());
                String postingUser = ((NotificationMessage) message).getPostingUser() + "\0";
                byte[] bytes3 = postingUser.getBytes();
                addingBytes(arrayList, bytes3);
                String content = ((NotificationMessage) message).getContent() + ";";
                byte[] bytes2 = content.getBytes();
                addingBytes(arrayList, bytes2);
            }

        }
        if(opCode == 10){
            short messageOpcode = ((AckMessage)message).getMessageOpcode();
            byte[] messageOpCodeByte = shortToBytes(messageOpcode); // sending the message opCode.
            addingBytes(arrayList,messageOpCodeByte);
            if (messageOpcode == 4){ // follow
                addingBytes(arrayList, nullTerm.getBytes());
                String username = ((AckMessage)message).getUsername() + ";";
                byte[] bytes1 = username.getBytes();
                addingBytes(arrayList , bytes1);
            }
            else if (messageOpcode == 7 || messageOpcode == 8){ // logStat , Stat
                addingBytes(arrayList, nullTerm.getBytes());
                String age = ((AckMessage)message).getAge() + "\0";
                byte[] ageBytes = age.getBytes();
                addingBytes(arrayList , ageBytes);
                String numOfPosts = ((AckMessage)message).getNumPosts() +"\0";
                byte[] PostsBytes = numOfPosts.getBytes();
                addingBytes(arrayList , PostsBytes);
                String numOfFollowers = ((AckMessage)message).getNumFollowers() + "\0";
                byte[] numOfFollowersBytes = numOfFollowers.getBytes();
                addingBytes(arrayList , numOfFollowersBytes);
                String numFollowing = ((AckMessage)message).getNumFollowing() + ";";
                byte[] numFollowingBytes = numFollowing.getBytes();
                addingBytes(arrayList,numFollowingBytes);
            }
            else{
                 byte[] semicolon = (";").getBytes(); // adding semicolon for the ending of the line
                 addingBytes(arrayList,semicolon);
            }
        }
        if (opCode == 11){
            short messageOpcode = ((ErrorMessage)message).getMessageOpcode();
            byte[]bytes1 = shortToBytes(messageOpcode);
            addingBytes(arrayList,bytes1);
            byte[] semicolon = (";").getBytes(); // adding semicolon for the ending of the line
            addingBytes(arrayList,semicolon);
        }
        /*byte[] semicolon = (";").getBytes(); // adding semicolon for the ending of the line
        addingBytes(arrayList,semicolon);*/
        byte[] Encode = new byte[arrayList.size()];
        for(int i = 0 ; i < Encode.length ; i++){ // making bytes array.
            Encode[i] = arrayList.get(i);
        }
        return Encode;
    }

//------------------pushBytes-------------------------------------
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len] = nextByte;
    }
//-----------------popMessage--------------------------------------
    private Message popMessage() {
        bytes = new byte[ 1 << 10];
        len = 0;
        return msg;
    }
    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    public void addingBytes(ArrayList<Byte> arrayList , byte[] bytes){
        for(int i = 0 ; i < bytes.length ; i++){
            arrayList.add(bytes[i]);
        }
    }
    private String popString(){
        String result = "";
        if(bytes[1] != (short) 12) {
            bytes[0] += 48;
            bytes[1] += 48;
            result = new String(bytes,0,len,StandardCharsets.UTF_8);
        }
        else{
            String str = "12";
            result = new String(bytes,2,len - 2,StandardCharsets.UTF_8);
            str += result;
            len = 0;
            lineHasEnded = false;
            return str;
        }

        len = 0;
        lineHasEnded = false;
        return result;
    }

}