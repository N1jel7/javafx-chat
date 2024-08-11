package chat.javafx.client.ui;

import chat.javafx.message.ResponseUserInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static chat.javafx.client.ClientApplication.StageType.MODAL;

public class DataViewController extends AbstractController {
    @FXML
    private TextField birthdayField;

    @FXML
    private Button closeButton;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField onlineField;

    @FXML
    private TextField usernameField;

    @FXML
    private ImageView avatarView;


    public void viewUserInfo(ResponseUserInfo responseUserInfo) {
        byte[] avatar = responseUserInfo.getAvatar();
        if(avatar != null && avatar.length > 0){
            avatarView.setImage(new Image(new ByteArrayInputStream(avatar)));
        }
        usernameField.setText(responseUserInfo.getLogin());
        onlineField.setText(responseUserInfo.isOnline() ? "Online" : "Offline");
        firstnameField.setText(responseUserInfo.getFirstname() == null ? "-" : responseUserInfo.getFirstname());
        lastnameField.setText(responseUserInfo.getLastname() == null ? "-" : responseUserInfo.getLastname());
        birthdayField.setText(responseUserInfo.getBirthday() == null ? "-" : responseUserInfo.getBirthday().toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeButton.setOnAction(e -> {
            application.closeStage(MODAL);
        });

    }
}

