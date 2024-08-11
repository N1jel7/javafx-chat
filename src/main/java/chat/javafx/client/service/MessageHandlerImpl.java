package chat.javafx.client.service;

import chat.javafx.client.ClientApplication;
import chat.javafx.client.ui.AlertUtil;
import chat.javafx.client.ui.ChatController;
import chat.javafx.client.ui.DataViewController;
import chat.javafx.client.ui.LoginController;
import chat.javafx.client.ui.dto.ClientCache;
import chat.javafx.message.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

import static chat.javafx.client.ClientApplication.ResourceType.*;
import static chat.javafx.client.ClientApplication.StageType.MAIN;
import static chat.javafx.client.ClientApplication.StageType.MODAL;

public class MessageHandlerImpl implements MessageHandler {

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
                ResponseUserInfo responseUserInfo = (ResponseUserInfo) message;

                ClientCache.getInstance().add(responseUserInfo);

                ChatController chatController = application.getController(ChatController.class);
                chatController.setUserImage(responseUserInfo.getLogin(), new Image(new ByteArrayInputStream(responseUserInfo.getAvatar())));

                if(application.isDisplayNextUserResponse()) {
                    DataViewController dataViewController = application.getController(DataViewController.class);
                    dataViewController.viewUserInfo(responseUserInfo);
                    application.showResource(VIEW, MODAL);
                    application.setDisplayNextUserResponse(false);
                }
                System.out.println("User info received.");
            }
            case USER_AUTH_RESPONSE -> {
                ResponseAuthInfo responseAuthInfo = (ResponseAuthInfo) message;
                if (!responseAuthInfo.isAuthorized()) {
                    Platform.runLater(() -> {
                        AlertUtil.createAlert(Alert.AlertType.WARNING, "Authorization", "Wrong credentials!");
                    });
                } else {
                    Platform.runLater(() -> {

                        application.showResource(CHAT, MAIN);
                        application.setTitle(MAIN, "Client - " + responseAuthInfo.getSender());
                        System.out.println("Connection to the server.");
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
            default -> throw new IllegalStateException("Unexpected value: " + message.getType());
        }
    }
}
