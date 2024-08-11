package chat.javafx.client.service;

import chat.javafx.message.AbstractMessage;

public interface MessageHandler {
    void handleMessage(AbstractMessage message);
}
