package com.promoit.finance.service;

import com.promoit.finance.exception.ValidationException;
import com.promoit.finance.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class FinanceService {

    public void addIncome(Wallet wallet, String category, double amount) {
        validateInput(category, amount);
        wallet.addTransaction(new Transaction(category, amount, TransactionType.INCOME));
    }

    public void addExpense(Wallet wallet, String category, double amount) {
        validateInput(category, amount);
        wallet.addTransaction(new Transaction(category, amount, TransactionType.EXPENSE));
    }

    public void setBudget(Wallet wallet, String category, double limit) {
        validateInput(category, limit);
        wallet.setBudget(new Budget(category, limit));
    }

    private void validateInput(String category, double value) {
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Категория не может быть пустой");
        }
        if (value <= 0) {
            throw new ValidationException("Сумма или лимит должны быть положительными");
        }
    }

    public double getTotalIncome(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenses(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getIncomeByCategory(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public Map<String, Double> getExpensesByCategory(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public Map<String, Double> getRemainingBudgets(Wallet wallet) {
        Map<String, Double> expenses = getExpensesByCategory(wallet);
        Map<String, Double> remaining = new HashMap<>();
        for (Map.Entry<String, Budget> entry : wallet.getBudgets().entrySet()) {
            String category = entry.getKey();
            double limit = entry.getValue().getLimit();
            double spent = expenses.getOrDefault(category, 0.0);
            remaining.put(category, limit - spent);
        }
        return remaining;
    }
}