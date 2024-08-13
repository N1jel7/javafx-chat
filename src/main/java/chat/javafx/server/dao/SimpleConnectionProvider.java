package chat.javafx.server.dao;

import chat.javafx.server.service.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Supplier;

public class SimpleConnectionProvider implements Supplier<Connection> {

    private static final Logger log = LoggerFactory.getLogger(SimpleConnectionProvider.class);
    private final Properties dbProperties;

    public SimpleConnectionProvider(Properties dbProperties) {
        this.dbProperties = dbProperties;
    }

    @Override
    public Connection get() {
        try {
            return DriverManager.getConnection(
                    dbProperties.getProperty("jdbc.connectionUrl"),
                    dbProperties.getProperty("jdbc.username"),
                    dbProperties.getProperty("jdbc.password")
            );
        } catch (SQLException e) {
            log.warn("Can't create new connection", e);
            throw new RuntimeException("Can't create new connection", e);
        }
    }
}
