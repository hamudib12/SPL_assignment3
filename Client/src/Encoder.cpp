//
// Created by Hamudi Brik on 04/01/2022.
//
#include "Encoder.h"
#include<vector>
    Encoder::Encoder(ConnectionHandler* connectionHandler_ , bool terminate , mutex& receiveMutex , std::condition_variable &con): connectionHandler(connectionHandler_) , terminate(terminate) , _mutex(receiveMutex) , cv(con){}




//------------------------------------RUN---------------------------------------------------------
    void Encoder::run() {
    while (!terminate) {
        string input;
        getline(std::cin, input);
        vector<string> commandLine;
        commandLine = split(input);
        short opCode = getOp(commandLine[0]);
        if (opCode != -1) {
            if (opCode == 3) {
                terminate = true;
                sendOp(opCode);
                char delmitar = ';';
                connectionHandler->sendBytes(&delmitar,1);
                std::unique_lock<std::mutex> lck(_mutex);
                cv.wait(lck);
            } else if (opCode == 7) {
                sendOp(opCode);
                char delmitar = ';';
                connectionHandler->sendBytes(&delmitar, 1);
            }
            else{
                sendOp(opCode);
                char nullTerminator[1] = {'\0'};
                connectionHandler->sendBytes(nullTerminator, 1);
                int commandLineSize = commandLine.size();
                for (int i = 1; i < commandLineSize - 1; i++) {
                    connectionHandler->sendLine(commandLine[i]);
                }
                connectionHandler->sendFrameAscii(commandLine[commandLineSize - 1], ';');
            }
        }
    }

}



//------------------------------------Split---------------------------------------------------------
    vector<string> Encoder:: split(string str){
        vector<string> v;
        string temp = "";
        int strLength = str.length();
        for(int i=0;i<strLength;++i){

            if(str[i] == ' '){
                v.push_back(temp);
                temp = "";
            }
            else{
                temp.push_back(str[i]);
            }

        }
        v.push_back(temp);
        return v;
    }
//------------------------------------GETOP---------------------------------------------------------
    short Encoder::getOp(string str){
        if(str == "REGISTER"){
           return 1;
        }
        if(str ==  "LOGIN") {
            return 2;
        }
        if (str == "LOGOUT"){
            return 3;
        }
        if (str ==  "FOLLOW"){
            return 4;
        }
        if (str == "POST"){
            return 5;
        }
        if (str == "PM" ){
            return 6;
        }
        if (str == "LOGSTAT"){
            return 7;
        }
        if (str == "STAT"){
            return 8;
        }
        if (str == "BLOCK"){
            return 12;
        }
        return -1;
    }



//------------------------------------sendOP---------------------------------------------------------
bool Encoder::sendOp(short opCode){
    char opByte[2];
    opByte[0] = ((opCode >> 8) & 0xFF);
    opByte[1] = (opCode & 0xFF);
    return connectionHandler->sendBytes(opByte , 2);
}