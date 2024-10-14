package chat.message.request;

import chat.message.AbstractMessage;
import chat.message.MessageType;

import java.io.Serializable;

public class LogoutRequest extends AbstractMessage implements Serializable {

    private String login;

    public LogoutRequest(String login) {
        super(MessageType.LOGOUT_REQUEST);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
