# Ben Gurion Social (BGS) - Social Network Server and Client

## General Description

This repository contains the implementation of a simple social network server and client using a binary communication protocol. The communication between the server and clients is based on the Thread-Per-Client (TPC) and Reactor server patterns taught in class. The server and client(s) support both pull and push notifications.

### Push Notifications

The server has been extended to support push notifications, allowing messages to be sent directly to clients without receiving a request to do so. This behavior enhances the real-time interaction between users and is fundamental for features such as sending messages between clients and broadcasting announcements to a group of clients.

### BGS Protocol

The BGS (Ben Gurion Social) Protocol has been implemented to emulate a simple social network. Users can register, log in, post messages, follow/unfollow other users, send private messages, and retrieve various statistics.

### Data Storage

Unlike real social networks, no real databases are used. Instead, data (users, passwords, messages, etc.) is saved in memory from the time the server starts until it closes.

## Establishing a Connection

Upon connecting, a client must identify itself to the service. A new client will issue a Register command with the requested username, password, and birthday. A registered client can then log in using the Login command. Once logged in successfully, the client can submit other commands.

## Supported Commands

The BGS protocol supports various commands for client-to-server and server-to-client communication. Each command begins with 2 bytes (short) to describe the opcode, followed by specific parameters based on the command type.

### Client-to-Server Commands (Opcodes 1-8)

1. **Register (REGISTER)**
   - Opcode: 1
   - Parameters:
     - Username: The username to register in the server.
     - Password: The password for the username.
     - Birthday: The birthday of the user (DD-MM-YYYY).
   - Command initiation: `REGISTER <Username> <Password> <Birthday>`

2. **Login (LOGIN)**
   - Opcode: 2
   - Parameters:
     - Username: The username to log in.
     - Password: The password for the username.
     - Captcha: A captcha to simulate (must be 1 for successful login).
   - Command initiation: `LOGIN <Username> <Password> <Captcha>`

3. **Logout (LOGOUT)**
   - Opcode: 3
   - Command initiation: `LOGOUT`
   - Notes: Informs the server of client disconnection. Client may terminate only after receiving an ACK message.

4. **Follow/Unfollow (FOLLOW)**
   - Opcode: 4
   - Parameters:
     - Follow/Unfollow: 0 for follow, 1 for unfollow.
     - UserName: The user to follow/unfollow.
   - Command initiation: `FOLLOW <0/1 (Follow/Unfollow)> <UserName>`

5. **Post (POST)**
   - Opcode: 5
   - Parameters:
     - Content: The content of the message to post.
   - Command initiation: `POST <PostMsg>`

6. **PM (PM)**
   - Opcode: 6
   - Parameters:
     - UserName: The user to send the private message to.
     - Content: The content of the private message.
   - Command initiation: `PM <Username> <Content>`

7. **Logged in States (LOGSTAT)**
   - Opcode: 7
   - Command initiation: `LOGSTAT`
   - Notes: Used to receive data on a logged-in user's age, number of posts, followers, and users they are following.

8. **Stats (STAT)**
   - Opcode: 8
   - Parameters:
     - List of usernames: The list of users to retrieve stats for.
   - Command initiation: `STAT <UserNames_list>`

### Server-to-Client Commands (Opcodes 9-11)

9. **Notification (NOTIFICATION)**
   - Opcode: 9
   - Parameters:
     - NotificationType: 0 for PM, 1 for public message.
     - PostingUser: The user who posted/sent the message.
     - Content: The content of the message.
   - Client screen output: `NOTIFICATION <"PM"/"Public"> <PostingUser> <Content>`

10. **Ack (ACK)**
    - Opcode: 10
    - Parameters:
      - Message Opcode: The message opcode the ACK was sent for.
      - Optional: Changes for each message.
    - Client screen output: `ACK <Message Opcode> <Optional>`

11. **Error (ERROR)**
    - Opcode: 11
    - Parameters:
      - Message Opcode: The message opcode the ERROR was sent for.
    - Error Notification: `ERROR <Message Opcode>`

12. **Block (BLOCK)**
    - Opcode: 12
    - Parameters:
      - Username: The username to block.
    - Command initiation: `BLOCK <UserNames>`

## Implementation Details

### General Guidelines

- The server is written in Java, and the client is written in C++ with BOOST.
- Maven is used as the build tool for the server, and a Makefile is used for the C++ client.
- Follow the coding standards expected in the course and previous assignments.

### Server

#### Classes and Interfaces

- **Connections**: This interface maps a unique ID for each active client connected to the server. It has functions for sending messages, broadcasting messages, and disconnecting clients.

- **ConnectionHandler**: This interface has a function for sending messages to clients.

- **BidiMessagingProtocol**: This interface replaces the MessagingProtocol interface. It supports peer-to-peer messaging via the Connections interface and contains functions to start the protocol and process messages.

#### Tasks

1. **Implement Connections**: Hold a list of ConnectionHandler interfaces for each active client. Implement the required functions to send messages, broadcast messages, and disconnect clients.

2. **Refactor Thread-Per-Client Server**: Support the new interfaces. The ConnectionHandler should implement the new interface, working with BidiMessagingProtocol.

3. **Refactor Reactor Server**: Support the new interfaces. The ConnectionHandler should implement the new interface, working with BidiMessagingProtocol.

4. **Implement BidiMessagingProtocol and MessageEncoderDecoder**: Support the BGS protocol as described in section 1.2. Define messages in the interfaces.

#### Leading Questions

- Which classes and interfaces are part of the Server pattern, and which are part of the Protocol implementation?
- When and how do I register a new connection handler to the Connections interface implementation?
- When do I call start to initiate the connections list? What are the implications for the reactor?

#### Tips

- Test tasks 1–3 by fixing one of the examples in the `impl` folder in the supplied `spl-net.zip`.
- Complete tasks 1 and 2, proceed to 4, and return to the reactor code later.

#### Testing Run Commands

- Reactor server: `mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="<port> <Num of threads>"`
  
- Thread per client server: `mvn exec:java -Dexec.mainClass="

bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="<port>"`

### Client

An echo client is provided but is a single-threaded client. Improve the client to run two threads—one for reading from the keyboard and the other for reading from the socket. The client should receive the server's IP and PORT as arguments.

The client should receive commands using standard input. Translate keyboard commands to network messages and vice versa according to the specifications. The client should close itself upon receiving an ACK message in response to an outgoing LOGOUT command.

#### Testing Run Command
