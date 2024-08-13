package chat.javafx.server.service;

import chat.javafx.message.AbstractMessage;
import chat.javafx.message.ChatMessage;
import chat.javafx.message.request.AuthRequest;
import chat.javafx.message.request.RegistrationRequest;
import chat.javafx.message.request.UserInfoRequest;
import chat.javafx.message.request.UserInfoUpdateRequest;
import chat.javafx.message.response.AuthInfoResponse;
import chat.javafx.message.response.RegistrationResponse;
import chat.javafx.message.response.UserInfoResponse;
import chat.javafx.server.dao.UserDao;
import chat.javafx.server.dao.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandlerImpl implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandlerImpl.class);
    private final UserDao userDao;
    private final AuthServiceImpl authService;

    public RequestHandlerImpl(UserDao userDao, AuthServiceImpl authService) {
        this.userDao = userDao;
        this.authService = authService;
    }

    @Override
    public void handle(ServerThread serverThread, AbstractMessage message) {
        ServerCore server = ServerCore.getInstance();
        if (serverThread.getUsername() == null) {
            handleUnauthorized(serverThread, message);
        } else {
            handleAuthorized(server, serverThread, message);
        }
        log.info("Server had received message with type: {}", message.getType());
    }

    private void handleAuthorized(ServerCore server, ServerThread serverThread, AbstractMessage message) {
        switch (message.getType()) {
            case CHAT_MESSAGE -> {
                ChatMessage chatMessage = (ChatMessage) message;
                server.sendMessageToAllClients(message.getSender(), chatMessage);
            }
            case USER_DATA_UPDATE -> {
                UserInfoUpdateRequest userInfoUpdateRequest = (UserInfoUpdateRequest) message;

                UserDto userDto = new UserDto(userInfoUpdateRequest.getAvatar(), userInfoUpdateRequest.getSender(), true, userInfoUpdateRequest.getFirstname(), userInfoUpdateRequest.getLastname(), userInfoUpdateRequest.getBirthday());

                if (userDao.findUserByLogin(userInfoUpdateRequest.getSender()) == null) {
                    userDao.saveUserData(userDto);
                } else {
                    userDao.updateUserData(userDto);
                }

            }
            case USER_DATA_REQUEST -> {
                UserInfoRequest userInfoRequest = (UserInfoRequest) message;
                String login = userInfoRequest.getLogin();
                UserDto user = userDao.findUserByLogin(login);
                if (user == null) {
                    UserInfoResponse userInfoResponse = new UserInfoResponse(new byte[0], login, false, null, null, null);
                    log.info("Data of user {} not found!", login);
                    serverThread.sendMessageToClient(userInfoResponse);
                } else {
                    UserInfoResponse userInfoResponse = new UserInfoResponse(user.getAvatar(), user.getLogin(), user.isOnline(), user.getFirstname(), user.getLastname(), user.getBirthday());
                    serverThread.sendMessageToClient(userInfoResponse);
                }
            }

            default -> {
                log.warn("Unknown message type: {}", message.getType());
            }
        }
    }

    private void handleUnauthorized(ServerThread serverThread, AbstractMessage message) {
        switch (message.getType()) {
            case REGISTRATION_REQUEST -> {
                RegistrationRequest registrationRequest = (RegistrationRequest) message;
                boolean register = authService.register(registrationRequest.getLogin(), registrationRequest.getPass());
                serverThread.sendMessageToClient(new RegistrationResponse(register));
            }
            case AUTH_REQUEST -> {
                AuthRequest authRequest = (AuthRequest) message;
                boolean authorized = authService.authorize(authRequest.getSender(), authRequest.getPass());
                if (authorized) {
                    serverThread.setUsername(authRequest.getSender());
                }
                AuthInfoResponse authResponse = new AuthInfoResponse(authorized);
                authResponse.setSender(message.getSender());
                serverThread.sendMessageToClient(authResponse);
            }
            default -> {
                log.warn("Unsupported message for unauthorized user: {}", message.getType());

            }
        }
    }
}
