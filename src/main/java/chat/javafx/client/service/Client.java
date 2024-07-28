package chat.javafx.client.service;

import chat.javafx.message.AbstractMessage;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Client {
    private List<Consumer<AbstractMessage>> subscribers = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            startMessageReceiver();
        } catch (IOException e) {
            System.out.println("Error creating the client");
            e.printStackTrace();
            closeEverything(socket, objectOutputStream);
        }

    }

    public static Client connect(String host, int port){
        try {
            return new Client(new Socket(host, port));
        } catch (IOException e) {
            throw new RuntimeException("Can't create client", e);
        }
    }

    public void subscribe(Consumer<AbstractMessage>... subscribers){
        this.subscribers.addAll(Arrays.asList(subscribers));
    }

    public void sendMessageToServer(AbstractMessage message) {
        try {
            objectOutputStream.writeObject(message);

        } catch (IOException e) {
            System.out.println("Error sending message to the server");
            e.printStackTrace();
            closeEverything(socket, objectOutputStream);
        }

    }

    public void startMessageReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

                    while (socket.isConnected()) {
                        AbstractMessage message = (AbstractMessage) inputStream.readObject();
                        for (Consumer<AbstractMessage> subscriber : subscribers) {
                            subscriber.accept(message);
                            System.out.println("Client had received message with type: " + message.getType());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error receiving message from server.");
                    e.printStackTrace();
                } finally {
                    closeEverything(socket, objectOutputStream);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectOutputStream objectOutputStream) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
