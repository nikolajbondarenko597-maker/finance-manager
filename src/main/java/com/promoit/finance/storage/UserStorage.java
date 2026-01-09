package com.promoit.finance.storage;

import com.promoit.finance.model.User;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private final Map<String, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getLogin(), user);
    }

    public User getUser(String login) {
        return users.get(login);
    }

    public boolean userExists(String login) {
        return users.containsKey(login);
    }
}