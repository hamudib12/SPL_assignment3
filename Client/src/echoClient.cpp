//
// Created by Hamudi Brik on 05/01/2022.
//
#include <stdlib.h>
#include <boost/thread.hpp>
#include <connectionHandler.h>
#include <Encoder.h>
#include <Decoder.h>
#include <thread>
using namespace std;
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/



int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    //terminate flag
    bool shouldTerminate = false;
    //host address
    std::string host = argv[1];
    //port
    short port = atoi(argv[2]);
    //Client connectionHandler
    ConnectionHandler* connectionHandler = new ConnectionHandler(host,port);
    //connecting the client
    if(!connectionHandler->connect()){
        std::cerr<< "Cannot connect to " << host << ";" << port << std::endl;
        return 1;
    }
    //lock mechanism
    mutex mutex1;
    std::condition_variable cv;
    Encoder encoder(connectionHandler, shouldTerminate, mutex1 , cv);
    Decoder decoder(connectionHandler, shouldTerminate, cv);
    //creating threads for receiving and sending messages
        std::thread getThread(&Encoder::run, &encoder);
        std::thread sendThread(&Decoder::run, &decoder);
        sendThread.join();
        getThread.join();
        connectionHandler->close();
    return 0;
    }
