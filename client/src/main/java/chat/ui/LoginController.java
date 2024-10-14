package chat.ui;

import chat.service.ClientPropertiesHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static chat.ClientApplication.ResourceType.REGISTER;
import static chat.ClientApplication.StageType.MAIN;

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

    public void setHostname(String hostname) {
        hostField.setText(hostname);
    }

    public void setPort(String port) {
        portField.setText(port);
    }

    private void connect() {
        if(usernameField.getText().isBlank() || hostField.getText().isBlank() || portField.getText().isBlank() || passField.getText().isBlank()) {
            AlertUtil.showWarning( "Warning", "There are some empty fields!");
        } else {
            username = usernameField.getText();
            pass = passField.getText();
            host = hostField.getText();
            port = portField.getText();
            application.connect(host, Integer.parseInt(port));
            application.authorize(username, pass);


            Properties properties = ClientPropertiesHolder.getProperties();
            properties.setProperty("hostname", hostField.getText());
            properties.setProperty("port", portField.getText());
            properties.setProperty("username", usernameField.getText());
            ClientPropertiesHolder.storeProperties();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Properties properties = ClientPropertiesHolder.getProperties();
        String storedHostname = properties.getProperty("hostname");
        String storedPort = properties.getProperty("port");
        String storedUsername = properties.getProperty("username");
        if(storedUsername != null && storedPort != null && storedHostname != null){
            setUsername(storedUsername);
            setPort(storedPort);
            setHostname(storedHostname);
        }

        passField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                connect();
            }
        });

        registerButton.setOnMouseClicked(e -> {
            application.showResource(REGISTER, MAIN);
        });

        connectButton.setOnMouseClicked(e -> {
            connect();
        });
    }
}
