package chat.javafx.message;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ResponseUserInfo extends AbstractMessage implements Serializable {
    private String login;
    private boolean online;
    private String firstname;
    private String lastname;
    private LocalDate birthday;

    public ResponseUserInfo(String login, boolean online,String firstname, String lastname, LocalDate birthday) {
        super(MessageType.USER_DATA_RESPONSE);
        this.login = login;
        this.online = online;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
