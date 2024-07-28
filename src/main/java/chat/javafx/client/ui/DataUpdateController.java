package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import chat.javafx.message.UpdateUserInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DataUpdateController implements Initializable {

    private ClientApplication application;

    @FXML
    private DatePicker birthdayField;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private Button resetButton;

    @FXML
    private Button saveButton;



    private void updateData() {
        if(birthdayField.getValue() != null && !firstnameField.getText().isBlank() && !lastnameField.getText().isBlank()) {
                    application.sendMessageToServer(new UpdateUserInfo(firstnameField.getText(), lastnameField.getText(), birthdayField.getValue()));
        }
    }

    public void setApplication(ClientApplication application) {
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        saveButton.setOnAction(e -> {

            updateData();
            application.closeEditModal();

        });

    }
}
