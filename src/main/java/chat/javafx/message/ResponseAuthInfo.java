package chat.javafx.message;

import java.io.Serializable;

public class ResponseAuthInfo extends AbstractMessage implements Serializable {

    boolean authorized;

    public ResponseAuthInfo(boolean authorized) {
        super(MessageType.USER_AUTH_RESPONSE);
        this.authorized = authorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
