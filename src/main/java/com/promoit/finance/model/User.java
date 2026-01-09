package com.promoit.finance.model;

public class User {
    private final String login;
    private final String password;
    private final Wallet wallet;

    public User(String login, String password) {
        if (login == null || login.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин и пароль не могут быть пустыми");
        }
        this.login = login.trim();
        this.password = password;
        this.wallet = new Wallet();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Wallet getWallet() {
        return wallet;
    }
}