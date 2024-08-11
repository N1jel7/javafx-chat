package chat.javafx.server.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import chat.javafx.message.*;
import chat.javafx.server.dao.UserDao;
import chat.javafx.server.dao.UserDto;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable {
    private ServerCore server;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private UserDao userDao;
    private boolean authorized;
    private static List<String> users = new ArrayList<>();
    private String username;
    private BCrypt.Hasher hasher;

    public ServerThread(ServerCore server, Socket socket, UserDao userDao) {
        try {
            this.userDao = userDao;
            this.server = server;
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error creating server.");
            e.printStackTrace();
            closeConnection();
        }

    }

    public void sendMessageToClient(AbstractMessage message) {
        try {
            objectOutputStream.writeObject(message);

        } catch (IOException e) {
            System.out.println("Error sending message to the client");
            e.printStackTrace();
            closeConnection();
        }

    }



    private void closeConnection() {
        try {
            if(socket != null) {
                socket.close();
            }
            if(objectInputStream != null) {
                objectInputStream.close();
            }
            if(objectOutputStream != null) {
                objectOutputStream.close();
            }

            users.remove(username);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessageFromClient(AbstractMessage message) {
        if (authorized) {
            switch (message.getType()) {
                case CHAT_MESSAGE -> {
                    ChatMessage chatMessage = (ChatMessage) message;
                    server.sendMessageToAllClients(this, chatMessage);
                }
                case USER_DATA_UPDATE -> {
                    UpdateUserInfo updateUserInfo = (UpdateUserInfo) message;

                    UserDto userDto = new UserDto(updateUserInfo.getAvatar(), updateUserInfo.getSender(), true, updateUserInfo.getFirstname(), updateUserInfo.getLastname(), updateUserInfo.getBirthday());

                    if (userDao.findUserByLogin(updateUserInfo.getSender()) == null) {
                        userDao.saveUserData(userDto);
                    } else {
                        userDao.updateUserData(userDto);
                    }

                }
                case USER_DATA_REQUEST -> {
                    RequestUserInfo requestUserInfo = (RequestUserInfo) message;
                    String login = requestUserInfo.getLogin();
                    UserDto user = userDao.findUserByLogin(login);
                    if (user == null) {
                        ResponseUserInfo responseUserInfo = new ResponseUserInfo(new byte[0], login, false, null, null, null);
                        System.out.println("Data of user " + login + " not found!");
                        sendMessageToClient(responseUserInfo);
                    } else {
                        ResponseUserInfo responseUserInfo = new ResponseUserInfo(user.getAvatar(), user.getLogin(), user.isOnline(), user.getFirstname(), user.getLastname(), user.getBirthday());
                        sendMessageToClient(responseUserInfo);
                    }
                }

                default -> {
                    System.out.println("Unknown message type: " + message.getType());
                }
            }
        } else if (message.getType().equals(MessageType.AUTH_REQUEST)) {
            RequestAuth requestAuth = (RequestAuth) message;
            String hash = userDao.findUserHashByLogin(message.getSender());

            if (hash != null) {
                boolean verified = BCrypt.verifyer().verify(requestAuth.getPass().toCharArray(), hash).verified;
                if (verified && !users.contains(requestAuth.getSender())) {
                    System.out.println(message.getSender() + " connected to the server.");
                    users.add(requestAuth.getSender());
                    authorized = true;
                    username = requestAuth.getSender();
                }
            }
            ResponseAuthInfo authResponse = new ResponseAuthInfo(authorized);
            authResponse.setSender(message.getSender());
            sendMessageToClient(authResponse);
        } else {
            hasher = BCrypt.withDefaults();
            RegistrationRequest registrationRequest = (RegistrationRequest) message;
            String hash = hasher.hashToString(10, registrationRequest.getPass().toCharArray());

            if (userDao.findUserByLogin(registrationRequest.getLogin()) == null) {

                userDao.registerUser(registrationRequest.getLogin(), hash);
                sendMessageToClient(new RegistrationResponse(true));

            } else {
                sendMessageToClient(new RegistrationResponse(false));
            }

            System.out.println("Server had received message with type: " + message.getType());

        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                receiveMessageFromClient((AbstractMessage) objectInputStream.readObject());

            } catch (Exception e) {
                System.out.println("Error receiving message from client.");
                e.printStackTrace();
                closeConnection();
                break;
            }
        }
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
