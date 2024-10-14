package chat.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ServerController.class);

    @FXML
    private Button stopButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        stopButton.setOnAction(e -> {
            log.info("Server closed");
            System.exit(0);
        });
    }


}