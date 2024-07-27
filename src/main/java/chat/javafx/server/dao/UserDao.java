package chat.javafx.server.dao;

public interface UserDao {

    UserDto findUserByLogin(String login);
    void save(UserDto userDto);

}
