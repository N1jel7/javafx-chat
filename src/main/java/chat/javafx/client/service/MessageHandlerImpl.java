package chat.javafx.client.service;

import chat.javafx.client.ClientApplication;
import chat.javafx.client.ui.AlertUtil;
import chat.javafx.client.ui.ChatController;
import chat.javafx.client.ui.DataViewController;
import chat.javafx.client.ui.LoginController;
import chat.javafx.client.ui.dto.ClientCache;
import chat.javafx.message.*;
import chat.javafx.message.response.AuthInfoResponse;
import chat.javafx.message.response.RegistrationResponse;
import chat.javafx.message.response.UserInfoResponse;
import chat.javafx.server.service.ServerThread;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

import static chat.javafx.client.ClientApplication.ResourceType.*;
import static chat.javafx.client.ClientApplication.StageType.MAIN;
import static chat.javafx.client.ClientApplication.StageType.MODAL;

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
                        AlertUtil.createAlert(Alert.AlertType.WARNING, "Authorization", "Wrong credentials!");
                    });
                } else {
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
                        AlertUtil.createAlert(Alert.AlertType.INFORMATION, "Registration", "You successfully registered. Now you need to login into your account!");
                        application.showResource(LOGIN, MAIN);
                        LoginController controller = application.getController(LoginController.class);
                        controller.setUsername(registrationResponse.getSender());
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertUtil.createAlert(Alert.AlertType.WARNING, "Registration", "Wrong credentials");
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
