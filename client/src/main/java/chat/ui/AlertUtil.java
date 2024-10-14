package chat.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertUtil {
    public static void showInfo(String header, String context) {
        createAlert(Alert.AlertType.INFORMATION, header, context);
    }

    public static void showWarning(String header, String context) {
        createAlert(Alert.AlertType.WARNING, header, context);
    }

    private static void createAlert(Alert.AlertType alertType, String headerText, String contextText) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.showAndWait();
    }

}
