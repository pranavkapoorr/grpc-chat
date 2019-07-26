package com.pranavkapoorr.grpc_chat;

import java.io.IOException;

import com.pranavkapoorr.grpc_chat.service.ChatServiceImpl;

import io.grpc.*;

/**
 * server
 *
 */
public class App {
    public static void main( String[] args ) throws IOException, InterruptedException {
    	 Server server = ServerBuilder.forPort(9090)
    			 .addService(new ChatServiceImpl())
    			 .build();
    	    server.start();
    	    server.awaitTermination();
    }
}
