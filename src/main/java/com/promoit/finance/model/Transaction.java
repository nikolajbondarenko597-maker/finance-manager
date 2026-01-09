package com.promoit.finance.model;

import java.time.LocalDateTime;

public class Transaction {
    private final String category;
    private final double amount;
    private final TransactionType type;
    private final LocalDateTime timestamp;

    public Transaction(String category, double amount, TransactionType type) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        this.category = category.trim();
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}