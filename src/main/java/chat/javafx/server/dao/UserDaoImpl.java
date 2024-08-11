package chat.javafx.server.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.function.Supplier;

public class UserDaoImpl implements UserDao {
    private final Supplier<Connection> connectionProvider;

    public UserDaoImpl(Supplier<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public UserDto findUserByLogin(String login) {
        try (Connection connection = connectionProvider.get()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT avatar, login, online, first_name, last_name, birthday FROM users WHERE login = ?;");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return new UserDto(resultSet.getBytes("avatar"), resultSet.getString("login"), resultSet.getBoolean("online"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUserData(UserDto userDto) {
        try (Connection connection = connectionProvider.get()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `users`(login, online, first_name, last_name, birthday) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, userDto.getLogin());
            preparedStatement.setBoolean(2, userDto.isOnline());
            preparedStatement.setString(3, userDto.getFirstname());
            preparedStatement.setString(4, userDto.getLastname());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(userDto.getBirthday().atStartOfDay()));
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUserData(UserDto userDto) {
        try (Connection connection = connectionProvider.get()) {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("UPDATE `users` SET avatar = ?, first_name = ?, last_name = ?, birthday = ? WHERE login = ?");
            preparedStatement.setBlob(1, new ByteArrayInputStream(userDto.getAvatar()));
            preparedStatement.setString(2, userDto.getFirstname());
            preparedStatement.setString(3, userDto.getLastname());
            preparedStatement.setString(4, String.valueOf(userDto.getBirthday().atStartOfDay()));
            preparedStatement.setString(5, userDto.getLogin());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String findUserHashByLogin(String login) {
        try (Connection connection = connectionProvider.get()) {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("SELECT pass FROM `users` WHERE login = ?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            } else {
                return resultSet.getString("pass");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerUser(String login, String password) {
        try (Connection connection = connectionProvider.get()) {
            InputStream imageInputStream = getClass().getClassLoader().getResourceAsStream("chat/javafx/server/images/user_avatar.png");
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("INSERT INTO `users`(login, pass, avatar) VALUES (?, ?, ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setBlob(3, imageInputStream);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
