package chat.javafx.message.response;

import chat.javafx.message.AbstractMessage;
import chat.javafx.message.MessageType;

import java.io.Serializable;

public class RegistrationResponse extends AbstractMessage implements Serializable {

    boolean register;

    public RegistrationResponse(boolean register) {
        super(MessageType.REGISTRATION_RESPONSE);
        this.register = register;
    }

    public boolean isRegister() {
        return register;
    }
}
