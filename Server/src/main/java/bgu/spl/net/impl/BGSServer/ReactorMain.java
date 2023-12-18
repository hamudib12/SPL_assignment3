//
//package bgu.spl.net.impl.BGSServer;
//import bgu.spl.net.api.bidi.BidiMessagingEncoderDecoder;
//
//import bgu.spl.net.api.bidi.BidiMessagingEncoderDecoderImpl;
//import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
//import bgu.spl.net.srv.Reactor;
//
//public class ReactorMain {
//    public static void main(String[] args){
//        Reactor<String> reactorServer = new Reactor(Integer.parseInt(args[1]),
//                Integer.parseInt(args[0]), BidiMessagingProtocolImpl::new , BidiMessagingEncoderDecoderImpl::new);
//        reactorServer.serve();
//    }
//}

