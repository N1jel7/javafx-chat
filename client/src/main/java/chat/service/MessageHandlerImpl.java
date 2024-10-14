package chat.service;

import chat.ClientApplication;
import chat.message.AbstractMessage;
import chat.message.ChatMessage;
import chat.message.response.AuthInfoResponse;
import chat.message.response.RegistrationResponse;
import chat.message.response.UserInfoResponse;
import chat.ui.AlertUtil;
import chat.ui.ChatController;
import chat.ui.DataViewController;
import chat.ui.LoginController;
import chat.ui.dto.ClientCache;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

import static chat.ClientApplication.ResourceType.*;
import static chat.ClientApplication.StageType.MAIN;
import static chat.ClientApplication.StageType.MODAL;

public class MessageHandlerImpl implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageHandlerImpl.class);

    @Override
    public void handleMessage(AbstractMessage message) {
        ClientApplication application = ClientApplication.getInstance();
        switch (message.getType()) {
            case CHAT_MESSAGE -> {
                ChatMessage chatMessage = (ChatMessage) message;
                ChatController chatController = application.getController(ChatController.class);
                chatController.addReceivedMessage(chatMessage);
            }
            case USER_DATA_RESPONSE -> {
                UserInfoResponse userInfoResponse = (UserInfoResponse) message;

                ClientCache.getInstance().add(userInfoResponse);

                ChatController chatController = application.getController(ChatController.class);
                chatController.setUserImage(userInfoResponse.getLogin(), new Image(new ByteArrayInputStream(userInfoResponse.getAvatar())));

                if(application.isDisplayNextUserResponse()) {
                    DataViewController dataViewController = application.getController(DataViewController.class);
                    dataViewController.viewUserInfo(userInfoResponse);
                    application.showResource(VIEW, MODAL);
                    application.setDisplayNextUserResponse(false);
                }
                log.info("User info received.");
            }
            case USER_AUTH_RESPONSE -> {
                AuthInfoResponse authInfoResponse = (AuthInfoResponse) message;
                if (!authInfoResponse.isAuthorized()) {
                    Platform.runLater(() -> {
                        AlertUtil.showWarning( "Authorization", "Wrong credentials!");
                    });
                } else {
                    log.info("Logged in as {}", authInfoResponse.getSender());
                    Platform.runLater(() -> {

                        application.showResource(CHAT, MAIN);
                        application.setTitle(MAIN, "Client - " + authInfoResponse.getSender());
                        log.info("Connection to the server.");
                    });
                }
            }
            case REGISTRATION_RESPONSE -> {
                RegistrationResponse registrationResponse = (RegistrationResponse) message;
                if (registrationResponse.isRegister()) {
                    Platform.runLater(() -> {
                        AlertUtil.showInfo( "Registration", "You successfully registered. Now you need to login into your account!");
                        application.showResource(LOGIN, MAIN);
                        LoginController controller = application.getController(LoginController.class);
                        controller.setUsername(registrationResponse.getSender());
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertUtil.showWarning( "Registration", "Wrong credentials");
                    });
                }

            }
            default -> {
                log.warn("Unexpected value: {}", message.getType());
                throw new IllegalStateException("Unexpected value: " + message.getType());
            }
        }
    }
}
