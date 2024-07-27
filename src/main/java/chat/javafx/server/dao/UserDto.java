package chat.javafx.server.dao;


import java.time.LocalDate;
import java.util.Objects;

public class UserDto {

    private String login;
    private boolean online;
    private String firstname;
    private String lastname;
    private LocalDate birthday;

    public UserDto(String login, boolean online, String firstname, String lastname, LocalDate birthday) {
        this.login = login;
        this.online = online;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "login='" + login + '\'' +
                ", online=" + online +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto userDto)) return false;

        return online == userDto.online && login.equals(userDto.login) && Objects.equals(firstname, userDto.firstname) && Objects.equals(lastname, userDto.lastname) && Objects.equals(birthday, userDto.birthday);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + Boolean.hashCode(online);
        result = 31 * result + Objects.hashCode(firstname);
        result = 31 * result + Objects.hashCode(lastname);
        result = 31 * result + Objects.hashCode(birthday);
        return result;
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


