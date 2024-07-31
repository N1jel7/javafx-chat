package chat.javafx.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

import static chat.javafx.client.ClientApplication.ResourceType.LOGIN;
import static chat.javafx.client.ClientApplication.ResourceType.REGISTER;
import static chat.javafx.client.ClientApplication.StageType.MAIN;

public class WelcomeController extends AbstractController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(e -> {
            application.showResource(LOGIN, MAIN);
        });

        registerButton.setOnAction(e -> {
            application.showResource(REGISTER, MAIN);
        });
    }
}
