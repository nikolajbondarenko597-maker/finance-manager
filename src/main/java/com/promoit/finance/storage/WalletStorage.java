package com.promoit.finance.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.promoit.finance.model.Wallet;

import java.io.File;
import java.io.IOException;

public class WalletStorage {
    private static final String DATA_DIR = "data";
    private final ObjectMapper mapper = new ObjectMapper();

    public WalletStorage() {
        mapper.registerModule(new JavaTimeModule());
    }

    public void saveWallet(String login, Wallet wallet) {
        try {
            new File(DATA_DIR).mkdirs();
            mapper.writeValue(new File(DATA_DIR + "/" + login + ".json"), wallet);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить данные пользователя " + login, e);
        }
    }

    public Wallet loadWallet(String login) {
        try {
            File file = new File(DATA_DIR + "/" + login + ".json");
            if (!file.exists()) {
                return new Wallet();
            }
            return mapper.readValue(file, Wallet.class);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить данные пользователя " + login, e);
        }
    }
}