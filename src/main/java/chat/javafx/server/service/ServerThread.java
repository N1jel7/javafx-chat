package chat.javafx.server.service;

import chat.javafx.message.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ServerThread.class);
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String username;

    private RequestHandler requestHandler;

    public ServerThread(Socket socket, RequestHandler requestHandler) {
        try {
            this.requestHandler = requestHandler;
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            log.error("Error creating server", e);
            closeConnection();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMessageToClient(AbstractMessage message) {
        try {
            objectOutputStream.writeObject(message);

        } catch (IOException e) {
            log.debug("Error sending message to the client", e);
            closeConnection();
        }

    }


    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                requestHandler.handle(this, (AbstractMessage) objectInputStream.readObject());

            } catch (Exception e) {
                log.debug("Error receiving message from client", e);
                closeConnection();
                break;
            }
        }
    }
}
