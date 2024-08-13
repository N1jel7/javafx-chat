package chat.javafx.server.service;

import chat.javafx.message.AbstractMessage;

public interface RequestHandler {
    void handle(ServerThread serverThread, AbstractMessage message);
}
