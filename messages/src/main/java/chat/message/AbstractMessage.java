package chat.message;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    private static final long serialVersionUID = 123123L;

    public AbstractMessage(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    private MessageType type;
    private String sender;

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
