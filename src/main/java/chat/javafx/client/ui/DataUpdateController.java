package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import chat.javafx.message.UpdateUserInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DataUpdateController extends AbstractController {

    @FXML
    private DatePicker birthdayField;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private Button resetButton, saveButton, uploadAvatarButton;

    private byte[] image;

    private void updateData(byte[] image) {
        if(image != null && birthdayField.getValue() != null && !firstnameField.getText().isBlank() && !lastnameField.getText().isBlank()) {
                    application.sendMessageToServer(new UpdateUserInfo(image, firstnameField.getText(), lastnameField.getText(), birthdayField.getValue()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        uploadAvatarButton.setOnMouseClicked(e -> {
             image = application.showFilechooser();
        });

        saveButton.setOnAction(e -> {
            updateData(image);
            application.closeStage(ClientApplication.StageType.MODAL);
        });

        resetButton.setOnAction(e -> {
            firstnameField.setText("");
            lastnameField.setText("");
            birthdayField.setValue(LocalDate.now());
        });

    }
}
