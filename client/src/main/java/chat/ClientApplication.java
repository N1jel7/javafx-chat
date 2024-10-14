package chat;

import by.chat.SocketTemplate;
import chat.message.AbstractMessage;
import chat.message.request.AuthRequest;
import chat.message.request.LogoutRequest;
import chat.message.request.RegistrationRequest;
import chat.service.MessageHandlerImpl;
import chat.ui.AbstractController;
import chat.ui.AlertUtil;
import chat.ui.dto.ViewResource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static chat.ClientApplication.ResourceType.*;
import static chat.ClientApplication.StageType.MAIN;
import static chat.ClientApplication.StageType.MODAL;

public class ClientApplication extends Application {

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh");
        System.setProperty("currenttime", dateFormat.format(new Date()));
    }

    private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);
    private static ClientApplication instance;
    private Map<ResourceType, ViewResource> resources;
    private Stage applicationStage;
    private Stage modalStage;
    private String username;
    private boolean displayNextUserResponse;
    private MessageHandlerImpl messageHandler;
    private SocketTemplate socketTemplate;
    private Socket socket;

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
        log.info(readBanner("/chat/banner.txt"));

        this.applicationStage = stage;

        applicationStage.setOnCloseRequest(e -> {
            if(socket != null && socket.isConnected()) {
                sendMessageToServer(new LogoutRequest(username));
                log.info("Closing online application.");
                System.exit(0);
            } else {
                log.info("Closing offline application.");
                System.exit(0);
            }
        });

        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        resources = Map.of(
                CHAT, loadViewResource("/chat"),
                LOGIN, loadViewResource("/chat/login"),
                EDITOR, loadViewResource("/chat/infoEditor"),
                VIEW, loadViewResource("/chat/infoView"),
                REGISTER, loadViewResource("/chat/register"),
                WELCOME, loadViewResource("/chat/welcome")
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
            log.warn("Can't read avatar image", e);
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
        sendMessageToServer(new AuthRequest(pass));
    }

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            messageHandler = new MessageHandlerImpl();
            socketTemplate = new SocketTemplate(socket) {
                @Override
                public void onMessageReceived(AbstractMessage message) {
                    messageHandler.handleMessage(message);
                }

                @Override
                public void onConnectionOpened() {

                }

                @Override
                public void onConnectionClosed() {
                    showResource(WELCOME, MAIN);
                    Platform.runLater(() -> {
                        AlertUtil.showInfo("Server closed", "Server closed the connection");
                    });
                }
            };


        } catch (IOException e) {
            log.warn("Unable to connect to the server");
            AlertUtil.showWarning("Connection", "Unable to connect to the server!");
        }
    }

    public void sendMessageToServer(AbstractMessage message) {
        message.setSender(username);
        socketTemplate.send(message);
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch();
    }

    private static String readBanner(String path) {
        try {
            return "\n\n\n" +
                    Files.readString(Path.of(ClientApplication.class.getResource(path)
                            .getPath().substring(1))) +
                    "\n\n\n";

        } catch (IOException e) {
            return "";
        }
    }
}