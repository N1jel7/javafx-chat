package chat.javafx.message;

import java.io.Serializable;

public class RequestUserInfo extends AbstractMessage implements Serializable {

    private String login;

    public RequestUserInfo(String login) {
        super(MessageType.USER_DATA_REQUEST);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
