package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import chat.javafx.message.UpdateUserInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DataUpdateController {

    ClientApplication clientApplication;

    @FXML
    private TextField birthdayField;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private Button resetButton;

    @FXML
    private Button saveButton;

    private void updateData() {
        if(!birthdayField.getText().isBlank() && !firstnameField.getText().isBlank() && !lastnameField.getText().isBlank()) {
            clientApplication.sendMessageToServer(new UpdateUserInfo(firstnameField.getText(), lastnameField.getText(), LocalDate.parse(birthdayField.getText())));
        }
    }



}
