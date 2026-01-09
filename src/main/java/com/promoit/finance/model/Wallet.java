package com.promoit.finance.model;

import java.util.*;

public class Wallet {
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Budget> budgets = new HashMap<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void setBudget(Budget budget) {
        budgets.put(budget.getCategory(), budget);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public Map<String, Budget> getBudgets() {
        return new HashMap<>(budgets);
    }
}