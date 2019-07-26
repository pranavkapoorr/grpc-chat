package com.pranavkapoorr.grpc_chat.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Timestamp;
import com.pranavkapoorr.grpc_chat.proto.*;
import com.pranavkapoorr.grpc_chat.proto.ChatServiceGrpc.ChatServiceImplBase;

import io.grpc.stub.StreamObserver;

public class ChatServiceImpl extends ChatServiceImplBase {
	private static Set<StreamObserver<ChatMessageFromServer>> observers = ConcurrentHashMap.newKeySet();
	
	@Override
	public StreamObserver<ChatMessage> chat(final StreamObserver<ChatMessageFromServer> responseObserver) {
		observers.add(responseObserver);
		return new StreamObserver<ChatMessage>() {
		      public void onNext(ChatMessage value) {
		    	  System.out.println(value);
			        ChatMessageFromServer message = ChatMessageFromServer.newBuilder()
			            .setMessage(value)
			            .setTimestamp(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
			            .build();

			        for (StreamObserver<ChatMessageFromServer> observer : observers) {
			          observer.onNext(message);
			        }
		    	
		    }

		    public void onCompleted() {
		    	observers.remove(responseObserver);
		    }

			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}
		    };
	}
}
