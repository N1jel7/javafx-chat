package chat.ui.dto;

import chat.ui.AbstractController;
import javafx.scene.Scene;

public class ViewResource {
    private AbstractController controller;
    private Scene scene;

    public ViewResource(AbstractController controller, Scene scene) {
        this.controller = controller;
        this.scene = scene;
    }

    public AbstractController getController() {
        return controller;
    }

    public void setController(AbstractController controller) {
        this.controller = controller;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

}
