package chat.javafx.client;

import chat.javafx.client.ui.ChatController;
import chat.javafx.client.ui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    private ChatController chatController;
    private LoginController loginController;
    private Scene chatScene;
    private Scene loginScene;
    private Stage applicationStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.applicationStage = stage;

        FXMLLoader chatLoader = new FXMLLoader(ClientApplication.class.getResource("/chat/javafx/chat-view.fxml"));
        FXMLLoader loginLoader = new FXMLLoader(ClientApplication.class.getResource("/chat/javafx/login-view.fxml"));

        chatScene = new Scene(chatLoader.load());
        loginScene = new Scene(loginLoader.load());

        chatController = chatLoader.getController();
        loginController = loginLoader.getController();

        chatController.setApplication(this);
        loginController.setApplication(this);

        stage.setOnCloseRequest(e -> System.exit(0));

        stage.setTitle("Client");
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public void connect(String username, String host, int port) {
        applicationStage.setScene(chatScene);
        chatController.connect(host, port);
    }

    public static void main(String[] args) {
        launch();
    }
}