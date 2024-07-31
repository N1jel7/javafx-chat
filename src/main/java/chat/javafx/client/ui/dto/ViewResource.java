package chat.javafx.client.ui.dto;

import chat.javafx.client.ui.AbstractController;
import javafx.scene.Scene;

public class ViewResource {
    private AbstractController controller;
    private Scene scene;

    public ViewResource(chat.javafx.client.ui.AbstractController controller, Scene scene) {
        this.controller = controller;
        this.scene = scene;
    }

    public AbstractController getController() {
        return controller;
    }

    public void setController(chat.javafx.client.ui.AbstractController controller) {
        this.controller = controller;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

}
