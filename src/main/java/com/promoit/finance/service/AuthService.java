package com.promoit.finance.service;

import com.promoit.finance.exception.ValidationException;
import com.promoit.finance.model.User;
import com.promoit.finance.storage.UserStorage;
import com.promoit.finance.storage.WalletStorage;

public class AuthService {
    private final UserStorage userStorage = new UserStorage();
    private final WalletStorage walletStorage = new WalletStorage();

    public User register(String login, String password) {
        if (login == null || login.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("Логин и пароль не могут быть пустыми");
        }
        if (userStorage.userExists(login)) {
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
        User user = new User(login, password);
        userStorage.addUser(user);
        return user;
    }

    public User login(String login, String password) {
        User user = userStorage.getUser(login);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ValidationException("Неверный логин или пароль");
        }
        // Загружаем кошелёк из файла
        user.getWallet().getTransactions().clear();
        user.getWallet().getBudgets().clear();
        var loadedWallet = walletStorage.loadWallet(login);
        user.getWallet().getTransactions().addAll(loadedWallet.getTransactions());
        user.getWallet().getBudgets().putAll(loadedWallet.getBudgets());
        return user;
    }

    public void logoutAndSave(User user) {
        walletStorage.saveWallet(user.getLogin(), user.getWallet());
    }
}