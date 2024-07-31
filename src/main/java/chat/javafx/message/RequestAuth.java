package chat.javafx.message;

import java.io.Serializable;

public class RequestAuth extends AbstractMessage implements Serializable {

    private String pass;

    public RequestAuth(String pass) {
        super(MessageType.AUTH_REQUEST);
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }
}
