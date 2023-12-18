package bgu.spl.net.api.bidi;

import bgu.spl.net.messages.*;
import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.ConnectionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T>{
    private AtomicInteger numRegisteredClient;
    private ConcurrentHashMap<String, Client> clientsMap;//<username,client>
    private ConcurrentHashMap<Integer, String> connectionIdMap;//<id,username>
    private List<String> massagesList;
    private List<String> filterList;
    private Object lock;
    private static ConnectionsImpl connections = new ConnectionsImpl();

    private ConnectionsImpl(){
        clientsMap = new ConcurrentHashMap<>();
        connectionIdMap = new ConcurrentHashMap<>();
        numRegisteredClient = new AtomicInteger(0); // todo; this should be implemented when client connect to the server
        massagesList = new LinkedList<>();
        filterList = new LinkedList<>();
        filterList.add("fuck");
        filterList.add("face");
        filterList.add("faggot");

        lock = new Object();
    }


    public static ConnectionsImpl getInstance(){
        if(connections == null)
            connections = new ConnectionsImpl();
        return connections;
    }

    public boolean send(int connectionId, T msg){
        String username = connectionIdMap.get(connectionId);
        if(username != null){
            ConnectionHandler CH = clientsMap.get(username).getConnectionHandler();
            if(CH != null){
                CH.send(msg);
                return true;
            }
        }
        return false;// client isn't logged in
    }
    public boolean send(String username, T msg){
        ConnectionHandler CH = clientsMap.get(username).getConnectionHandler();
        if(CH != null){
            CH.send(msg);
            return true;
        }
        return false;// client isn't logged in
    }

    public void broadcast(T msg){
        for (Integer connId : connectionIdMap.keySet()){
            send(connId,msg);
        }
    }
    public void register(Client client){
        clientsMap.put(client.getUsername(), client);
    }
    public void disconnect(int connectionId) {///////////
        String username = connectionIdMap.get(connectionId);
        connectionIdMap.remove(connectionId);
        clientsMap.get(username).setConnectionHandler(null);
    }
    public void connect(int connectionId, String username, ConnectionHandler<T> connectionHandler){
        connectionIdMap.put(connectionId, username);
        clientsMap.get(username).setConnectionHandler(connectionHandler);
    }
    public boolean isUserNameRegistered(String username){
        return clientsMap.keySet().contains(username);
    }
    public boolean isUserNameAvailable(String username){
        return !clientsMap.keySet().contains(username);

    }
    public boolean isPasswordAvailable(String password){
        boolean ans = true;
        for (Client client : clientsMap.values()){
            if(client.getPassword().equals(password)){
                ans = false;
                break;
            }
        }
        return ans;
    }

    public boolean areUserNameAndPasswordAvailable(String username, String password){
        return isUserNameAvailable(username) && isPasswordAvailable(password);
    }
    public boolean isClientActive(int connectionId){
        return connectionIdMap.get(connectionId) != null;

    }
    public boolean isClientActive(String username){
        Client client = clientsMap.get(username);
        if(client != null){
            return client.getConnectionHandler() != null;
        }
        return false;
    }
    public boolean isClientRegisteredAndUsernameAndPasswordCorrect( String username, String password){
        Client client = clientsMap.get(username);
        if(client !=null){
            return client.getUsername().equals(username) && client.getPassword().equals(password);
        }
        else {
            return false;
        }
    }

    public void loginProcess(String username, String password, Short captcha, int connectionId, ConnectionHandler<T> connectionHandler){
        boolean successfulConnect ;
        synchronized (lock){
            if(captcha == 1 && !isClientActive(connectionId) && !isClientActive(username)
                    && isClientRegisteredAndUsernameAndPasswordCorrect(username, password)){
                connect(connectionId,username,connectionHandler);
                successfulConnect = true;
            }
            else {
                successfulConnect = false;
            }
        }
        if(successfulConnect){
            String myNameIs = connectionIdMap.get(connectionId);
            List<NotificationMessage> notList = clientsMap.get(myNameIs).getNotificationList();
            for(NotificationMessage notificationMessage: notList){
                send(username, (T)notificationMessage);
            }
            connectionHandler.send((T)new AckMessage((short) 2));// send Ack
        }
        else {
            connectionHandler.send((T)new ErrorMessage((short) 2));// send error
        }
    }



    public List<String> getFilterList() {
        return filterList;
    }
    public String getTime(Message message){
        Date date = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("DD-MM-YYYY HH:MM");
        String str = timeFormat.format(date);
        ((PMMessage)message).setDate(str);
        return str;
    }
    public int getNumRegisteredClient(){
        return numRegisteredClient.getAndIncrement();
    }
    public List<String> getMassagesList() {
        return massagesList;
    }
    public ConcurrentHashMap<Integer, String> getConnectionIdMap() {
        return connectionIdMap;
    }
    public ConcurrentHashMap<String, Client> getClientsMap() {
        return clientsMap;
    }
}
