package chat.javafx.message.response;

import chat.javafx.message.AbstractMessage;
import chat.javafx.message.MessageType;

import java.io.Serializable;

public class AuthInfoResponse extends AbstractMessage implements Serializable {

    boolean authorized;

    public AuthInfoResponse(boolean authorized) {
        super(MessageType.USER_AUTH_RESPONSE);
        this.authorized = authorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
