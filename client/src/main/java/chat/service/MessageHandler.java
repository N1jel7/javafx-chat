package chat.service;

import chat.message.AbstractMessage;

public interface MessageHandler {
    void handleMessage(AbstractMessage message);
}
