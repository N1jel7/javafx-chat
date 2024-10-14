package chat.service;

import by.chat.SocketTemplate;
import chat.message.AbstractMessage;

public interface RequestHandler {
    void handle(SocketTemplate socketTemplate, AbstractMessage message);
}
