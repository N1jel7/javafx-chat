package chat.javafx.server.dao;

import chat.javafx.message.UpdateUserInfo;

import java.sql.*;
import java.time.Instant;
import java.util.function.Supplier;

public class UserDaoImpl implements UserDao{
    private final Supplier<Connection> connectionProvider;

    public UserDaoImpl(Supplier<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public UserDto findUserByLogin(String login) {
        try (Connection connection = connectionProvider.get()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT login, online, first_name, last_name, birthday FROM users WHERE login = ?;");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                return null;
            }
            return new UserDto(resultSet.getString("login"), resultSet.getBoolean("online"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate());
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
            preparedStatement = connection.prepareStatement("UPDATE `users`SET first_name = ?, last_name = ?, birthday = ?) WHERE login = ?");
            preparedStatement.setString(1, userDto.getFirstname());
            preparedStatement.setString(2, userDto.getLastname());
            preparedStatement.setString(3, String.valueOf(userDto.getBirthday().atStartOfDay()));
            preparedStatement.setString(4, userDto.getLogin());
        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }

    }
}
