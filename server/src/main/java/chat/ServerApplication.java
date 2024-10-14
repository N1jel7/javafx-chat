package chat;

import by.chat.SocketTemplate;
import chat.dao.SimpleConnectionProvider;
import chat.dao.UserDao;
import chat.dao.UserDaoImpl;
import chat.service.AuthServiceImpl;
import chat.service.RequestHandlerImpl;
import chat.service.ServerCore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ServerApplication extends Application {

    private SocketTemplate socketTemplate;
    private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh");
        System.setProperty("currenttime", dateFormat.format(new Date()));
    }

    @Override
    public void start(Stage stage) throws IOException {
        log.info(readBanner("banner.txt"));
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("/chat/server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            log.info("Server closed");
            System.exit(0);
        });
        ServerCore server = ServerCore.getInstance();
        Properties properties = new Properties();
        String propertiesPath = Path.of("chat",  "db.properties").toString();
        InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(propertiesPath);
        properties.load(propertiesStream);
        UserDao userDao = new UserDaoImpl(new SimpleConnectionProvider(properties));
        server.setRequestHandler(new RequestHandlerImpl(userDao, new AuthServiceImpl(userDao)));
        server.start(8080);
        stage.show();
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        launch();
    }

    private static String readBanner(String path) {
        try {
            return "\n\n\n" +
                    Files.readString(Path.of(ServerApplication.class.getResource(path)
                            .getPath().substring(1))) +
                    "\n\n\n";

        } catch (IOException e) {
            return "";
        }
    }
}