package chat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ClientPropertiesHolder {

    private static final Logger log = LoggerFactory.getLogger(ClientPropertiesHolder.class);
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
            log.info("Properties file loaded successfully.");
        } catch (IOException e) {
            log.warn("Error while loading properties file", e);
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
            log.warn("Error while saving properties file", e);
            throw new RuntimeException(e);
        }
    }
}
