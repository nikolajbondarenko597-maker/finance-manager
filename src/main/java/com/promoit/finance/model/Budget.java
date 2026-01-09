package com.promoit.finance.model;

public class Budget {
    private final String category;
    private final double limit;

    public Budget(String category, double limit) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит должен быть положительным");
        }
        this.category = category.trim();
        this.limit = limit;
    }

    public String getCategory() {
        return category;
    }

    public double getLimit() {
        return limit;
    }
}