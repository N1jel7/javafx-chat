package chat.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import chat.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserDao userDao;
    private final BCrypt.Hasher hasher;
    private final List<String> users;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        this.hasher = BCrypt.withDefaults();
        this.users = new ArrayList<>();
    }

    @Override
    public boolean isAuthorized(String login) {
        return users.contains(login);
    }

    @Override
    public boolean authorize(String login, String password) {
        String hash = userDao.findUserHashByLogin(login);

        if (hash != null) {
            boolean verified = BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
            if (verified && !users.contains(login)) {
                log.info("User \"{}\" authorized successfully", login);
                users.add(login);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean register(String login, String password) {
        String hash = hasher.hashToString(10, password.toCharArray());

        if (userDao.findUserByLogin(login) == null) {
            userDao.registerUser(login, hash);
            log.info("User \"{}\" registered successfully", login);
            return true;
        }
        return false;
    }

    @Override
    public void logout(String login) {
        users.remove(login);
        log.info("User \"{}\" logout successfully", login);
    }
}
