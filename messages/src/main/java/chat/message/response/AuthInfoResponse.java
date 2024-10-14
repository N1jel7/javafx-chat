package chat.message.response;

import chat.message.AbstractMessage;
import chat.message.MessageType;

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
