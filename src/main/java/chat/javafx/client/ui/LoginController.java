package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField, hostField, portField;

    @FXML
    private Button connectButton;

    private String username, host, port;

    private ClientApplication application;

    public void setApplication(ClientApplication application) {
        this.application = application;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectButton.setOnMouseClicked(e -> {
            if(usernameField.getText().isBlank() || hostField.getText().isBlank() || portField.getText().isBlank()) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Empty fields!");
                alert.setContentText("Fill empty fields.");
                alert.showAndWait();

            } else {
                username = usernameField.getText();
                host = hostField.getText();
                port = portField.getText();
                application.connect(username, host, Integer.parseInt(port));
            }
        });
    }
}
