//
// Created by Hamudi Brik on 04/01/2022.
//

#ifndef ASSIGNMENT3_CLIENT_ENCODER_H
#define ASSIGNMENT3_CLIENT_ENCODER_H

#endif //ASSIGNMENT3_CLIENT_ENCODER_H
#include <istream>
#include <vector>
#include <connectionHandler.h>
#include <condition_variable>
#include <mutex>
using namespace std;
class Encoder{
public:
    Encoder(ConnectionHandler* connectionHandler_ , bool terminate , mutex& receiveMutex , std::condition_variable &con);
    void run();
    vector<string> split(string str);
    short getOp(string str);
    bool sendOp(short opcode);
private:
    ConnectionHandler* connectionHandler;
    bool terminate;
    mutex& _mutex;
    std::condition_variable& cv;

};