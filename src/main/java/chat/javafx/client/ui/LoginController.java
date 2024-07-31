package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static chat.javafx.client.ClientApplication.ResourceType.REGISTER;
import static chat.javafx.client.ClientApplication.StageType.MAIN;

public class LoginController extends AbstractController {

    @FXML
    private TextField usernameField, hostField, portField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button connectButton, registerButton;

    private String username, pass, host, port;

    public void setUsername(String username) {
        usernameField.setText(username);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerButton.setOnMouseClicked(e -> {
            application.showResource(REGISTER, MAIN);
        });

        connectButton.setOnMouseClicked(e -> {
            if(usernameField.getText().isBlank() || hostField.getText().isBlank() || portField.getText().isBlank() || passField.getText().isBlank()) {
                AlertUtil.createAlert(Alert.AlertType.WARNING, "Warning", "There are some empty fields!");
            } else {
                username = usernameField.getText();
                pass = passField.getText();
                host = hostField.getText();
                port = portField.getText();
                application.connect(host, Integer.parseInt(port));
                application.authorize(username, pass);
            }
        });
    }
}
