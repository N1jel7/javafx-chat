package chat.javafx.client;

import chat.javafx.client.service.Client;
import chat.javafx.client.service.MessageHandlerImpl;
import chat.javafx.client.ui.AbstractController;
import chat.javafx.client.ui.AlertUtil;
import chat.javafx.client.ui.dto.ViewResource;
import chat.javafx.message.AbstractMessage;
import chat.javafx.message.RegistrationRequest;
import chat.javafx.message.RequestAuth;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static chat.javafx.client.ClientApplication.ResourceType.*;
import static chat.javafx.client.ClientApplication.StageType.MAIN;
import static chat.javafx.client.ClientApplication.StageType.MODAL;
import static chat.javafx.message.MessageType.USER_AUTH_RESPONSE;

public class ClientApplication extends Application {
    private static ClientApplication instance;
    private Map<ResourceType, ViewResource> resources;
    private Stage applicationStage;
    private Stage modalStage;
    private Client client;
    private String username;
    private boolean displayNextUserResponse;

    public boolean isDisplayNextUserResponse() {
        return displayNextUserResponse;
    }

    public void setDisplayNextUserResponse(boolean displayNextUserResponse) {
        this.displayNextUserResponse = displayNextUserResponse;
    }

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

    public static ClientApplication getInstance() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
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

    public <T extends AbstractController> T getController(Class<T> controllerClass) {
        for (ViewResource value : resources.values()) {
            if (value.getController().getClass().equals(controllerClass)) {
                return (T) value.getController();
            }
        }
        throw new IllegalArgumentException("Controller class not exists");
    }

    public byte[] showFilechooser() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(applicationStage);
        try {
            byte[] image = Files.readAllBytes(file.toPath());
            return image;
        } catch (IOException e) {

        }
        return null;
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

    public void setTitle(StageType stageType, String title) {
        Platform.runLater(() -> {
            Stage stage = getStage(stageType);
            stage.setTitle(title);
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
    }

    public void authorize(String username, String pass) {
        this.username = username;
        sendMessageToServer(new RequestAuth(pass));
    }

    public void connect(String host, int port) {
        try {
            client = Client.connect(host, port);

            client.subscribe(msg -> new MessageHandlerImpl().handleMessage(msg));

            client.subscribe(message -> {
                if (message.getType().equals(USER_AUTH_RESPONSE)) {

                }
            });

        } catch (RuntimeException e) {
            System.out.println("Unable to connect to the server");
            AlertUtil.createAlert(Alert.AlertType.WARNING, "Connection", "Unable to connect to the server!");
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