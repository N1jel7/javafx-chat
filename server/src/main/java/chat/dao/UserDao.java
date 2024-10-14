package chat.dao;

public interface UserDao {

    UserDto findUserByLogin(String login);
    void saveUserData(UserDto userDto);
    void updateUserData(UserDto userDto);
    String findUserHashByLogin(String login);
    void registerUser(String login, String password);

}
