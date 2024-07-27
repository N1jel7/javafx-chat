package chat.javafx.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public class SimpleConnectionProvider implements Supplier<Connection> {

    private static final String address = "jdbc:mysql://localhost:3306/chat-app";
    private static final String login = "root";
    private static final String password = "31102012";

    @Override
    public Connection get() {
        try {
            return DriverManager.getConnection(address, login, password);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create new connection", e);
        }
    }
}
