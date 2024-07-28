package chat.javafx.client;

import chat.javafx.client.service.Client;
import chat.javafx.client.ui.ChatController;
import chat.javafx.client.ui.DataUpdateController;
import chat.javafx.client.ui.DataViewController;
import chat.javafx.client.ui.LoginController;
import chat.javafx.message.AbstractMessage;
import chat.javafx.message.RequestUserInfo;
import chat.javafx.message.ResponseUserInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientApplication extends Application {
    private ChatController chatController;
    private LoginController loginController;
    private DataUpdateController dataUpdateController;
    private DataViewController dataViewController;
    private Scene chatScene;
    private Scene loginScene;
    private Scene editorScene;
    private Scene viewScene;
    private Stage applicationStage;
    private Stage editorStage;
    private Stage viewStage;
    private Client client;
    private String username;

    @Override
    public void start(Stage stage) throws IOException {
        this.applicationStage = stage;

        FXMLLoader chatLoader = new FXMLLoader(ClientApplication.class.getResource("/chat/javafx/chat-view.fxml"));
        FXMLLoader loginLoader = new FXMLLoader(ClientApplication.class.getResource("/chat/javafx/login-view.fxml"));
        FXMLLoader editorLoader = new FXMLLoader(ClientApplication.class.getResource("/chat/javafx/info-editor.fxml"));
        FXMLLoader viewLoader = new FXMLLoader(ClientApplication.class.getResource("/chat/javafx/info-view.fxml"));

        editorScene = new Scene(editorLoader.load());
        editorStage = new Stage();
        editorStage.setScene(editorScene);
        editorStage.initModality(Modality.APPLICATION_MODAL);

        viewScene = new Scene(viewLoader.load());
        viewStage = new Stage();
        viewStage.setScene(viewScene);
        viewStage.initModality(Modality.APPLICATION_MODAL);


        chatScene = new Scene(chatLoader.load());
        loginScene = new Scene(loginLoader.load());

        chatController = chatLoader.getController();
        loginController = loginLoader.getController();
        dataUpdateController = editorLoader.getController();
        dataViewController = viewLoader.getController();

        chatController.setApplication(this);
        loginController.setApplication(this);
        dataUpdateController.setApplication(this);
        dataViewController.setApplication(this);


        stage.setOnCloseRequest(e -> System.exit(0));

        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public void showEditModal(){
        editorStage.showAndWait();
    }

    public void closeEditModal() {
        editorStage.close();
    }

    public void showViewModal(ResponseUserInfo userInfo) {
        dataViewController.viewUserInfo(userInfo);
        Platform.runLater(()->{
            viewStage.showAndWait();
        });
    }

    public void closeViewModal() {
        viewStage.close();
    }

    public void connect(String username, String host, int port) {
        try {
            client = Client.connect(host, port);
        } catch (RuntimeException e) {
            System.out.println("Unable to connect to the server");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Connection");
            alert.setContentText("Unable to connect to the server");
            alert.showAndWait();
        }
        if(client != null) {
            this.username = username;
            applicationStage.setTitle("Client - " + username);
            applicationStage.setScene(chatScene);

            client.subscribe(message -> chatController.onMessageReceived(message));
            System.out.println("Connection to the server.");
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