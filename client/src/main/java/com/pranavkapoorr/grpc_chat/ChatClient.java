package com.pranavkapoorr.grpc_chat;
import com.pranavkapoorr.grpc_chat.proto.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatClient extends Application {
	private ObservableList<String> messages = FXCollections.observableArrayList();
	private ListView<String> messagesView = new ListView<>();
	private TextField name = new TextField("name");
	private TextField message = new TextField();
	private Button send = new Button();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		messagesView.setItems(messages);

		send.setText("Send");

		BorderPane pane = new BorderPane();
		pane.setLeft(name);
		pane.setCenter(message);
		pane.setRight(send);

		BorderPane root = new BorderPane();
		root.setCenter(messagesView);
		root.setBottom(pane);

		primaryStage.setTitle("gRPC Chat");
		primaryStage.setScene(new Scene(root, 480, 320));

		primaryStage.show();

		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext(true).build();
		ChatServiceGrpc.ChatServiceStub chatService = ChatServiceGrpc.newStub(channel);
		StreamObserver<ChatMessage> chat = chatService.chat(new StreamObserver<ChatMessageFromServer>() {

			@Override
			public void onNext(ChatMessageFromServer value) {
				Platform.runLater(() -> {
					messages.add(value.getMessage().getFrom() + ": " + value.getMessage().getMessage());
					messagesView.scrollTo(messages.size());
				});
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				System.out.println("Disconnected");
			}

			@Override
			public void onCompleted() {
				System.out.println("Disconnected");
			}
		});

		send.setOnAction(e -> {
			chat.onNext(ChatMessage.newBuilder().setFrom(name.getText()).setMessage(message.getText()).build());
			message.setText("");
		});
		primaryStage.setOnCloseRequest(e -> {
			chat.onCompleted();
			channel.shutdown();
		});
	}
}
