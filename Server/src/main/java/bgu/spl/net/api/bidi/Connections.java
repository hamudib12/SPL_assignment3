package bgu.spl.net.api.bidi;

import bgu.spl.net.messages.Message;
import bgu.spl.net.messages.NotificationMessage;
import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.ConnectionHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface Connections<T> {

    boolean send(int connectionId, T msg);
    boolean send(String username, T msg);

    void broadcast(T msg);

    void register(Client client);

    void connect(int connectionId, String username, ConnectionHandler<T> connectionHandler);

    void disconnect(int connectionId);
    boolean isUserNameAvailable(String username);
    boolean isUserNameRegistered(String username);
    boolean areUserNameAndPasswordAvailable(String username, String password);
    boolean isClientActive(int connectionId);
    boolean isClientActive(String username);
    boolean isClientRegisteredAndUsernameAndPasswordCorrect(String username, String password);
    void loginProcess(String username, String password, Short captcha, int connectionId, ConnectionHandler<T> connectionHandler);
    ConcurrentHashMap<String, Client> getClientsMap();
    ConcurrentHashMap<Integer, String> getConnectionIdMap();
    List<String> getMassagesList();
    List<String> getFilterList();
    String getTime(Message message);
}