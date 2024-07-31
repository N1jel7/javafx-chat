package chat.javafx.client.ui;

import chat.javafx.client.ClientApplication;
import javafx.fxml.Initializable;

public abstract class AbstractController implements Initializable {
    protected ClientApplication application;

    public void setApplication(ClientApplication application) {
        this.application = application;
    }
}
