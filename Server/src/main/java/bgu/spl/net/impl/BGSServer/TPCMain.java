package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.bidi.BidiMessagingEncoderDecoder;

import bgu.spl.net.api.bidi.BidiMessagingEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args){

        Server.threadPerClient(Integer.parseInt(args[0]),BidiMessagingProtocolImpl::new,BidiMessagingEncoderDecoderImpl::new).serve();
    }
}
