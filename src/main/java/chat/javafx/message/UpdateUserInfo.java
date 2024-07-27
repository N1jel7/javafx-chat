package chat.javafx.message;

import java.io.Serializable;
import java.time.LocalDate;

public class UpdateUserInfo extends AbstractMessage implements Serializable {
    private String firstname;
    private String lastname;
    private LocalDate birthday;

    public UpdateUserInfo(String firstname, String lastname, LocalDate birthday) {
        super(MessageType.USER_DATA_UPDATE);
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;

    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }
}
