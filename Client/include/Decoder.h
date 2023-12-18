//
// Created by Hamudi Brik on 04/01/2022.
//

#include "connectionHandler.h"
#include <mutex>
#include <condition_variable>
using namespace std;
#ifndef ASSIGNMENT3_CLIENT_DECODER_H
#define ASSIGNMENT3_CLIENT_DECODER_H

#endif //ASSIGNMENT3_CLIENT_DECODER_H

class Decoder{
public:
    Decoder(ConnectionHandler* connectionHandler_ ,  bool terminate ,std::condition_variable &con);
    void run();
    static short bytesToShort(char* bytesArr);
    static string getMessageType(short opCode);
    static bool isOptional (short opCode);
private:
    ConnectionHandler* connectionHandler;
    bool terminate;
    std::condition_variable& conditionV;
};