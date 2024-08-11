package chat.javafx.client.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ClientPropertiesHolder {
    private static final String CONFIG_FILENAME = "client.config";
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties(){
        try {
            Path filepath = Path.of(CONFIG_FILENAME);
            if(!Files.exists(filepath)){
                Files.createFile(filepath);
            }

            properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILENAME));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getProperties(){
        return properties;
    }

    public static void storeProperties(){
        try {
            properties.store(new FileOutputStream(CONFIG_FILENAME), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
