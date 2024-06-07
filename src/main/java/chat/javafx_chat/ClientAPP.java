package chat.javafx_chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ClientAPP extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientAPP.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Client");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if(args.length == 2){
            String host = Objects.isNull(args[0]) ? "localhost" : args[0];
            int port = Objects.isNull(args[1]) ? 8080 : Integer.parseInt(args[1]);
            ClientController.setHostAndPort(host, port);
        }
        launch();
    }
}