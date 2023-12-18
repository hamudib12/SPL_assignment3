GUIDE TO RUN OUR CODE:

ThreadPerClient:
	-mvn clean
	-mvn compile
	-mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"
Reactor:
	-mvn clean 
	-mvn compile
	-mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 10"

Client:
	-make clean all
	-./bin/BGSclient 127.0.0.1 7777

Input correctenns:
	-Date format : DD-MM-YYYY
	-to edit filtered messages go to : ./323068692_322540964/Server/src/main/java/bgu/spl/net/api/bidi/ConnectionsImpl.java
	



Example of the correct format of written commands:
	
	1. REGISTER Message:
		REGISTER <Username> <Password> <DD-MM-YYYY>
	
	2.LOGIN Message:
		LOGIN <Username> <Password> <Captcha : 0 for "failure" 1 for "success">
	
	3.FOLLOW Message:
		FOLLOW <0/1 (Follow/Unfollow)> <UserName>		
	
	4.POST Message:
		POST <PostMsg(Content)>

	5.PM Message:
		PM <Username> <Content>

	6.LOGSTAT Message:
		LOGSTAT
	
	7.STAT Message:
		STAT usernam1|username2|username3|...

	8.LOGOUT Message:
		LOGOUT
	
	9:BLOCK Message:
		BLOCK <UserName to block> 
		
