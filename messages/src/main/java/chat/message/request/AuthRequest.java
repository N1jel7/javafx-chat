package chat.message.request;

import chat.message.AbstractMessage;
import chat.message.MessageType;

import java.io.Serializable;

public class AuthRequest extends AbstractMessage implements Serializable {

    private String pass;

    public AuthRequest(String pass) {
        super(MessageType.AUTH_REQUEST);
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }
}
