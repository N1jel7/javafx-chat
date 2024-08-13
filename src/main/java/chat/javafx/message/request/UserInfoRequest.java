package chat.javafx.message.request;

import chat.javafx.message.AbstractMessage;
import chat.javafx.message.MessageType;

import java.io.Serializable;

public class UserInfoRequest extends AbstractMessage implements Serializable {

    private String login;

    public UserInfoRequest(String login) {
        super(MessageType.USER_DATA_REQUEST);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
