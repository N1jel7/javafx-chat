package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import chat.javafx.client.service.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    private static String host = "localhost";
    private static int port = 8080;

    @FXML
    private Button button_send;

    @FXML
    private TextField tf_message;

    @FXML
    private ScrollPane mainSp;

    @FXML
    private AnchorPane mainAp;

    @FXML
    private VBox vboxMessages;

    private Client client;

    private ClientApplication application;

    public void setApplication(ClientApplication application){
        this.application = application;
    }

    private void sendMessage() {
        String messageToSend = tf_message.getText();
        if (!messageToSend.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(messageToSend);
            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle("-fx-color: rgb(239,242,255);"
                    + "-fx-background-color: rgb(15,125,242);"
                    + " -fx-background-radius: 20px");

            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.934, 0.945, 0.966));

            hBox.getChildren().add(textFlow);
            vboxMessages.getChildren().add(hBox);

            client.sendMessageToServer(messageToSend);
            tf_message.clear();
        }
    }

    public void connect(String host, int port) {
        try {
            client = new Client(new Socket(host, port));
            client.receiveMessageFromServer(vboxMessages);
            System.out.println("Connection to the server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tf_message.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)){
                sendMessage();
            }
        });

        button_send.setOnAction(e -> {
            sendMessage();
        });

        mainSp.vvalueProperty().bind(vboxMessages.heightProperty());

        vboxMessages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mainAp.setMinHeight((double) newValue);
            }
        });
    }

    public static void AddLabel(String messageFromServer, VBox vBox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromServer);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                " -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }

}
