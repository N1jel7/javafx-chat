package chat.javafx.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static chat.javafx.client.ClientApplication.ResourceType.LOGIN;
import static chat.javafx.client.ClientApplication.StageType.MAIN;

public class RegisterController extends AbstractController {

    @FXML
    private PasswordField passField;

    @FXML
    private Button registerButton, loginButton;

    @FXML
    private TextField usernameField, addressField, portField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnMouseClicked(e -> {
            application.showResource(LOGIN, MAIN);
        });

        registerButton.setOnMouseClicked(e ->
        {
            if (!usernameField.getText().isBlank() && !passField.getText().isBlank() && !addressField.getText().isBlank() && !portField.getText().isBlank()) {

                application.register(usernameField.getText(), passField.getText(), addressField.getText(), Integer.parseInt(portField.getText()));


            } else {
                AlertUtil.createAlert(Alert.AlertType.WARNING, "Error", "There are some empty fields!");
            }

        });

    }
}
