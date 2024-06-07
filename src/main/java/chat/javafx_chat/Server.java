package chat.javafx_chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Server implements Runnable {
    private final List<ServerHandler> connections;
    private final List<Consumer<String>> messageListeners;
    private final int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        messageListeners = new ArrayList<>();
        this.port = port;
        this.connections = new ArrayList<>();
    }

    public void subscribeForNewMessages(Consumer<String> subscriber){
        messageListeners.add(subscriber);
    }

    private void startServer(){
        try {
            serverSocket =  new ServerSocket(port);
            System.out.println("Starting local server on port " + port);
        } catch (IOException e) {
            throw new RuntimeException("Can't run local server on port " + port, e);
        }
    }

    public void sendMessageToAllClients(ServerHandler sender, String message){
        for (ServerHandler connection : connections) {
            if(!connection.equals(sender)){
                connection.sendMessageToClient(message);
            }
        }
        for (Consumer<String> messageListener : messageListeners) {
            messageListener.accept(message);
        }
    }

    @Override
    public void run() {
        startServer();
        while(!serverSocket.isClosed()){
            try {
                Socket clientSocket = serverSocket.accept();
                ServerHandler clientConnection = new ServerHandler(this, clientSocket);
                connections.add(clientConnection);
                new Thread(clientConnection).start();

            } catch (IOException e) {
                throw new RuntimeException("Can't accept new connection", e);
            }

        }
    }

    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't stop server", e);
        }
    }
}
