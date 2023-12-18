package bgu.spl.net.api.bidi;

import bgu.spl.net.messages.*;
import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.*;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private Connections<Message> connections;
    private int connectionId;
    private ConnectionHandler<Message> connectionHandler;
    boolean shouldTerminate;
    public void start(int connectionId, Connections<Message> connections, ConnectionHandler<Message> connectionHandler){
        this.connections = connections;
        this.connectionId = connectionId;
        this.connectionHandler = connectionHandler;
        shouldTerminate = false;
    }

    public void process(Message message){

        short opcode = message.getOpcode();
        String userName;
        String password;
        String birthday;
        String content;
        short captcha, follow;

        if(opcode == 1){ //REGISTER
            userName = ((RegisterMessage)message).getUsername();
            password = ((RegisterMessage)message).getPassword();
            birthday = ((RegisterMessage)message).getBirthday();

            if (connections.areUserNameAndPasswordAvailable(userName, password)){
                connections.register(new Client(userName, password, birthday, null));
                connectionHandler.send(new AckMessage((short) 1));// send Ack
            }
            else {
                connectionHandler.send(new ErrorMessage((short) 1));// send error
            }
        }
        else if (opcode == 2){// LOGIN
            userName = ((LoginMessage)message).getUsername();
            password = ((LoginMessage)message).getPassword();
            captcha = ((LoginMessage)message).getCaptcha();
            connections.loginProcess(userName, password, captcha, connectionId, connectionHandler);

        }
        else if(opcode == 3){// LOGOUT
            if(connections.isClientActive(connectionId)){
                connectionHandler.send(new AckMessage((short) 3));// send Ack
                connections.disconnect(connectionId);
                // todo: does the client receive ack massage before he got terminated
                shouldTerminate = true;
            }
            else {
                connectionHandler.send(new ErrorMessage((short) 3));// send error
            }
        }
        else if(opcode == 4){// FOLLOW
            follow = ((FollowMessage)message).getFollow();
            userName = ((FollowMessage)message).getUsername();

            if(connections.isClientActive(connectionId) && connections.isUserNameRegistered(userName)){
                String myNameIs = connections.getConnectionIdMap().get(connectionId);
                List<String> myFollowingList = connections.getClientsMap().get(myNameIs).getFollowing();
                List<String> usernameFollowerList =connections.getClientsMap().get(userName).getFollowers();
                List<String> usernameBlockList = connections.getClientsMap().get(userName).getBlockList();

                //todo : shallow ot copy?
                if(follow == 0){// Follow
                    if(!myFollowingList.contains(userName) && !usernameBlockList.contains(myNameIs)){
                        myFollowingList.add(userName);
                        usernameFollowerList.add(myNameIs);
                        connectionHandler.send(new AckMessage((short) 4,userName));// send Ack
                    }
                    else {
                        connectionHandler.send(new ErrorMessage((short) 4));// send error
                    }
                }
                else {// UnFollow
                    if(myFollowingList.contains(userName) && !usernameBlockList.contains(myNameIs)){
                        myFollowingList.remove(userName);
                        usernameFollowerList.remove(myNameIs);
                        connectionHandler.send(new AckMessage((short) 4,userName));// send Ack
                    }
                    else {
                        connectionHandler.send(new ErrorMessage((short) 4));// send error
                    }
                }
            }
            else {
                connectionHandler.send(new ErrorMessage((short) 4));// send error
            }
        }
        else if (opcode==5){// POST
            content = ((PostMessage)message).getContent();
            if(connections.isClientActive(connectionId)){
                connections.getMassagesList().add(content);
                Set<String> sendMsgTo = setOfUsernameInContent(content);
                String myNameIs = connections.getConnectionIdMap().get(connectionId);
                for (String user : connections.getClientsMap().get(myNameIs).getFollowers()) {
                    sendMsgTo.add(user);
                }
                /*sendMsgTo.addAll(connections.getClientsMap().get(myNameIs).getFollowing());*/
                for(String user: sendMsgTo){
                    if (!connections.getClientsMap().get(user).getBlockList().contains(myNameIs)){
                        if (connections.isClientActive(user) ){
                            if(!connections.send(user, new NotificationMessage((short) 1, myNameIs, content))){
                                connectionHandler.send(new ErrorMessage((short) 5));// send error

                            }

                        }
                        else {
                            connections.getClientsMap().get(user).getNotificationList()
                                    .add(new NotificationMessage((short) 1, myNameIs, content));
                        }
                    }
                    else {
                        connectionHandler.send(new ErrorMessage((short) 5));// send error
                    }

                }
                connections.getClientsMap().get(connections.getConnectionIdMap().get(connectionId)).incrementNumberOfPosts();// increase number of posts
                connectionHandler.send(new AckMessage((short) 5));// send error
            }
            else {
                connectionHandler.send(new ErrorMessage((short) 5));// send error
            }
        }
        else if (opcode == 6) {// PM
            userName = ((PMMessage)message).getUsername();
            content = ((PMMessage)message).getContent();
            String myNameIs = connections.getConnectionIdMap().get(connectionId);
            if(connections.isClientActive(connectionId) && connections.isUserNameRegistered(userName)
                    && connections.getClientsMap().get(userName).getFollowers().contains(myNameIs)
                    && !connections.getClientsMap().get(userName).getBlockList().contains(myNameIs)){
                String filteredContent = filterMessage(content, connections.getFilterList());
                if (connections.isClientActive(userName)){
                    connections.send(userName, new NotificationMessage((short) 0, myNameIs,
                            filteredContent , connections.getTime(message)));
                }
                else {
                    connections.getClientsMap().get(userName).getNotificationList()
                            .add(new NotificationMessage((short) 0, myNameIs,
                                    filteredContent , connections.getTime(message)));
                }
                connectionHandler.send(new AckMessage((short) 6));// send error
            }
            else {
                connectionHandler.send(new ErrorMessage((short) 6));// send error
            }

        }
        else if(opcode == 7){// LOGSTAT
            if(connections.isClientActive(connectionId)){
                String myNameIs = connections.getConnectionIdMap().get(connectionId);
                for(Client client: connections.getClientsMap().values()){
                    if (!client.getBlockList().contains(myNameIs)){
                        connectionHandler.send(new AckMessage((short) 7, Integer.toString(client.getAge())
                                ,Integer.toString(client.getNumberOfPosts()),
                                Integer.toString(client.getFollowers().size()),
                                Integer.toString(client.getFollowing().size())));// send Ack
                    }
                }
            }

            else {
                connectionHandler.send(new ErrorMessage((short) 7));// send error
            }

        }
        else if(opcode == 8){// STAT
            List<String> listOfUsernames = ((StatMessage)message).getUsersList();
            if(connections.isClientActive(connectionId)){
                String myNameIs = connections.getConnectionIdMap().get(connectionId);
                Client clientMe= connections.getClientsMap().get(myNameIs);
                for(String username: listOfUsernames){
                    if(connections.isUserNameRegistered(username)){
                        Client client= connections.getClientsMap().get(username);
                        if (!client.getBlockList().contains(myNameIs) && !clientMe.getBlockList().contains(myNameIs)){
                            connectionHandler.send(new AckMessage((short) 8, Integer.toString(client.getAge())
                                    ,Integer.toString(client.getNumberOfPosts()),
                                    Integer.toString(client.getFollowers().size()),
                                    Integer.toString(client.getFollowing().size())));// send Ack
                        }
                        else {
                            connectionHandler.send(new ErrorMessage((short) 8));
                        }
                    }
                    else {
                        connectionHandler.send(new ErrorMessage((short) 8));
                    }

                }
            }
            else {
                connectionHandler.send(new ErrorMessage((short) 8));// send error
            }

        }
        else if(opcode == 9){//NOTIFICATION
            connectionHandler.send(message);
        }
        else if(opcode == 10){//ACK
            connectionHandler.send(message);
        }
        else if(opcode ==11){//ERROR
            connectionHandler.send(message);
        }
        else if(opcode == 12){// BLOCK
            userName = ((BlockMessage)message).getUsername();
            if(connections.isClientActive(connectionId) && connections.isUserNameRegistered(userName)){
                String myNameIs = connections.getConnectionIdMap().get(connectionId);
                Client theClientMe= connections.getClientsMap().get(myNameIs);
              /*  Client theClientMe = connections.getClientsMap().get(connections.getConnectionIdMap().get(connectionId));*/
                Client blockedUser = connections.getClientsMap().get(userName);
                theClientMe.getBlockList().add(userName);
                if(theClientMe.getFollowing().remove(userName)){
                    blockedUser.getFollowers().remove(theClientMe.getUsername());
                }
                if(theClientMe.getFollowers().remove(userName)){
                    blockedUser.getFollowing().remove(theClientMe.getUsername());
                }
                connectionHandler.send(new AckMessage((short) 12));

            }
            else {
                connectionHandler.send(new ErrorMessage((short) 12));// send error
            }

        }else if(opcode == -1){
            connectionHandler.send(new ErrorMessage(((IllegalMessage)message).getMessageOpcode()));// send error
        }

    }


    public boolean shouldTerminate(){
        return shouldTerminate;
    }
    private HashSet<String> setOfUsernameInContent(String content){
        HashSet<String> ans = new HashSet<>();
        String username;
        int i = 0;
        while (i< content.length() -1){
            int index;
            if(content.charAt(i) == '@'){
                index= content.indexOf(' ',i);
                if(index > -1){
                    username = content.substring(i+1, index);
                }
                else {
                    username = content.substring(i+1);
                }
                if(!connections.isUserNameAvailable(username)){
                    ans.add(username);
                }
            }
            i++;
        }
        return ans;

    }
    private String filterMessage(String msg,List<String> filterList){
        String filteredMsg = msg;
        for(String badWord: filterList){
            int index = filteredMsg.indexOf(badWord);
            while (index != -1){
                filteredMsg = filteredMsg.substring(0, index) + "<filtered>" + filteredMsg.substring(index + badWord.length());
                index = filteredMsg.indexOf(badWord);
            }
        }
        return filteredMsg;
    }

}
