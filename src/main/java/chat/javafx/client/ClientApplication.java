package chat.javafx.client;

import chat.javafx.client.service.Client;
import chat.javafx.client.ui.AbstractController;
import chat.javafx.client.ui.AlertUtil;
import chat.javafx.client.ui.ChatController;
import chat.javafx.client.ui.LoginController;
import chat.javafx.client.ui.dto.ViewResource;
import chat.javafx.message.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

import static chat.javafx.client.ClientApplication.ResourceType.*;
import static chat.javafx.client.ClientApplication.StageType.MAIN;
import static chat.javafx.client.ClientApplication.StageType.MODAL;
import static chat.javafx.message.MessageType.USER_AUTH_RESPONSE;

public class ClientApplication extends Application {
    private Map<ResourceType, ViewResource> resources;
    private Stage applicationStage;
    private Stage modalStage;
    private Client client;
    private String username;

    public enum ResourceType {
        CHAT("Client - ", false),
        LOGIN("Login", false),
        EDITOR("Edit info", false),
        VIEW("View info", false),
        REGISTER("Registration", false),
        WELCOME("Welcome!", false);

        private final String title;
        private final boolean resizable;

        ResourceType(String title, boolean resizable) {
            this.title = title;
            this.resizable = resizable;
        }
    }

    public enum StageType {
        MAIN,
        MODAL
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.applicationStage = stage;
        applicationStage.setOnCloseRequest(e -> System.exit(0));

        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        resources = Map.of(
                CHAT, loadViewResource("/chat/javafx/chat"),
                LOGIN, loadViewResource("/chat/javafx/login"),
                EDITOR, loadViewResource("/chat/javafx/infoEditor"),
                VIEW, loadViewResource("/chat/javafx/infoView"),
                REGISTER, loadViewResource("/chat/javafx/register"),
                WELCOME, loadViewResource("/chat/javafx/welcome")
        );

        showResource(WELCOME, MAIN);
    }

    public void showResource(ResourceType resourceType, StageType stageType) {
        Platform.runLater(() -> {
            Stage stage = getStage(stageType);
            ViewResource viewResource = resources.get(resourceType);
            stage.setScene(viewResource.getScene());
            stage.setResizable(resourceType.resizable);
            stage.setTitle(resourceType.title);
            if (stage.equals(applicationStage)) {
                stage.show();
            } else {
                stage.showAndWait();
            }
        });
    }

    public void closeStage(StageType stageType) {
        getStage(stageType).close();
    }

    private Stage getStage(StageType stageType) {
        return MODAL.equals(stageType) ? modalStage : applicationStage;
    }

    private ViewResource loadViewResource(String resourcePath) throws IOException {
        FXMLLoader resourceLoader = new FXMLLoader(ClientApplication.class.getResource(resourcePath + "/view.fxml"));
        Scene scene = new Scene(resourceLoader.load());
        AbstractController controller = resourceLoader.getController();
        controller.setApplication(this);
        return new ViewResource(controller, scene);
    }

    public void register(String login, String password, String host, int port) {
        connect(host, port);
        sendMessageToServer(new RegistrationRequest(login, password));

        client.subscribe(message -> {
            RegistrationResponse registrationResponse = (RegistrationResponse) message;
            if (registrationResponse.isRegister()) {
                Platform.runLater(() -> {
                    AlertUtil.createAlert(Alert.AlertType.INFORMATION, "Registration", "You successfully registered. Now you need to login into your account!");
                    showResource(LOGIN, MAIN);
                    ((LoginController) resources.get(LOGIN).getController()).setUsername(login);
                });
            } else {
                Platform.runLater(() -> {
                    AlertUtil.createAlert(Alert.AlertType.WARNING, "Registration", "Wrong credentials");
                });
            }

        });
    }

    public void authorize(String username, String pass) {
        this.username = username;
        sendMessageToServer(new RequestAuth(pass));
    }

    public void connect(String host, int port) {
        try {
            client = Client.connect(host, port);

            client.subscribe(message -> {
                if (message.getType().equals(USER_AUTH_RESPONSE)) {
                    ResponseAuthInfo responseAuthInfo = (ResponseAuthInfo) message;
                    if (!responseAuthInfo.isAuthorized()) {
                        Platform.runLater(() -> {
                            AlertUtil.createAlert(Alert.AlertType.WARNING, "Authorization", "Wrong credentials!");
                        });
                    } else {
                        Platform.runLater(() -> {
                            applicationStage.setTitle("Client - " + username);
                            client.subscribe(msg -> ((ChatController) resources.get(CHAT).getController()).onMessageReceived(msg));
                            showResource(CHAT, MAIN);
                            System.out.println("Connection to the server.");
                        });
                    }
                }
            });

        } catch (RuntimeException e) {
            System.out.println("Unable to connect to the server");
            AlertUtil.createAlert(Alert.AlertType.WARNING, "Connection", "Unnable to connect to the server!");
        }
    }

    public void sendMessageToServer(AbstractMessage message) {
        message.setSender(username);
        client.sendMessageToServer(message);
    }

    public static void main(String[] args) {
        launch();
    }
}