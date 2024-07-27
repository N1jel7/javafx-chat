package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import chat.javafx.message.AbstractMessage;
import chat.javafx.message.ChatMessage;
import chat.javafx.message.MessageType;
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

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
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

    @FXML
    private Button editButton;


    private ClientApplication application;

    public void setApplication(ClientApplication application) {
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

            application.sendMessageToServer(new ChatMessage(messageToSend));
            tf_message.clear();
        }
    }

    public void onMessageReceived(AbstractMessage message) {
        if (message.getType().equals(MessageType.CHAT_MESSAGE)) {
            ChatMessage chatMessage = (ChatMessage) message;
            addLabel(chatMessage, vboxMessages);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tf_message.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                sendMessage();
            }
        });

        button_send.setOnAction(e -> {
            sendMessage();
        });

        editButton.setOnAction(e -> {
            application.showEditModal();
        });

        mainSp.vvalueProperty().bind(vboxMessages.heightProperty());

        vboxMessages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mainAp.setMinHeight((double) newValue);
            }
        });
    }

    private static void addLabel(ChatMessage chatMessage, VBox vBox) {
        Text username = new Text(chatMessage.getSender() + ": ");
        HBox container = new HBox(username);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 0, 0, 10));

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));


        Text content = new Text(chatMessage.getText());
        TextFlow textFlow = new TextFlow(content);

        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                " -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);
        container.getChildren().add(hBox);


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(container);
            }
        });
    }

}
