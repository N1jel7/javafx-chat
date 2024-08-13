package chat.javafx.server;

import chat.javafx.server.dao.SimpleConnectionProvider;
import chat.javafx.server.dao.UserDao;
import chat.javafx.server.dao.UserDaoImpl;
import chat.javafx.server.service.AuthServiceImpl;
import chat.javafx.server.service.RequestHandlerImpl;
import chat.javafx.server.service.ServerCore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("/chat/javafx/server/server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        ServerCore server = ServerCore.getInstance();
        Properties properties = new Properties();
        String propertiesPath = Path.of("chat", "javafx", "server", "db.properties").toString();
        InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(propertiesPath);
        properties.load(propertiesStream);
        UserDao userDao = new UserDaoImpl(new SimpleConnectionProvider(properties));
        server.setRequestHandler(new RequestHandlerImpl(userDao, new AuthServiceImpl(userDao)));
        server.start(8080);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}