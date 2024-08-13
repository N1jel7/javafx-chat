package chat.javafx.client.ui.dto;

import chat.javafx.message.response.UserInfoResponse;

import java.util.HashMap;
import java.util.Map;

public class ClientCache {
    private Map<String, UserInfoResponse> users;

    private ClientCache(){
        users = new HashMap<>();
    }

    private static ClientCache clientCache;

    static {
        loadClientCache();
    }

    private static void loadClientCache() {
        clientCache = new ClientCache();
    }

    public static ClientCache getInstance() {
        return clientCache;
    }

    public void add(UserInfoResponse infoResponse){
        users.put(infoResponse.getLogin(), infoResponse);
    }

    public UserInfoResponse findUserInfo(String login){
        return users.get(login);
    }

}
