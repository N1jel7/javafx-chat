package chat.javafx.server.service;

import chat.javafx.message.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerCore extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ServerCore.class);
    private final List<ServerThread> connections;
    private int port;
    private ServerSocket serverSocket;
    private static ServerCore serverCore;
    private RequestHandler requestHandler;

    ServerCore() {
        this.connections = new ArrayList<>();
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public static ServerCore getInstance() {
        if (serverCore == null) {
            serverCore = new ServerCore();
        }
        return serverCore;
    }

    public void start(int port) {
        this.port = port;
        startServer();
        start();
    }


    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            log.info("Starting local server on port {}", port);
        } catch (IOException e) {
            log.debug("Can't run local server on port {}", port, e);
            throw new RuntimeException("Can't run local server on port " + port, e);
        }
    }

    public void sendMessageToAllClients(String sender, AbstractMessage message) {
        for (ServerThread connection : connections) {
            String username = connection.getUsername();
            if (username != null && !username.equals(sender)) {
                connection.sendMessageToClient(message);
            }
        }
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                ServerThread clientConnection = new ServerThread(clientSocket, requestHandler);
                connections.add(clientConnection);
                new Thread(clientConnection).start();

            } catch (IOException e) {
                log.debug("Can't accept new connection", e);
                throw new RuntimeException("Can't accept new connection", e);
            }

        }
    }
}
