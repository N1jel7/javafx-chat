package chat.service;

import by.chat.SocketTemplate;
import chat.dao.UserDao;
import chat.dao.UserDto;
import chat.message.AbstractMessage;
import chat.message.ChatMessage;
import chat.message.request.*;
import chat.message.response.AuthInfoResponse;
import chat.message.response.RegistrationResponse;
import chat.message.response.UserInfoResponse;
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
    public void handle(SocketTemplate template, AbstractMessage message) {
        ServerCore server = ServerCore.getInstance();
        boolean authorized = authService.isAuthorized(message.getSender());
        if(authorized){
            handleAuthorized(server, template, message);
        }else {
            handleUnauthorized(template, message);
        }
        log.info("Server had received message with type: {}", message.getType());
    }

    private void handleAuthorized(ServerCore server, SocketTemplate template, AbstractMessage message) {
        switch (message.getType()) {
            case CHAT_MESSAGE -> {
                ChatMessage chatMessage = (ChatMessage) message;
                server.sendToAll(template, chatMessage);
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
                    template.send(userInfoResponse);
                } else {
                    UserInfoResponse userInfoResponse = new UserInfoResponse(user.getAvatar(), user.getLogin(), user.isOnline(), user.getFirstname(), user.getLastname(), user.getBirthday());
                    template.send(userInfoResponse);
                }
            }
            case LOGOUT_REQUEST -> {
                LogoutRequest logoutRequest = (LogoutRequest) message;
                authService.logout(logoutRequest.getLogin());
                log.info("User {} disconnected from the server", logoutRequest.getLogin());
                template.closeConnection();
            }

            default -> {
                log.warn("Unknown message type: {}", message.getType());
            }
        }
    }

    private void handleUnauthorized(SocketTemplate socketTemplate, AbstractMessage message) {
        switch (message.getType()) {
            case REGISTRATION_REQUEST -> {
                RegistrationRequest registrationRequest = (RegistrationRequest) message;
                boolean register = authService.register(registrationRequest.getLogin(), registrationRequest.getPass());
                socketTemplate.send(new RegistrationResponse(register));
            }
            case AUTH_REQUEST -> {
                AuthRequest authRequest = (AuthRequest) message;
                boolean authorized = authService.authorize(authRequest.getSender(), authRequest.getPass());
                AuthInfoResponse authResponse = new AuthInfoResponse(authorized);
                authResponse.setSender(message.getSender());
                socketTemplate.send(authResponse);
            }
            default -> {
                log.warn("Unsupported message for unauthorized user: {}", message.getType());

            }
        }
    }
}
