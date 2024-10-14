package chat.service;

public interface AuthService {
    boolean isAuthorized(String login);
    boolean authorize(String login, String password);
    boolean register(String login, String password);
    void logout(String login);
}
