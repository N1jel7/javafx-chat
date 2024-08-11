package chat.javafx.client.ui;

import chat.javafx.client.ui.dto.ClientCache;
import chat.javafx.message.ChatMessage;
import chat.javafx.message.RequestUserInfo;
import chat.javafx.message.ResponseUserInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;

import static chat.javafx.client.ClientApplication.ResourceType.EDITOR;
import static chat.javafx.client.ClientApplication.StageType.MODAL;

public class ChatController extends AbstractController {
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

    private Map<String, List<ImageView>> userImageViews = new HashMap<>();

    public void setUserImage(String username, Image image) {
        List<ImageView> imageViews = userImageViews.getOrDefault(username, Collections.emptyList());
        for (ImageView imageView : imageViews) {
            imageView.setImage(image);
        }
    }

    private void addUserImageView(String username, ImageView imageView) {
        List<ImageView> imageViews = userImageViews.getOrDefault(username, new ArrayList<>());
        imageViews.add(imageView);
        userImageViews.put(username, imageViews);
    }

    private void addOwnMessage() {
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tf_message.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                addOwnMessage();
            }
        });

        button_send.setOnAction(e -> {
            addOwnMessage();
        });

        editButton.setOnAction(e -> {
            application.showResource(EDITOR, MODAL);
        });

        mainSp.vvalueProperty().bind(vboxMessages.heightProperty());

        vboxMessages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mainAp.setMinHeight((double) newValue);
            }
        });
    }

    public void addReceivedMessage(ChatMessage chatMessage) {

        String sender = chatMessage.getSender();
        ResponseUserInfo cachedInfo = ClientCache.getInstance().findUserInfo(sender);
        Image image;
        if (cachedInfo == null) {
            image = new Image("chat/javafx/chat/images/user_avatar.png");
            application.sendMessageToServer(new RequestUserInfo(sender));
        } else {
            image = new Image(new ByteArrayInputStream(cachedInfo.getAvatar()));
        }
        ImageView avatar = new ImageView();
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        avatar.setImage(image);

        Text username = new Text(sender + ":");
        username.setOnMouseClicked(e -> {
            application.sendMessageToServer(new RequestUserInfo(sender));
            application.setDisplayNextUserResponse(true);
        });
        HBox container = new HBox(avatar, username);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 0, 0, 1));
        container.setSpacing(5);

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
                vboxMessages.getChildren().add(container);
            }
        });
    }

}
