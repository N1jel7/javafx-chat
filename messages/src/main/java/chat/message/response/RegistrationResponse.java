package chat.message.response;

import chat.message.AbstractMessage;
import chat.message.MessageType;

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
