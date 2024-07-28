package chat.javafx.server.dao;

public interface UserDao {

    UserDto findUserByLogin(String login);
    void saveUserData(UserDto userDto);
    void updateUserData(UserDto userDto);

}
