package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import chat.javafx.message.ResponseUserInfo;
import chat.javafx.message.UpdateUserInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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


    public void viewUserInfo(ResponseUserInfo responseUserInfo) {
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

