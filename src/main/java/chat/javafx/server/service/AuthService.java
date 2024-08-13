package chat.javafx.server.service;

public interface AuthService {
    boolean authorize(String login, String password);
    boolean register(String login, String password);
    void logout(String login);
}
