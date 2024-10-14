package by.chat;

import chat.message.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class SocketTemplate {
    private static final Logger log = LoggerFactory.getLogger(SocketTemplate.class);

    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;


    public SocketTemplate(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            startMessageReceiver();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(AbstractMessage message) {
        if (socket.isConnected() && !socket.isClosed()) {
            try {
                objectOutputStream.writeObject(message);
                log.info("Successfully send message with type {}", message.getType());
            } catch (IOException e) {
                log.warn("Error sending message with type {}", message.getType(), e);
                closeConnection();
            }
        }
    }

    public void startMessageReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onConnectionOpened();
                    while (socket.isConnected()) {
                        onMessageReceived((AbstractMessage) objectInputStream.readObject());
                    }

                    closeConnection();

                } catch (Exception e) {
                    closeConnection();
                }
            }
        }).start();
    }

    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

        } catch (IOException e) {
            log.warn("Can't close socket", e);

        } finally {
            onConnectionClosed();
        }
    }

    public abstract void onMessageReceived(AbstractMessage message);

    public abstract void onConnectionOpened();

    public abstract void onConnectionClosed();
}
