#include <Decoder.h>
#include <connectionHandler.h>

//
// Created by Hamudi Brik on 04/01/2022.
//

Decoder::Decoder(ConnectionHandler* connectionHandler_ , bool terminate , std::condition_variable &con):connectionHandler(connectionHandler_) , terminate(terminate),conditionV(con){}





//------------------------------------RUN---------------------------------------------------------
void Decoder::run() {
    while (!terminate) {
        char bytes[5];
        if(!connectionHandler->getBytes(bytes, 5)) {
            std::cout << "Disconnected. Exiting. \n" <<std::endl;
            break;
        }
        char serverMessage[] = {bytes[0], bytes[1]};
        short serverMessageOp = bytesToShort(serverMessage);
        char clientMessage[] = {bytes[2], bytes[3]};
        short clientMessageOp = bytesToShort(clientMessage);
        string message = "";
        if (serverMessageOp == 9) {
            message = "NOTIFICATION ";
            if (clientMessageOp == 0) {
                message += "PM ";
            }
            if (clientMessageOp == 1) {
                message += "Public ";
            }
            string postUser = "";
            connectionHandler->getFrameAscii(postUser, '\0');
            message += postUser + " ";
            string content = "";
            connectionHandler->getFrameAscii(content, ';');
            message += content + " ";
            message.pop_back();
            message.pop_back();
            std::cout << message << std::endl;
        }
        if (serverMessageOp == 10) {
            message += "ACK ";
            message += std::to_string(clientMessageOp);
            if (clientMessageOp == 3) {
                conditionV.notify_all();
                terminate = true;
                std::cout << message << std::endl;
                break;
            }
            if (isOptional(clientMessageOp)) {
                message += " ";
                if(clientMessageOp == 4){
                    std::string userName = "";
                    connectionHandler->getFrameAscii(userName,';');
                    message += userName;
                    if(message[message.size() - 1] == ';') {
                        message.pop_back();
                    }
                }
                else if (clientMessageOp == 7 || clientMessageOp == 8 ){
                    std::string age = "";
                    connectionHandler->getFrameAscii(age,'\0');
                    message += age + " ";
                    std::string numPosts = "";
                    connectionHandler->getFrameAscii(numPosts,'\0');
                    message += numPosts + " ";
                    std::string numFollowers = "";
                    connectionHandler->getFrameAscii(numFollowers,'\0');
                    message += numFollowers + " ";
                    std::string numFollowing = "";
                    connectionHandler->getFrameAscii(numFollowing,';');
                    message += numFollowing;
                    message.pop_back();
                }
            }
            std::cout << message << std::endl;
        }

        if (serverMessageOp == 11) {
            message += "ERROR ";
            message += std::to_string(clientMessageOp);
            std::cout << message << std::endl;
        }
    }

}









//------------------------------------bytestoshort---------------------------------------------------------
short Decoder::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}







//------------------------------------getMessageType---------------------------------------------------------
string Decoder::getMessageType(short opCode){
    string message;
    if(opCode == 1){
        message += "REGISTER";
    }
    if(opCode == 2){
        message += "LOGIN ";
    }
    if(opCode == 3){
        message += "LOGOUT";

    }
    if(opCode == 4){
        message += "FOLLOW";

    }
    if(opCode == 5){
        message += "POST";
    }
    if(opCode == 6){
        message += "PM";

    }
    if(opCode == 7){
        message += "LOGSTAT";

    }
    if(opCode == 8){
        message += "STAT";
    }
    return message;
}


//------------------------------------isOptional---------------------------------------------------------
bool Decoder:: isOptional (short opCode){
    switch (opCode){
        case 4:
            return true;
        case 7:
            return true;
        case 8:
            return true;
    }
    return false;
}
