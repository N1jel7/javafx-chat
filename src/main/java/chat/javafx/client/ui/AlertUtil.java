package chat.javafx.client.ui;

import javafx.scene.control.Alert;

public class AlertUtil {

    public static void createAlert(Alert.AlertType alertType, String headerText, String contextText) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.showAndWait();
    }

}
