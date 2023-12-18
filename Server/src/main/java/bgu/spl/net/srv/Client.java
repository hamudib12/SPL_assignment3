package bgu.spl.net.srv;
import bgu.spl.net.api.bidi.BidiMessagingEncoderDecoder;

import bgu.spl.net.messages.Message;
import bgu.spl.net.messages.NotificationMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Client {
    private String username;
    private String password;
    private String birthday;
    private List<String> followers;
    private List<String> following;
    private int numberOfPosts;
    private List<String> blockList;
    private List<NotificationMessage> notificationList;

    private ConnectionHandler<Message> connectionHandler;
    public Client(String username, String password,String birthday, ConnectionHandler<Message> connectionHandler){
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        followers = new LinkedList<>();
        following = new LinkedList<>();
        blockList = new LinkedList<>();
        notificationList = new LinkedList<>();
        this.connectionHandler = connectionHandler;
    }


    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getBirthday() { return birthday; }

    public List<String> getBlockList() {
        return blockList;
    }

    public int getNumberOfPosts() {
        return numberOfPosts;
    }
    public void incrementNumberOfPosts() {
        numberOfPosts++;
    }

    public int getAge() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = formatter.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Instant instant = date.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate givenDate = zone.toLocalDate();
        Period period = Period.between(givenDate, LocalDate.now());
        return period.getYears();
    }

    public List<String> getFollowers() { return followers; }

    public List<String> getFollowing() { return following; }
    public List<NotificationMessage> getNotificationList() { return notificationList; }

    public ConnectionHandler<Message> getConnectionHandler() { return connectionHandler; }

    public void setConnectionHandler(ConnectionHandler connectionHandler) { this.connectionHandler = connectionHandler; }

}