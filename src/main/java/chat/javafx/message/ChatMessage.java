package chat.javafx.message;

import java.io.Serial;
import java.io.Serializable;

public class ChatMessage extends AbstractMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String text;

    public ChatMessage(String text) {
        super(MessageType.CHAT_MESSAGE);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
