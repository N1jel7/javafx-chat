package chat.javafx.client.ui;

import chat.javafx.message.response.UserInfoResponse;
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


    public void viewUserInfo(UserInfoResponse userInfoResponse) {
        byte[] avatar = userInfoResponse.getAvatar();
        if(avatar != null && avatar.length > 0){
            avatarView.setImage(new Image(new ByteArrayInputStream(avatar)));
        }
        usernameField.setText(userInfoResponse.getLogin());
        onlineField.setText(userInfoResponse.isOnline() ? "Online" : "Offline");
        firstnameField.setText(userInfoResponse.getFirstname() == null ? "-" : userInfoResponse.getFirstname());
        lastnameField.setText(userInfoResponse.getLastname() == null ? "-" : userInfoResponse.getLastname());
        birthdayField.setText(userInfoResponse.getBirthday() == null ? "-" : userInfoResponse.getBirthday().toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeButton.setOnAction(e -> {
            application.closeStage(MODAL);
        });

    }
}

