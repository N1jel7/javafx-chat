package chat.javafx.client.service;

import chat.javafx.message.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private List<Consumer<AbstractMessage>> subscribers = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            startMessageReceiver();
        } catch (IOException e) {
            log.warn("Error creating the client", e);
            closeEverything(socket, objectOutputStream);
        }

    }

    public static Client connect(String host, int port) {
        try {
            return new Client(new Socket(host, port));
        } catch (IOException e) {
            log.warn("Can't create client", e);
            throw new RuntimeException("Can't create client", e);
        }
    }

    public void subscribe(Consumer<AbstractMessage>... subscribers) {
        this.subscribers.addAll(Arrays.asList(subscribers));
    }

    public void sendMessageToServer(AbstractMessage message) {
        try {
            objectOutputStream.writeObject(message);
            log.info("Client send message to the server with type: {}", message.getType());

        } catch (IOException e) {
            log.warn("Error sending message to the server", e);
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
                        log.info("Client had received message with type: {}", message.getType());
                        for (Consumer<AbstractMessage> subscriber : subscribers) {
                            subscriber.accept(message);
                        }
                    }

                } catch (Exception e) {
                    log.info("Connection closed");
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
            log.info("Closing socket", e);
        }
    }

}
