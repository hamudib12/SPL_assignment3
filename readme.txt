```markdown
# Ben Gurion Social (BGS) - Social Network Server and Client

## Overview

Welcome to the Ben Gurion Social (BGS) project, a simple social network server and client implementation. This project uses a binary communication protocol and supports both Thread-Per-Client (TPC) and Reactor server patterns.

## Features

- **Push Notifications:** Real-time interaction between users with support for push notifications.
  
- **BGS Protocol:** Emulates a social network with features such as user registration, login, posting messages, following/unfollowing other users, sending private messages, and retrieving statistics.

- **Data Storage:** In-memory storage for users, passwords, messages, etc., during the server runtime.

## Establishing a Connection

1. **Register a New User:**
   ```
   REGISTER <Username> <Password> <DD-MM-YYYY>
   ```

2. **Log In:**
   ```
   LOGIN <Username> <Password> <Captcha: 0 for "failure" 1 for "success">
   ```

3. **Post a Message:**
   ```
   POST <PostMsg(Content)>
   ```

4. **Follow/Unfollow:**
   ```
   FOLLOW <0/1 (Follow/Unfollow)> <UserName>
   ```

5. **Send Private Message:**
   ```
   PM <Username> <Content>
   ```

6. **Retrieve User Stats:**
   ```
   LOGSTAT
   ```

7. **Retrieve Stats for Multiple Users:**
   ```
   STAT usernam1|username2|username3|...
   ```

8. **Log Out:**
   ```
   LOGOUT
   ```

9. **Block a User:**
   ```
   BLOCK <UserName to block>
   ```

## Implementation Details

### Server

- Written in Java.
- Supports both TPC and Reactor server patterns.
- Maven is used as the build tool.

### Client

- Written in C++ with BOOST.
- Multi-threaded client for keyboard and socket input.
- Makefile is used for building.

## Running the Code

### ThreadPerClient Server

1. **Clean and Compile:**
   ```
   mvn clean
   mvn compile
   ```

2. **Run Server:**
   ```
   mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"
   ```

### Reactor Server

1. **Clean and Compile:**
   ```
   mvn clean 
   mvn compile
   ```

2. **Run Server:**
   ```
   mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 10"
   ```

### Client

1. **Build Client:**
   ```
   make clean all
   ```

2. **Run Client:**
   ```
   ./bin/BGSclient 127.0.0.1 7777
   ```

## Input Corrections

- **Date Format:** DD-MM-YYYY

- **Filtered Messages Editing:**
  To edit filtered messages, go to: `./Server/src/main/java/bgu/spl/net/api/bidi/ConnectionsImpl.java`

## Example Commands

See the example commands section for correctly formatted commands.

```

