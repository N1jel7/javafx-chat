package chat.javafx.message;

import java.io.Serializable;

public class RegistrationRequest extends AbstractMessage implements Serializable {

    String login;
    String pass;

    public RegistrationRequest(String login, String pass) {
        super(MessageType.REGISTRATION_REQUEST);
        this.login = login;
        this.pass = pass;

    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }
}
