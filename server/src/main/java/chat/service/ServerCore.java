package chat.service;

import by.chat.SocketTemplate;
import chat.message.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerCore extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ServerCore.class);

    private final List<SocketTemplate> connections;
    private static ServerCore serverCore;

    private int port;
    private ServerSocket serverSocket;
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

    public void sendToAll(SocketTemplate sender, AbstractMessage message) {
        connections.stream()
                .filter(socket -> !socket.equals(sender))
                .forEach(socket -> socket.send(message));
        /*for (SocketTemplate template : connections) {
            if(!template.equals(sender)){
                template.send(message);
            }
            //String username = connection.getUsername();
            *//*if (message.getSender() != null && !username.equals(sender)) {
                template.send(message);
            }*//*
        }*/
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                new SocketTemplate(socket) {
                    @Override
                    public void onMessageReceived(AbstractMessage message) {
                        requestHandler.handle(this, message);
                    }

                    @Override
                    public void onConnectionOpened() {
                        connections.add(this);
                    }

                    @Override
                    public void onConnectionClosed() {
                        connections.remove(this);
                    }
                };

            } catch (IOException e) {
                log.debug("Can't accept new connection", e);
            }

        }
    }
}
