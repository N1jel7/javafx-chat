package chat.javafx.server.service;

import chat.javafx.client.ui.ChatController;
import chat.javafx.message.AbstractMessage;
import chat.javafx.message.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable{
    private ServerCore server;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ServerThread(ServerCore server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error creating server.");
            e.printStackTrace();
            closeConnection();
        }

    }

    public void sendMessageToClient(AbstractMessage message) {
        try {
            objectOutputStream.writeObject(message);

        } catch (IOException e) {
            System.out.println("Error sending message to the client");
            e.printStackTrace();
            closeConnection();
        }

    }

    private void closeConnection() {
        try {
            if(socket != null) {
                socket.close();
            }
            if(objectInputStream != null) {
                objectInputStream.close();
            }
            if(objectOutputStream != null) {
                objectOutputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessageFromClient(AbstractMessage message){
        switch (message.getType()){
            case CHAT_MESSAGE -> {
                ChatMessage chatMessage = (ChatMessage) message;
                server.sendMessageToAllClients(this, chatMessage);
            }
            default -> {
                System.out.println("Unknown message type: " + message.getType());
            }
        }

    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                receiveMessageFromClient((AbstractMessage) objectInputStream.readObject());

            } catch (Exception e) {
                System.out.println("Error receiving message from client.");
                e.printStackTrace();
                closeConnection();
                break;
            }
        }
    }
}
